package com.zzd.model.weight;

import com.zzd.model.utils.Matrix;

public class DWeightY {
	public int xDim;
	public int hiddenDim;
	public Matrix dw,db;
	
	public DWeightY(int xDim, int hiddenDim){
		this.xDim=xDim;
		this.hiddenDim=hiddenDim;
		this.dw=new Matrix(xDim,hiddenDim,0.0);
		this.db=new Matrix(xDim,1,0.0);		
	}
	
	public DWeightY add(DWeightY dWeightY){
		DWeightY result=new DWeightY(this.xDim,this.hiddenDim);
		result.dw=this.dw.plus(dWeightY.dw);
		result.db=this.db.plus(dWeightY.db);
		
		return result;
	}
}
