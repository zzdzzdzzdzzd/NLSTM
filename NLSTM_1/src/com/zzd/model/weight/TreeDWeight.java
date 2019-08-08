package com.zzd.model.weight;

import com.zzd.model.utils.CommonConfigure;
import com.zzd.model.utils.Matrix;

public class TreeDWeight {
	
	public int caseNum=CommonConfigure.caseNum;
	
	public int mainFlag=CommonConfigure.mainFlag;
	
	public DWeightHX[] dwhxIs=new DWeightHX[caseNum];

	public DWeightHX[] dwhxFs=new DWeightHX[caseNum];
	
	public DWeightHX[] dwhxAs=new DWeightHX[caseNum];
	
	public DWeightHX[] dwhxN1s=new DWeightHX[caseNum];
	
	public DWeightHX[] dwhxN2s=new DWeightHX[caseNum];
	
	public DWeightHX[][] dwhxRs=new DWeightHX[caseNum][];
	
	public DWeightHX[] dwhxOs=new DWeightHX[caseNum];
	
	public DWeightY dwY;
	
	public TreeDWeight(int[][] preLayer){
		for(int i=0;i<caseNum;i++){
			int xDim=(int) CommonConfigure.getPara("xDim"+i);
			int hiddenDim=(int) CommonConfigure.getPara("hiddenDim"+i);
			this.dwhxIs[i]=new DWeightHX(xDim, hiddenDim);
			this.dwhxFs[i]=new DWeightHX(xDim, hiddenDim);
			this.dwhxAs[i]=new DWeightHX(xDim, hiddenDim);
			this.dwhxN1s[i]=new DWeightHX(xDim, hiddenDim);
			this.dwhxN2s[i]=new DWeightHX(xDim, hiddenDim);
			this.dwhxOs[i]=new DWeightHX(xDim, hiddenDim);
			
			int preNum=preLayer[i].length;
			this.dwhxRs[i]=new DWeightHX[preNum];
			for(int j=0;j<preNum;j++){
				this.dwhxRs[i][j]=new DWeightHX(xDim,hiddenDim);
			}			
		}
		int hiddenDim=(int) CommonConfigure.getPara("hiddenDim"+mainFlag);
		
		this.dwY=new DWeightY(1,hiddenDim);
	}
	
	
	public void update(Matrix[] dis,Matrix[] dfs,Matrix[] das,Matrix[] dos,Matrix[] dn1s,Matrix[] dn2s,Matrix[][] drs,Matrix[] preHs,Matrix[] xs,int[][] preLayer){
		for(int i=0;i<caseNum;i++){
			this.dwhxIs[i].calGrad(dis[i], preHs[i], xs[i]);
			this.dwhxFs[i].calGrad(dfs[i], preHs[i], xs[i]);
			this.dwhxAs[i].calGrad(das[i], preHs[i], xs[i]);
			this.dwhxN1s[i].calGrad(dn1s[i], preHs[i], xs[i]);
			this.dwhxN2s[i].calGrad(dn2s[i], preHs[i], xs[i]);
			this.dwhxOs[i].calGrad(dos[i], preHs[i], xs[i]);
			
			int preNum=drs[i].length;
			for(int j=0;j<preNum;j++){
				this.dwhxRs[i][j].calGrad(drs[i][j], preHs[i], xs[i]);
			}

		}
		
		
		
	}
	
	public TreeDWeight add(TreeDWeight dWeight,int[][] preLayer){
		TreeDWeight result=new TreeDWeight(preLayer);
		for(int i=0;i<caseNum;i++){
			result.dwhxIs[i]=this.dwhxIs[i].add(dWeight.dwhxIs[i]);
			result.dwhxFs[i]=this.dwhxFs[i].add(dWeight.dwhxFs[i]);
			result.dwhxAs[i]=this.dwhxAs[i].add(dWeight.dwhxAs[i]);
			result.dwhxN1s[i]=this.dwhxN1s[i].add(dWeight.dwhxN1s[i]);
			result.dwhxN2s[i]=this.dwhxN2s[i].add(dWeight.dwhxN2s[i]);
			result.dwhxOs[i]=this.dwhxOs[i].add(dWeight.dwhxOs[i]);
			int preNum=preLayer[i].length;
			for(int j=0;j<preNum;j++){
				result.dwhxRs[i][j]=this.dwhxRs[i][j].add(dWeight.dwhxRs[i][j]);
			}
			
			
		}
		
		
		result.dwY=this.dwY.add(dWeight.dwY);
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		

}
