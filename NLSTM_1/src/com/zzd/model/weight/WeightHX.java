package com.zzd.model.weight;

import com.zzd.model.optimizer.AdamOptimizer;
import com.zzd.model.utils.Matrix;

public class WeightHX {
	public Matrix wh;

	public Matrix wx;

	public Matrix b;
	
	public AdamOptimizer adamWh,adamWx,adamB;
	
	public WeightHX(int xDim,int hiddenDim){
		double num1=Math.sqrt(6.0/(2*hiddenDim+1));
		double num2=Math.sqrt(6.0/(xDim+hiddenDim+1));
		double num3=Math.sqrt(1.0/xDim);
		this.wh=Matrix.uniform(-num1, num1, hiddenDim, hiddenDim);
		this.wx=Matrix.uniform(-num2, num2, hiddenDim, xDim);
		this.b=Matrix.uniform(-num3, num3, hiddenDim, 1);
		adamWh=new AdamOptimizer(wh);
		adamWx=new AdamOptimizer(wx);
		adamB=new AdamOptimizer(b);
		
	}
	
	public WeightHX(){
		
	}
	
	/**
	 * 更新权重
	 * @param dWeightHX
	 * @param lr
	 */
	public void update(DWeightHX dWeightHX, double lr){
		this.wh.minusEquals(this.adamWh.update(dWeightHX.dwh).times(lr));
		this.wx.minusEquals(this.adamWx.update(dWeightHX.dwx).times(lr));
		this.b.minusEquals(this.adamB.update(dWeightHX.db).times(lr));
	}
	
	public WeightHX clone(){
		WeightHX newWeightHX=new WeightHX();
		newWeightHX.wx=this.wx.copy();
		newWeightHX.wh=this.wh.copy();
		newWeightHX.b=this.b.copy();
		newWeightHX.adamWx=this.adamWx.clone();
		newWeightHX.adamWh=this.adamWh.clone();
		newWeightHX.adamB=this.adamB.clone();
		
		return newWeightHX;
	}
	
}
