package com.zzd.model.weight;



public class Weight {
	/**
	 * 输入门权重，有Wh和Wx两部分和偏置b组成
	 */
	public WeightHX whxI;
	/**
	 * 遗忘门权重，有Wh和Wx两部分和偏置b组成
	 */
	public WeightHX whxF;
	/**
	 * 输出门权重，有Wh和Wx两部分和偏置b组成
	 */
	public WeightHX whxO;
	/**
	 * 当前C权重，有Wh和Wx两部分和偏置b组成
	 */
	public WeightHX whxA;
	/**
	 * 输出层权重，有W和偏置b组成
	 */
	public WeightY wY;
	
	/**
	 * 构造器
	 * @param xDim
	 * @param hiddenDim
	 */
	public Weight(int xDim, int hiddenDim){
		this.whxI=new WeightHX(xDim, hiddenDim);
		this.whxF=new WeightHX(xDim, hiddenDim);
		this.whxO=new WeightHX(xDim, hiddenDim);
		this.whxA=new WeightHX(xDim, hiddenDim);
		
		this.wY=new WeightY(1, hiddenDim);
	}
	
	public Weight(){
		
	}
	/**
	 * 更新权重
	 * @param dWeight
	 * @param lr
	 */
	public void updateHX(DWeight dWeight, double lr){
		this.whxI.update(dWeight.dwhxI, lr);
		this.whxF.update(dWeight.dwhxF, lr);
		this.whxA.update(dWeight.dwhxA, lr);
		this.whxO.update(dWeight.dwhxO, lr);
		
	}
	
	public void updateY(DWeight dWeight, double lr){
		this.wY.update(dWeight.dwY, lr);
	}
	
	public Weight clone(){
		Weight newWeight=new Weight();
		newWeight.whxA=this.whxA.clone();
		newWeight.whxF=this.whxF.clone();
		newWeight.whxI=this.whxI.clone();
		newWeight.whxO=this.whxO.clone();
		newWeight.wY=this.wY.clone();
		
		return newWeight;
	}
	

}

