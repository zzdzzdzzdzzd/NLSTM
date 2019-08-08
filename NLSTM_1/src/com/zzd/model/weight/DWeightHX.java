package com.zzd.model.weight;

import com.zzd.model.utils.Matrix;

public class DWeightHX {
	int xDim;
	int hiddenDim;
	Matrix dwh,dwx,db;
	
	public DWeightHX(int xDim,int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		this.dwh=new Matrix(hiddenDim,hiddenDim,0.0);
		this.dwx=new Matrix(hiddenDim,xDim,0.0);
		this.db=new Matrix(hiddenDim,1,0.0);
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
		this.db.plusEquals(delta);
	}
	/**
	 * 梯度叠加
	 * @param dWeightHX
	 * @return
	 */
	public DWeightHX add(DWeightHX dWeightHX){
		DWeightHX result=new DWeightHX(this.xDim, this.hiddenDim);
		result.dwh=this.dwh.plus(dWeightHX.dwh);
		result.dwx=this.dwx.plus(dWeightHX.dwx);
		result.db=this.db.plus(dWeightHX.db);
		
		return result;
	}
	
}
