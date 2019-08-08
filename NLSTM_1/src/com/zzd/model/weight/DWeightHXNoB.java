package com.zzd.model.weight;

import com.zzd.model.utils.Matrix;

public class DWeightHXNoB {
	int xDim;
	int hiddenDim;
	Matrix dwh,dwx;
	
	public DWeightHXNoB(int xDim,int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		this.dwh=new Matrix(hiddenDim,hiddenDim,0.0);
		this.dwx=new Matrix(hiddenDim,xDim,0.0);
	}
	/**
	 * 求权重梯度
	 * @param delta
	 * @param preHt
	 * @param x
	 */
	public void calGrad(Matrix delta, Matrix preHt, Matrix x){
		this.dwh.plusEquals(delta.times(preHt.transpose()));
		this.dwx.plusEquals(delta.times(x));//自己修改处2：去掉了x的转置
	}
	/**
	 * 梯度叠加
	 * @param dWeightHX
	 * @return
	 */
	public DWeightHXNoB add(DWeightHXNoB dWeightHX){
		DWeightHXNoB result=new DWeightHXNoB(this.xDim, this.hiddenDim);
		result.dwh=this.dwh.plus(dWeightHX.dwh);
		result.dwx=this.dwx.plus(dWeightHX.dwx);
		
		return result;
	}
	
}
