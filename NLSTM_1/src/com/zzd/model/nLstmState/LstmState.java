package com.zzd.model.nLstmState;

import com.zzd.model.utils.Matrix;
import com.zzd.model.weight.Weight;
import com.zzd.model.weight.WeightHX;

public class LstmState {
	/**
	 * 所有时段输入门状态
	 */
	public Matrix[] is;
	/**
	 * 所有时段遗忘门状态
	 */
	public Matrix[] fs;
	/**
	 * 所有时段输出门状态
	 */
	public Matrix[] os;
	/**
	 * 所有时段C波浪状态
	 */
	public Matrix[] as;
	/**
	 * 所有时段输出状态
	 */
	public Matrix[] hs;
	/**
	 * 所有时段cell状态
	 */
	public Matrix[] cs;
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
	
	public LstmState(int step, int xDim, int hiddenDim){
		this.step=step+1;
		//当前时段从1开始计数，是因为给第一个时段预留一个全0的前一时段
		this.currentStep=1;
		
		//输入门
		this.is=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			is[i]=new Matrix(hiddenDim,1);
		}
		
		//遗忘门
		this.fs=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			fs[i]=new Matrix(hiddenDim,1);
		}
		
		//输出门
		this.os=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			os[i]=new Matrix(hiddenDim,1);
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
		
		//cell状态
		this.cs=new Matrix[step+1];
		for(int i=0;i<step+1;i++){
			cs[i]=new Matrix(hiddenDim,1);
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
	public void update(Weight weight, Matrix x){
		//前一时刻隐藏状态
		Matrix preHt=this.hs[currentStep-1];
		//输入门
		this.is[currentStep]=calGate(weight.whxI,preHt,x,"sigmoid");
		//遗忘门
		this.fs[currentStep]=calGate(weight.whxF,preHt,x,"sigmoid");
		//输出门
		this.os[currentStep]=calGate(weight.whxO,preHt,x,"sigmoid");
		//当前C波浪状态
		this.as[currentStep]=calGate(weight.whxA,preHt,x,"tanh");
		
		//cell 状态
		Matrix fc=fs[currentStep].arrayTimes(cs[currentStep-1]);
		Matrix ia=is[currentStep].arrayTimes(as[currentStep]);
		this.cs[currentStep]=fc.plus(ia);
		
		//隐藏状态
		Matrix tc=Matrix.tanh(cs[currentStep]);
		this.hs[currentStep]=os[currentStep].arrayTimes(tc);
		
		Matrix yz=weight.wY.w.times(hs[currentStep]).plus(weight.wY.b);
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
	private Matrix calGate(WeightHX whx, Matrix preHt, Matrix x, String activation){
		Matrix zh=whx.wh.times(preHt);
		Matrix zx=whx.wx.times(x.transpose());
		Matrix z=zh.plus(zx).plus(whx.b);
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
