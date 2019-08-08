package com.zzd.model.nLstmNetwork;

import java.util.HashMap;

import com.zzd.model.nLstmState.LstmState;
import com.zzd.model.utils.LossFunction;
import com.zzd.model.utils.Matrix;
import com.zzd.model.utils.ModelOperatorUtil;
import com.zzd.model.weight.DWeight;
import com.zzd.model.weight.Weight;



public class LstmNetwork {
	/**
	 * number of  features
	 */
	private int xDim;
	/**
	 * nodes of hidden layers
	 */
	private int hiddenDim;
	/**
	 * weights
	 * including: [Whi,Wxi,bi];[Whf,Wxf,bf];[Who,Wxo,bo];[Wha,Wxa,ba];[Wy,by];
	 */
	Weight weight;	
	
	/**
	 * constructors
	 * @param xDim 
	 * @param hiddenDim 
	 */
	public LstmNetwork(int xDim, int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		
		weight=new Weight(xDim,hiddenDim);
		
	}	
	
	public double train(Matrix x, Matrix y, Matrix validateX, Matrix validateY,
			int batchSize,double lr, int epochs){
		double startTime=System.currentTimeMillis();
		double[] losses=new double[epochs];
		int sampleNum=x.getRowDimension();
		
		Weight weightMinLoss=this.weight;
		double lossMin=Double.POSITIVE_INFINITY;
		
		Matrix xBatch;
		Matrix yBatch;
		
		for(int i=0;i<epochs;i++){
			Matrix xShuffle=new Matrix(x.getRowDimension(),x.getColumnDimension());
			Matrix yShuffle=new Matrix(y.getRowDimension(),y.getColumnDimension());
			int[] index=ModelOperatorUtil.shuffleIndex(sampleNum, false);
			for(int k=0;k<sampleNum;k++){
				xShuffle.setMatrix(k, k, 0, x.getColumnDimension()-1, x.getMatrix(index[k], index[k], 0, x.getColumnDimension()-1));
				yShuffle.setMatrix(k, k, 0, y.getColumnDimension()-1, y.getMatrix(index[k], index[k], 0, y.getColumnDimension()-1));							
			}
			
			for(int j=0;j<sampleNum-batchSize+1;j+=batchSize){
				xBatch=xShuffle.getMatrix(j, j+batchSize-1,0, x.getColumnDimension()-1);
				yBatch=yShuffle.getMatrix(j, j+batchSize-1,0, y.getColumnDimension()-1);				
				this.sgdBatch(xBatch, yBatch, lr);				
			}
			
			losses[i]=this.loss(validateX, validateY);
			System.out.printf("epoch %d:  loss= %f\n",i,losses[i]);
			if(lossMin>losses[i]){
				lossMin=losses[i];
				weightMinLoss=this.weight.clone();
			}						
		}
		
		this.weight=weightMinLoss;
		System.out.printf("min loss: %f\n", lossMin);
		
		long endTime=System.currentTimeMillis();
		double time=(endTime-startTime)/1000.0;
        System.out.printf("training time: %.2f s\n", time);

        return time;
	}
	
	public HashMap<String,double[]> validateOutput(Matrix trainX, Matrix trainY, Matrix validateX, Matrix validateY){

		Matrix mean=this.predict(validateX);
		
		
		double[][] preArray=mean.getArray();
		double[][] yArray=validateY.getArray();

		
		int len=preArray.length;
		double[] preDouble=new double[len];
		double[] yDouble=new double[len];

		
		for(int i=0;i<len;i++){
			preDouble[i]=preArray[i][0];
			yDouble[i]=yArray[i][0];

		}
		HashMap<String,double[]> result=new HashMap<>();
		result.put("preNorm", preDouble);
		result.put("yNorm", yDouble);
	
		
		return result;

	}
	
	/**
	 * sgd with mini batch
	 * @param xBatch
	 * @param yBatch
	 * @param lr
	 */
	private void sgdBatch(Matrix xBatch, Matrix yBatch, double lr) {
		int step=xBatch.getRowDimension();
		LstmState lstmState=this.forward(xBatch);		
		DWeight dWeightSum=this.bptt(xBatch, yBatch, lstmState, step);
		
		/*
		 * Chinese:本来是应该将一个批次的平均值和学习率传入的（当然也可以这样做）
		 * 但是dWeightSum的平均需要传进去对所有的dw变量进行平均
		 * 还不如把这个要除的数放在学习率上，反正后面更新权重都是直接乘的
		 * 
		 * English:The average of a batch and the learning rate should be passed into the function (of course you can), 
		 * but the average of dWeightSum needs to be passed in to average all the dw variables. 
		 * It is equivalent to divide the learning rate by step. 
		 * Because we're directly multiplying the weights with learning rate.
		 */
		this.weight.updateHX(dWeightSum, lr/step);
		this.weight.updateY(dWeightSum, lr/step);
		
	}
	/**
	 * forward propagation
	 * @param x
	 * @return
	 */
	private LstmState forward(Matrix x){
		int step=x.getRowDimension();
		LstmState lstmState=new LstmState(step,this.xDim,this.hiddenDim);
		for(int t=0;t<step;t++){
			lstmState.update(weight, x.getMatrix(t, t, 0, x.getColumnDimension()-1));			
		}
				
		return lstmState;
				
	}
	
