package com.zzd.model.nLstmState;

import com.zzd.model.utils.Matrix;
import com.zzd.model.weight.GruWeight;
import com.zzd.model.weight.WeightHXNoB;

public class GruState {
	/**
	 * 所有时段输入门状态
	 */
	public Matrix[] zs;
	/**
	 * 所有时段遗忘门状态
	 */
	public Matrix[] rs;
	/**
	 * 所有时段C波浪状态
	 */
	public Matrix[] as;
	/**
	 * 所有时段输出状态
	 */
	public Matrix[] hs;
	/**
	 * 所有时段预测值
	 */
	public Matrix[] ys;
	/**
	 * 总时段长
	 */
	int step;
	/**
	 * 当前时段
	 */
	int currentStep;
	
	public GruState(int step, int xDim, int hiddenDim){
		this.step=step+1;
		//当前时段从1开始计数，是因为给第一个时段预留一个全0的前一时段
		this.currentStep=1;
		
		//输入门
		this.zs=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			zs[i]=new Matrix(hiddenDim,1);
		}
		
		//遗忘门
		this.rs=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			rs[i]=new Matrix(hiddenDim,1);
		}
		
		//当前输入的细胞状态，C波浪线
		this.as=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			as[i]=new Matrix(hiddenDim,1);
		}
		
		//隐藏状态
		this.hs=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			hs[i]=new Matrix(hiddenDim,1);
		}
		
		
		//输出值
		this.ys=new Matrix[step];
		int yDim=1;//这里应该是输出层的维度
		for(int i=0;i<step;i++){
			ys[i]=new Matrix(yDim,1);
		}		
		
		
	}
	/**
	 * 前向传播
	 * @param weight
	 * @param x
	 */
	public void update(GruWeight weight, Matrix x){
		//前一时刻隐藏状态
		Matrix preHt=this.hs[currentStep-1];
		//输入门
		this.zs[currentStep]=calGate(weight.whxZ,preHt,x,"sigmoid");
		//遗忘门
		this.rs[currentStep]=calGate(weight.whxR,preHt,x,"sigmoid");
		//当前C波浪状态
		this.as[currentStep]=calGate(weight.whxH,this.rs[currentStep].arrayTimes(preHt),x,"tanh");
		
		//cell 状态
		int m=this.zs[currentStep].getRowDimension();
		int n=this.zs[currentStep].getColumnDimension();
		Matrix oneMinusZ=this.zs[currentStep].times(-1).plus(Matrix.ones(m, n));
		Matrix zh=oneMinusZ.arrayTimes(preHt);
		Matrix za=this.zs[currentStep].arrayTimes(this.as[currentStep]);

		this.hs[currentStep]=zh.plus(za);
		
		Matrix yz=weight.wY.w.times(hs[currentStep]);
		ys[currentStep-1]=Matrix.sigmoid(yz);
		currentStep++;
		
		
	}
	/**
	 * 计算门限结构
	 * @param whx
	 * @param preHt
	 * @param x
	 * @param activation
	 * @return
	 */
	private Matrix calGate(WeightHXNoB whx, Matrix preHt, Matrix x, String activation){
		Matrix zh=whx.wh.times(preHt);
		Matrix zx=whx.wx.times(x.transpose());
		Matrix z=zh.plus(zx);
		Matrix a=z;
		if(activation.equals("sigmoid")){
			a=Matrix.sigmoid(z);
		}else if(activation.equals("tanh")){
			a=Matrix.tanh(z);
		}else{
			throw new IllegalArgumentException("activation function not implemented!");
		}
		
		return a;
	}
	

}
