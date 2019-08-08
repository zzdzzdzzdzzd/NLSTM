package com.zzd.model.weight;

import com.zzd.model.optimizer.AdamOptimizer;
import com.zzd.model.utils.Matrix;

public class WeightHXNoB {
	public Matrix wh;

	public Matrix wx;

	public AdamOptimizer adamWh,adamWx;
	
	public WeightHXNoB(int xDim,int hiddenDim){
		double num1=Math.sqrt(6.0/(2*hiddenDim+1));
		double num2=Math.sqrt(6.0/(xDim+hiddenDim+1));

		this.wh=Matrix.uniform(-num1, num1, hiddenDim, hiddenDim);
		this.wx=Matrix.uniform(-num2, num2, hiddenDim, xDim);
		adamWh=new AdamOptimizer(wh);
		adamWx=new AdamOptimizer(wx);
		
	}
	
	public WeightHXNoB(){
		
	}
	
	/**
	 * 更新权重
	 * @param dWeightHX
	 * @param lr
	 */
	public void update(DWeightHXNoB dWeightHX, double lr){
		this.wh.minusEquals(this.adamWh.update(dWeightHX.dwh).times(lr));
		this.wx.minusEquals(this.adamWx.update(dWeightHX.dwx).times(lr));
	}
	
	public WeightHXNoB clone(){
		WeightHXNoB newWeightHX=new WeightHXNoB();
		newWeightHX.wx=this.wx.copy();
		newWeightHX.wh=this.wh.copy();
		newWeightHX.adamWx=this.adamWx.clone();
		newWeightHX.adamWh=this.adamWh.clone();
		
		return newWeightHX;
	}
	
}
