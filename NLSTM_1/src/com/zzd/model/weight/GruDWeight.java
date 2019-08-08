package com.zzd.model.weight;

import com.zzd.model.utils.Matrix;

public class GruDWeight {
	/**
	 * 一个样本的特征
	 */
	int xDim;
	/**
	 * 隐藏层节点数
	 */
	int hiddenDim;
	/**
	 * dwh,dwx,db
	 */
	public DWeightHXNoB dwhxZ,dwhxR,dwhxH;
	public DWeightYNoB dwY;
	
	public GruDWeight(int xDim,int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		
		this.dwhxZ=new DWeightHXNoB(xDim, hiddenDim);
		this.dwhxR=new DWeightHXNoB(xDim, hiddenDim);
		this.dwhxH=new DWeightHXNoB(xDim, hiddenDim);
		
		this.dwY=new DWeightYNoB(1,hiddenDim);
		
	}
	/**
	 * 计算各个权重矩阵的偏导数
	 * @param di
	 * @param df
	 * @param da
	 * @param doo
	 * @param hss
	 * @param x
	 */
	public void update(Matrix dz, Matrix dr, Matrix dh, Matrix hss, Matrix rh, Matrix x){
		this.dwhxZ.calGrad(dz, hss, x);
		this.dwhxR.calGrad(dr, hss, x);
		this.dwhxH.calGrad(dh, rh, x);
	}
	/**
	 * 权重向量叠加
	 * @param dWeight
	 * @return
	 */
	public GruDWeight add(GruDWeight dWeight){
		GruDWeight result=new GruDWeight(this.xDim,this.hiddenDim);
		result.dwhxZ=this.dwhxZ.add(dWeight.dwhxZ);
		result.dwhxR=this.dwhxR.add(dWeight.dwhxR);
		result.dwhxH=this.dwhxH.add(dWeight.dwhxH);

		result.dwY=this.dwY.add(dWeight.dwY);
		
		return result;
	}
	
}