	/**
	 * Back propagation Through Time (BPTT)
	 * @param x
	 * @param y
	 * @param lstmState
	 * @param step
	 * @return
	 */
	private DWeight bptt(Matrix x,Matrix y,LstmState lstmState, int step){
		
		DWeight dWeightSum=new DWeight(this.xDim,this.hiddenDim);
		
		Matrix deltaAtNet=new Matrix(this.hiddenDim,1);
		Matrix deltaItNet=new Matrix(this.hiddenDim,1);
		Matrix deltaFtNet=new Matrix(this.hiddenDim,1);
		Matrix deltaOtNet=new Matrix(this.hiddenDim,1);
		
		Matrix nextF=null;

		Matrix deltaC=null;
		
		for(int t=step;t>0;t--){
			DWeight dWeight=new DWeight(this.xDim,this.hiddenDim);
			
			Matrix py=lstmState.ys[t-1];
			
			Matrix Y=y.getMatrix(t-1, t-1, 0,0);
			Matrix deltaY=py.minus(Y);//.arrayTimes(Matrix.tanhDerivate(Y));
			
			Matrix h=lstmState.hs[t];
			
			Matrix deltaH=null;
			if(t==step){
				deltaH=weight.wY.w.transpose().times(deltaY.times(Matrix.sigmoidDerivate(py)));
			}else{
				deltaH=weight.wY.w.transpose().times(deltaY.times(Matrix.sigmoidDerivate(py)))
						.plus(weight.whxI.wh.transpose().times(deltaItNet))
						.plus(weight.whxF.wh.transpose().times(deltaFtNet))
						.plus(weight.whxO.wh.transpose().times(deltaOtNet))
						.plus(weight.whxA.wh.transpose().times(deltaAtNet));
			}
			
			Matrix c=lstmState.cs[t];
			Matrix deltaO=deltaH.arrayTimes(Matrix.tanh(c));
			
			Matrix o=lstmState.os[t];

			if(t==step){
				deltaC=deltaH.arrayTimes(o).arrayTimes(Matrix.tanhDerivate(Matrix.tanh(c)));
			}else{
				deltaC=deltaH.arrayTimes(o).arrayTimes(Matrix.tanhDerivate(Matrix.tanh(c)))
						.plus(deltaC.arrayTimes(nextF));
			}
			
			Matrix a=lstmState.as[t];
			Matrix preC=lstmState.cs[t-1];
			Matrix preH=lstmState.hs[t-1];
			Matrix i=lstmState.is[t];
			Matrix f=lstmState.fs[t];
			nextF=f;
			Matrix deltaI=deltaC.arrayTimes(a);
			Matrix deltaF=deltaC.arrayTimes(preC);
			Matrix deltaA=deltaC.arrayTimes(i);
			
			deltaAtNet=deltaA.arrayTimes(Matrix.tanhDerivate(a));
			deltaItNet=deltaI.arrayTimes(Matrix.sigmoidDerivate(i));
			deltaFtNet=deltaF.arrayTimes(Matrix.sigmoidDerivate(f));
			deltaOtNet=deltaO.arrayTimes(Matrix.sigmoidDerivate(o));
			
			dWeight.dwY.dw.plusEquals(deltaY.times(Matrix.sigmoidDerivate(py)).times(h.transpose()));
			dWeight.dwY.db.plusEquals(deltaY.times(Matrix.sigmoidDerivate(py)));
			dWeight.update(deltaItNet, deltaFtNet, deltaAtNet, deltaOtNet, preH, x.getMatrix(t-1, t-1, 0, x.getColumnDimension()-1));
		
			dWeightSum=dWeightSum.add(dWeight);
		}
		
		return dWeightSum;
		

	}	
	
	/**
	 * calculate loss
	 * @param x
	 * @param y
	 * @return
	 */
	public double loss(Matrix x, Matrix y){
		Matrix preY=this.predict(x);				
		double loss=-LossFunction.calRsquare(preY, y);
		return loss;
		
	}	
	
	/**
	 * predict
	 * @param x
	 * @return
	 */
	public Matrix predict(Matrix x){
		LstmState lstmState=this.forward(x);
		int step=x.getRowDimension();
		Matrix preY=new Matrix(step,1);
		for(int i=0;i<step;i++){
			preY.set(i, 0, lstmState.ys[i].get(0, 0));
		}
		return preY;
	}	
}
