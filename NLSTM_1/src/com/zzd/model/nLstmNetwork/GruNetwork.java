package com.zzd.model.nLstmNetwork;

import java.util.HashMap;

import com.zzd.model.nLstmState.GruState;
import com.zzd.model.utils.LossFunction;
import com.zzd.model.utils.Matrix;
import com.zzd.model.utils.ModelOperatorUtil;
import com.zzd.model.weight.GruDWeight;
import com.zzd.model.weight.GruWeight;



public class GruNetwork {
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
	 * including : [Whz,Wxz];[Whr,Wxr];[Whh,Wxh];[Wy,by];
	 */
	GruWeight weight;	
	
	/**
	 * constructors
	 * @param xDim 
	 * @param hiddenDim 
	 */
	public GruNetwork(int xDim, int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		
		weight=new GruWeight(xDim,hiddenDim);
		
	}	
	
	public double train(Matrix x, Matrix y,Matrix validateX, Matrix validateY,int batchSize,double lr, int epochs){
		double startTime=System.currentTimeMillis();
		double[] losses=new double[epochs];
		int sampleNum=x.getRowDimension();
		
		GruWeight weightMinLoss=this.weight;
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
		GruState lstmState=this.forward(xBatch);		
		GruDWeight dWeightSum=this.bptt(xBatch, yBatch, lstmState, step);
		
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
	private GruState forward(Matrix x){
		int step=x.getRowDimension();
		GruState lstmState=new GruState(step,this.xDim,this.hiddenDim);
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
	private GruDWeight bptt(Matrix x,Matrix y,GruState lstmState, int step){
		
		GruDWeight dWeightSum=new GruDWeight(this.xDim,this.hiddenDim);
		
		Matrix deltaZtNet=new Matrix(this.hiddenDim,1);
		Matrix deltaRtNet=new Matrix(this.hiddenDim,1);
		Matrix deltaAtNet=new Matrix(this.hiddenDim,1);
		
		Matrix nextOneMinusZ=null;

		Matrix deltaH=null;
		Matrix nextR=null;
		
		for(int t=step;t>0;t--){
			GruDWeight dWeight=new GruDWeight(this.xDim,this.hiddenDim);
			
			Matrix py=lstmState.ys[t-1];
			
			Matrix Y=y.getMatrix(t-1, t-1, 0,0);
			Matrix deltaY=py.minus(Y);//
			
			Matrix h=lstmState.hs[t];
			
			
			if(t==step){
				deltaH=weight.wY.w.transpose().times(deltaY.times(Matrix.sigmoidDerivate(py)));
			}else{
				deltaH=weight.wY.w.transpose().times(deltaY.times(Matrix.sigmoidDerivate(py)))
						.plus(weight.whxZ.wh.transpose().times(deltaZtNet))
						.plus(weight.whxR.wh.transpose().times(deltaRtNet))
						.plus(weight.whxH.wh.transpose().times(deltaAtNet).arrayTimes(nextR))//////
						.plus(deltaH.arrayTimes(nextOneMinusZ));
			}
			

			
			Matrix a=lstmState.as[t];
			Matrix preH=lstmState.hs[t-1];
			Matrix z=lstmState.zs[t];
			Matrix r=lstmState.rs[t];
			nextR=r.copy();
			
			int m=z.getRowDimension();
			int n=z.getColumnDimension();
			nextOneMinusZ=z.times(-1).plus(Matrix.ones(m, n));

			Matrix deltaZ=deltaH.arrayTimes(a.minus(preH));
			Matrix deltaA=deltaH.arrayTimes(z);
			
			deltaAtNet=deltaA.arrayTimes(Matrix.tanhDerivate(a));
			Matrix deltaR=weight.whxH.wh.transpose().times(deltaAtNet).arrayTimes(preH);
			deltaRtNet=deltaR.arrayTimes(Matrix.sigmoidDerivate(r));
			deltaZtNet=deltaZ.arrayTimes(Matrix.sigmoidDerivate(z));

			
			dWeight.dwY.dw.plusEquals(deltaY.times(Matrix.sigmoidDerivate(py)).times(h.transpose()));

			dWeight.update(deltaZtNet, deltaRtNet, deltaAtNet,  preH, r.arrayTimes(preH),x.getMatrix(t-1, t-1, 0, x.getColumnDimension()-1));
		
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
		GruState gruState=this.forward(x);
		int step=x.getRowDimension();
		Matrix preY=new Matrix(step,1);
		for(int i=0;i<step;i++){
			preY.set(i, 0, gruState.ys[i].get(0, 0));
		}
		return preY;
	}	
}
