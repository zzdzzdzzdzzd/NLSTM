package com.zzd.model.nLstmNetwork;

import java.util.HashMap;
import java.util.Map;

import com.zzd.model.nLstmState.TreeLstmState;
import com.zzd.model.utils.CommonConfigure;
import com.zzd.model.utils.LossFunction;
import com.zzd.model.utils.Matrix;
import com.zzd.model.utils.ModelOperatorUtil;
import com.zzd.model.weight.TreeDWeight;
import com.zzd.model.weight.TreeWeight;

public class TreeLstmNetwork {

	Matrix[] trainXs;

	Matrix[] trainYs;

	Matrix[] validateXs;

	Matrix[] validateYs;

	TreeWeight weight;

	double[] losses;

	TreeWeight weightMinLoss;

	double lossMin;
	/**
	 * number of nodes in the equivalent tree causality
	 */
	int caseNum = CommonConfigure.caseNum;
	/**
	 * flag index of VNP (variable need to be predicted)
	 */
	int mainFlag = CommonConfigure.mainFlag;
	/**
	 * child nodes
	 */
	int[][] preLayer;
	/**
	 * parent node
	 */
	int[] nextLayer;
	/**
	 * nodes in each layer
	 */
	int[][] caseRecord;
	/**
	 * index of each node in parent node's child nodes
	 */
	int[] indexInNext;
	/**
	 * epoches
	 */
	int epochs;
	/**
	 * size of samples
	 */
	int sampleNum;
	/**
	 * learning rate
	 */
	double lr;

	public TreeLstmNetwork(int[][] preLayer, int[] nextLayer, int[][] caseRecord, int[] indexInNext, int epochs,
			double lr) {
		this.preLayer = preLayer;
		this.nextLayer = nextLayer;
		this.caseRecord = caseRecord;
		this.indexInNext = indexInNext;
		this.epochs = epochs;
		this.lr = lr;

		trainXs = new Matrix[caseNum];
		trainYs = new Matrix[caseNum];
		validateXs = new Matrix[caseNum];
		validateYs = new Matrix[caseNum];
		weight = new TreeWeight(preLayer);
		losses = new double[epochs];
		weightMinLoss = this.weight;
		lossMin = Double.POSITIVE_INFINITY;
	}

	public void addCase(int flag, Matrix trainX, Matrix trainY) {
		trainXs[flag] = trainX;
		trainYs[flag] = trainY;
		if (flag == mainFlag) {
			this.sampleNum = trainX.getRowDimension();
		}
	}

	public void addCase(int flag, Matrix trainX, Matrix trainY, Matrix validateX, Matrix validateY) {
		trainXs[flag] = trainX;
		trainYs[flag] = trainY;
		validateXs[flag] = validateX;
		validateYs[flag] = validateY;
		if (flag == mainFlag) {
			this.sampleNum = trainX.getRowDimension();
		}
	}

	public void train(int batchSize) {
		double startTime = System.currentTimeMillis();

		for (int i = 0; i < epochs; i++) {
			Matrix[] xShuffles = new Matrix[caseNum];
			Matrix[] yShuffles = new Matrix[caseNum];
			int[] index = ModelOperatorUtil.shuffleIndex(sampleNum, false);

			for (int j = 0; j < caseNum; j++) {
				Matrix x = trainXs[j];
				Matrix y = trainYs[j];
				Matrix xShuffle = new Matrix(x.getRowDimension(), x.getColumnDimension());
				Matrix yShuffle = new Matrix(y.getRowDimension(), y.getColumnDimension());

				for (int k = 0; k < sampleNum; k++) {
					xShuffle.setMatrix(k, k, 0, x.getColumnDimension() - 1,
							x.getMatrix(index[k], index[k], 0, x.getColumnDimension() - 1));
					yShuffle.setMatrix(k, k, 0, y.getColumnDimension() - 1,
							y.getMatrix(index[k], index[k], 0, y.getColumnDimension() - 1));
				}
				xShuffles[j] = xShuffle;
				yShuffles[j] = yShuffle;
			}

			for (int j = 0; j < sampleNum - batchSize + 1; j += batchSize) {
				Matrix[] xBatches = new Matrix[CommonConfigure.caseNum];
				Matrix[] yBatches = new Matrix[CommonConfigure.caseNum];

				for (int k = 0; k < CommonConfigure.caseNum; k++) {
					Matrix x = trainXs[k];
					Matrix y = trainYs[k];
					xBatches[k] = xShuffles[k].getMatrix(j, j + batchSize - 1, 0, x.getColumnDimension() - 1);
					yBatches[k] = yShuffles[k].getMatrix(j, j + batchSize - 1, 0, y.getColumnDimension() - 1);

				}

				this.sgdBatch(xBatches, yBatches);

			}
			losses[i] = this.loss(validateXs, validateYs);
			if (i % 1 == 0) {
				System.out.printf("epoch %d:  loss= %f\n", i, losses[i]);
			}

			if (lossMin > losses[i]) {
				lossMin = losses[i];
				weightMinLoss = this.weight.clone();
			}
		}

		this.weight = weightMinLoss;
		System.out.printf("min loss: %f\n", lossMin);

		long endTime = System.currentTimeMillis();
		System.out.printf("training time: %.2f s\n", (endTime - startTime) / 1000.0);

	}

