package com.zzd.model.weight;

import com.zzd.model.optimizer.AdamOptimizer;
import com.zzd.model.utils.Matrix;

public class WeightY {
	
	public Matrix w,b;
	public AdamOptimizer adamW;
	public AdamOptimizer adamB;
	
	public WeightY(int xDim,int hiddenDim){
		/*
		 * 这个应该修改为1，这个类主要用来作为输出层
		 * 输出层一般只有一个数，所以这里应该修改为1
		 */
		xDim=1;
		double num2=Math.sqrt(6.0/(xDim+hiddenDim+1));
		this.w=Matrix.uniform(-num2, num2, xDim, hiddenDim);
		this.b=Matrix.uniform(-num2, num2, xDim, 1);
		adamW=new AdamOptimizer(w);
		adamB=new AdamOptimizer(b);
	}
	
	public WeightY(){
		
	}
	
	
	public void update(DWeightY dWeightY, double lr){
		this.w.minusEquals(this.adamW.update(dWeightY.dw).times(lr));
		this.b.minusEquals(this.adamB.update(dWeightY.db).times(lr));
	}
	
	public WeightY clone(){
		WeightY newWeightY=new WeightY();
		newWeightY.w=this.w.copy();
		newWeightY.b=this.b.copy();
		newWeightY.adamW=this.adamW.clone();
		newWeightY.adamB=this.adamB.clone();
		
		return newWeightY;
	}
	
}
