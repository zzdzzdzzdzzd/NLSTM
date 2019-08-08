package com.zzd.model.weight;



public class GruWeight {
	/**
	 * 输入门权重，有Wh和Wx两部分组成
	 */
	public WeightHXNoB whxZ;
	/**
	 * 遗忘门权重，有Wh和Wx两部分组成
	 */
	public WeightHXNoB whxR;
	/**
	 * 输出门权重，有Wh和Wx两部分组成
	 */
	public WeightHXNoB whxH;
	/**
	 * 输出层权重，有W和偏置b组成
	 */
	public WeightYNoB wY;
	
	/**
	 * 构造器
	 * @param xDim
	 * @param hiddenDim
	 */
	public GruWeight(int xDim, int hiddenDim){
		this.whxZ=new WeightHXNoB(xDim, hiddenDim);
		this.whxR=new WeightHXNoB(xDim, hiddenDim);
		this.whxH=new WeightHXNoB(xDim, hiddenDim);

		
		this.wY=new WeightYNoB(1, hiddenDim);
	}
	
	public GruWeight(){
		
	}
	/**
	 * 更新权重
	 * @param dWeight
	 * @param lr
	 */
	public void updateHX(GruDWeight dWeight, double lr){
		this.whxZ.update(dWeight.dwhxZ, lr);
		this.whxR.update(dWeight.dwhxR, lr);
		this.whxH.update(dWeight.dwhxH, lr);
		
	}
	
	public void updateY(GruDWeight dWeight, double lr){
		this.wY.update(dWeight.dwY, lr);
	}
	
	public GruWeight clone(){
		GruWeight newWeight=new GruWeight();
		newWeight.whxZ=this.whxZ.clone();
		newWeight.whxR=this.whxR.clone();
		newWeight.whxH=this.whxH.clone();

		newWeight.wY=this.wY.clone();
		
		return newWeight;
	}
	

}

