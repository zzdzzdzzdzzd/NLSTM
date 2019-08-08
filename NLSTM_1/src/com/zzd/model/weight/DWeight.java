package com.zzd.model.weight;

import com.zzd.model.utils.Matrix;

public class DWeight {
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
	public DWeightHX dwhxI,dwhxF,dwhxO,dwhxA;
	public DWeightY dwY;
	
	public DWeight(int xDim,int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		
		this.dwhxI=new DWeightHX(xDim, hiddenDim);
		this.dwhxF=new DWeightHX(xDim, hiddenDim);
		this.dwhxO=new DWeightHX(xDim, hiddenDim);
		this.dwhxA=new DWeightHX(xDim, hiddenDim);
		
		this.dwY=new DWeightY(1,hiddenDim);
		
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
	public void update(Matrix di, Matrix df, Matrix da, Matrix doo, Matrix hss, Matrix x){
		this.dwhxI.calGrad(di, hss, x);
		this.dwhxF.calGrad(df, hss, x);
		this.dwhxA.calGrad(da, hss, x);
		this.dwhxO.calGrad(doo, hss, x);
	}
	/**
	 * 权重向量叠加
	 * @param dWeight
	 * @return
	 */
	public DWeight add(DWeight dWeight){
		DWeight result=new DWeight(this.xDim,this.hiddenDim);
		result.dwhxI=this.dwhxI.add(dWeight.dwhxI);
		result.dwhxF=this.dwhxF.add(dWeight.dwhxF);
		result.dwhxA=this.dwhxA.add(dWeight.dwhxA);
		result.dwhxO=this.dwhxO.add(dWeight.dwhxO);
		result.dwY=this.dwY.add(dWeight.dwY);
		
		return result;
	}
	
}

