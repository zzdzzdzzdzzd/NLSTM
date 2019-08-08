package com.zzd.model.weight;

import com.zzd.model.optimizer.AdamOptimizer;
import com.zzd.model.utils.Matrix;

public class WeightYNoB {
	
	public Matrix w;
	public AdamOptimizer adamW;
	
	public WeightYNoB(int xDim,int hiddenDim){
		/*
		 * 这个应该修改为1，这个类主要用来作为输出层
		 * 输出层一般只有一个数，所以这里应该修改为1
		 */
		xDim=1;
		double num2=Math.sqrt(6.0/(xDim+hiddenDim+1));
		this.w=Matrix.uniform(-num2, num2, xDim, hiddenDim);
		adamW=new AdamOptimizer(w);
	}
	
	public WeightYNoB(){
		
	}
	
	
	public void update(DWeightYNoB dWeightY, double lr){
		this.w.minusEquals(this.adamW.update(dWeightY.dw).times(lr));
	}
	
	public WeightYNoB clone(){
		WeightYNoB newWeightY=new WeightYNoB();
		newWeightY.w=this.w.copy();
		newWeightY.adamW=this.adamW.clone();
		
		return newWeightY;
	}
	
}