	/**
	 * 验证并导出结果
	 * 
	 * @return
	 */
	public Map<String, double[]> validateOutput() {
		Matrix preY = this.predict(this.validateXs);
		double[][] preArray = preY.getArray();
		double[][] yArray = this.validateYs[mainFlag].getArray();

		int len = preArray.length;
		double[] preDouble = new double[len];
		double[] yDouble = new double[len];

		for (int i = 0; i < len; i++) {
			preDouble[i] = preArray[i][0];
			yDouble[i] = yArray[i][0];
		}

		Map<String, double[]> result = new HashMap<>();
		result.put("preNorm", preDouble);
		result.put("yNorm", yDouble);

		return result;
	}

	private void sgdBatch(Matrix[] xBatches, Matrix[] yBatches) {
		// TODO Auto-generated method stub
		int step = xBatches[0].getRowDimension();
		TreeLstmState nlstmState = this.forward(xBatches);

		TreeDWeight dWeightSum = this.bptt(xBatches, yBatches, nlstmState, preLayer);

		weight.update(dWeightSum, lr / step);

	}

	private TreeLstmState forward(Matrix[] xs) {
		int step = xs[0].getRowDimension();

		TreeLstmState nlstmState = new TreeLstmState(step, preLayer);

		for (int t = 0; t < step; t++) {
			Matrix[] xSingle = new Matrix[caseNum];
			for (int i = 0; i < caseNum; i++) {
				xSingle[i] = xs[i].getMatrix(t, t, 0, xs[i].getColumnDimension() - 1);
			}

			nlstmState.update(weight, xSingle, preLayer);

		}

		return nlstmState;
	}

