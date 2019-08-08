package com.zzd.model.weight;

import com.zzd.model.utils.Matrix;

public class DWeightYNoB {
	public int xDim;
	public int hiddenDim;
	public Matrix dw;
	
	public DWeightYNoB(int xDim, int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		this.dw=new Matrix(xDim,hiddenDim,0.0);	
	}
	
	public DWeightYNoB add(DWeightYNoB dWeightY){
		DWeightYNoB result=new DWeightYNoB(this.xDim,this.hiddenDim);
		result.dw=this.dw.plus(dWeightY.dw);
		
		return result;
	}
}
