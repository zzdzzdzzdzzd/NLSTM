package com.zzd.model.weight;

import com.zzd.model.utils.CommonConfigure;

public class TreeWeight {
	
	public int caseNum=CommonConfigure.caseNum;
	
	public int mainFlag=CommonConfigure.mainFlag;
	
	public WeightHX[] whxIs=new WeightHX[caseNum];
	
	public WeightHX[] whxFs=new WeightHX[caseNum];
	
	public WeightHX[] whxAs=new WeightHX[caseNum];
	
	public WeightHX[] whxN1s=new WeightHX[caseNum];
	
	public WeightHX[] whxN2s=new WeightHX[caseNum];
	
	public WeightHX[][] whxRs=new WeightHX[caseNum][];	
	
	public WeightHX[] whxOs=new WeightHX[caseNum];
	
	public WeightY wY;
	
	public TreeWeight(){
		
	}
	
	public TreeWeight(int[][] preLayer){
		for(int i=0;i<caseNum;i++){
			int xDim=(int) CommonConfigure.getPara("xDim"+i);
			int hiddenDim=(int) CommonConfigure.getPara("hiddenDim"+i);
			this.whxIs[i]=new WeightHX(xDim, hiddenDim);
			this.whxFs[i]=new WeightHX(xDim, hiddenDim);
			this.whxAs[i]=new WeightHX(xDim, hiddenDim);
			this.whxN1s[i]=new WeightHX(xDim, hiddenDim);
			this.whxN2s[i]=new WeightHX(xDim, hiddenDim);
			this.whxOs[i]=new WeightHX(xDim, hiddenDim);
			
			int preNum=preLayer[i].length;
			this.whxRs[i]=new WeightHX[preNum];
			for(int j=0;j<preNum;j++){
				this.whxRs[i][j]=new WeightHX(xDim,hiddenDim);
			}			
		}
		int hiddenDim=(int) CommonConfigure.getPara("hiddenDim"+mainFlag);
		
		this.wY=new WeightY(1,hiddenDim);
	}
	
	public void update(TreeDWeight dWeight, double lr){
		for(int i=0;i<caseNum;i++){
			this.whxIs[i].update(dWeight.dwhxIs[i], lr);
			this.whxFs[i].update(dWeight.dwhxFs[i], lr);
			this.whxAs[i].update(dWeight.dwhxAs[i], lr);
			this.whxN1s[i].update(dWeight.dwhxN1s[i], lr);
			this.whxN2s[i].update(dWeight.dwhxN2s[i], lr);
			this.whxOs[i].update(dWeight.dwhxOs[i], lr);
			
			int preNum=dWeight.dwhxRs[i].length;
			for(int j=0;j<preNum;j++){
				this.whxRs[i][j].update(dWeight.dwhxRs[i][j], lr);
			}			
		}
		
		
		this.wY.update(dWeight.dwY, lr);
	}
	
	public TreeWeight clone(){
		TreeWeight newWeight=new TreeWeight();
		for(int i=0;i<caseNum;i++){
			newWeight.whxIs[i]=this.whxIs[i].clone();
			newWeight.whxFs[i]=this.whxFs[i].clone();
			newWeight.whxAs[i]=this.whxAs[i].clone();
			newWeight.whxN1s[i]=this.whxN1s[i].clone();
			newWeight.whxN2s[i]=this.whxN2s[i].clone();
			
			int preNum=this.whxRs[i].length;
			newWeight.whxRs[i]=new WeightHX[preNum];
			for(int j=0;j<preNum;j++){
				newWeight.whxRs[i][j]=this.whxRs[i][j].clone();
			}
			newWeight.whxOs[i]=this.whxOs[i].clone();
			
		}
		newWeight.wY=this.wY.clone();
		return newWeight;
	}
	
	
	
}