	private TreeDWeight bptt(Matrix[] xs, Matrix[] ys, TreeLstmState nlstmState, int[][] preLayer) {
		int step = xs[0].getRowDimension();

		TreeDWeight dWeightSum = new TreeDWeight(preLayer);

		Matrix[] deltaAtsNet = new Matrix[caseNum];
		Matrix[] deltaItsNet = new Matrix[caseNum];
		Matrix[] deltaFtsNet = new Matrix[caseNum];
		Matrix[] deltaN1tsNet = new Matrix[caseNum];
		Matrix[] deltaN2tsNet = new Matrix[caseNum];
		Matrix[] deltaOtsNet = new Matrix[caseNum];

		Matrix[][] deltaRtsNet = new Matrix[caseNum][];

		for (int i = 0; i < caseNum; i++) {
			int hiddenNum = (int) CommonConfigure.getPara("hiddenDim" + i);
			deltaAtsNet[i] = new Matrix(hiddenNum, 1);
			deltaItsNet[i] = new Matrix(hiddenNum, 1);
			deltaFtsNet[i] = new Matrix(hiddenNum, 1);
			deltaOtsNet[i] = new Matrix(hiddenNum, 1);
			deltaN1tsNet[i] = new Matrix(hiddenNum, 1);
			deltaN2tsNet[i] = new Matrix(hiddenNum, 1);

			int preNum = preLayer[i].length;
			deltaRtsNet[i] = new Matrix[preNum];
			for (int j = 0; j < preNum; j++) {
				deltaRtsNet[i][j] = new Matrix(hiddenNum, 1);
			}
		}

		Matrix[] nextFs = new Matrix[caseNum];
		Matrix[] deltaCs = new Matrix[caseNum];

		for (int t = step; t > 0; t--) {
			TreeDWeight dWeight = new TreeDWeight(preLayer);

			Matrix[] preHs = new Matrix[caseNum];

			Matrix py = nlstmState.ys[t - 1];
			Matrix Y = ys[mainFlag].getMatrix(t - 1, t - 1, 0, 0);

			Matrix deltaY = py.minus(Y);
			Matrix deltaZ = deltaY.times(Matrix.sigmoidDerivate(py));

			Matrix N = nlstmState.Ns[t][mainFlag];
			dWeight.dwY.dw.plusEquals(deltaZ.times(N.transpose()));
			dWeight.dwY.db.plusEquals(deltaZ);

			Matrix[] x = new Matrix[caseNum];

			Matrix[] deltaNs = new Matrix[caseNum];
			Matrix[] deltaRs = new Matrix[caseNum];
			for (int i = caseNum - 1; i >= 0; i--) {

				if (i == mainFlag) {
					// 没有后层节点
					deltaNs[i] = weight.wY.w.transpose().times(deltaZ);
				} else {
					int nextNodeNum = nextLayer[i];
					int index = indexInNext[i];
					Matrix r = nlstmState.rs[t][nextNodeNum][index];
					deltaNs[i] = deltaRs[nextNodeNum].arrayTimes(r);
				}

				Matrix n1 = nlstmState.n1s[t][i];
				deltaRs[i] = deltaNs[i].arrayTimes(n1);

				Matrix n2 = nlstmState.n2s[t][i];
				Matrix deltaH = deltaNs[i].arrayTimes(n2);
				if (t != step) {
					deltaH = deltaH.plus(weight.whxIs[i].wh.transpose().times(deltaItsNet[i]))
							.plus(weight.whxFs[i].wh.transpose().times(deltaFtsNet[i]))
							.plus(weight.whxOs[i].wh.transpose().times(deltaOtsNet[i]))
							.plus(weight.whxAs[i].wh.transpose().times(deltaAtsNet[i]))
							.plus(weight.whxN1s[i].wh.transpose().times(deltaN1tsNet[i]))
							.plus(weight.whxN2s[i].wh.transpose().times(deltaN2tsNet[i]));
					if (preLayer[i].length > 0) {
						for (int j = 0; j < preLayer[i].length; j++) {
							deltaH = deltaH.plus(weight.whxRs[i][j].wh.transpose().times(deltaRtsNet[i][j]));
						}
					}
				}

				Matrix c = nlstmState.cs[t][i];
				Matrix deltaO = deltaH.arrayTimes(Matrix.tanh(c));
				Matrix o = nlstmState.os[t][i];
				Matrix deltaC = deltaH.arrayTimes(o).arrayTimes(Matrix.tanhDerivate(Matrix.tanh(c)));
				if (t == step) {
					deltaCs[i] = deltaC;

				} else {
					deltaCs[i] = deltaC.plus(deltaCs[i].arrayTimes(nextFs[i]));
				}

				Matrix a = nlstmState.as[t][i];
				Matrix preC = nlstmState.cs[t - 1][i];
				Matrix ig = nlstmState.is[t][i];
				Matrix f = nlstmState.fs[t][i];
				nextFs[i] = f;
				Matrix R = nlstmState.Rs[t][i];
				Matrix h = nlstmState.hs[t][i];

				Matrix deltaI = deltaCs[i].arrayTimes(a);
				Matrix deltaF = deltaCs[i].arrayTimes(preC);
				Matrix deltaA = deltaCs[i].arrayTimes(ig);
				Matrix deltaN1 = deltaNs[i].arrayTimes(R);
				Matrix deltaN2 = deltaNs[i].arrayTimes(h);

				deltaAtsNet[i] = deltaA.arrayTimes(Matrix.tanhDerivate(a));
				deltaItsNet[i] = deltaI.arrayTimes(Matrix.sigmoidDerivate(ig));
				deltaFtsNet[i] = deltaF.arrayTimes(Matrix.sigmoidDerivate(f));
				deltaOtsNet[i] = deltaO.arrayTimes(Matrix.sigmoidDerivate(o));
				deltaN1tsNet[i] = deltaN1.arrayTimes(Matrix.sigmoidDerivate(n1));
				deltaN2tsNet[i] = deltaN2.arrayTimes(Matrix.sigmoidDerivate(n2));

				int preNum = preLayer[i].length;
				for (int j = 0; j < preNum; j++) {
					int preNodeNum = preLayer[i][j];
					Matrix deltar = deltaRs[i].arrayTimes(nlstmState.Ns[t][preNodeNum]);
					Matrix r = nlstmState.rs[t][i][j];
					deltaRtsNet[i][j] = deltar.arrayTimes(Matrix.sigmoidDerivate(r));
				}
				x[i] = xs[i].getMatrix(t - 1, t - 1, 0, xs[i].getColumnDimension() - 1);
				preHs[i] = nlstmState.hs[t - 1][i];
			}

			dWeight.update(deltaItsNet, deltaFtsNet, deltaAtsNet, deltaOtsNet, deltaN1tsNet, deltaN2tsNet, deltaRtsNet,
					preHs, x, preLayer);
			dWeightSum = dWeightSum.add(dWeight, preLayer);
		}

		return dWeightSum;

	}

	public double loss(Matrix[] xs, Matrix[] ys) {
		Matrix preY = this.predict(xs);
		double loss = -LossFunction.calRsquare(preY, ys[mainFlag]);

		return loss;
	}

	public Matrix predict(Matrix[] xs) {
		TreeLstmState nlstmState = this.forward(xs);
		int step = xs[0].getRowDimension();

		Matrix preY = new Matrix(step, 1);

		for (int i = 0; i < step; i++) {
			preY.set(i, 0, nlstmState.ys[i].get(0, 0));
		}

		return preY;
	}

}
