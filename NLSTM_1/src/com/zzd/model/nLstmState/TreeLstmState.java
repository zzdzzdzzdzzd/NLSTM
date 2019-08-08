package com.zzd.model.nLstmState;

import com.zzd.model.utils.CommonConfigure;
import com.zzd.model.utils.Matrix;
import com.zzd.model.weight.TreeWeight;
import com.zzd.model.weight.WeightHX;

public class TreeLstmState {
	
	public Matrix[][] is;
	
	public Matrix[][] fs;
	
	public Matrix[][] as;
	
	public Matrix[][] cs;
	
	public Matrix[][][] rs;
	
	public Matrix[][] Rs;
	
	public Matrix[][] n1s;
	
	public Matrix[][] n2s;
	
	public Matrix[][] Ns;
	
	public Matrix[][] os;
	
	public Matrix[][] hs;
	
	public Matrix[] ys;
	
	public int step;
	
	public int currentStep;
	
	public int caseNum=CommonConfigure.caseNum;
	
	public int mainFlag=CommonConfigure.mainFlag;
	
	public TreeLstmState(int step, int[][] preLayer){
		this.step=step+1;
		this.currentStep=1;
		
		this.is=new Matrix[step+1][caseNum];
		this.fs=new Matrix[step+1][caseNum];
		this.as=new Matrix[step+1][caseNum];
		this.n1s=new Matrix[step+1][caseNum];
		this.n2s=new Matrix[step+1][caseNum];
		this.cs=new Matrix[step+1][caseNum];
		this.Rs=new Matrix[step+1][caseNum];
		this.Ns=new Matrix[step+1][caseNum];
		this.rs=new Matrix[step+1][caseNum][];
		this.os=new Matrix[step+1][caseNum];
		this.hs=new Matrix[step+1][caseNum];
		this.ys=new Matrix[step];
		
		for(int i=0;i<step+1;i++){
			for(int j=0;j<caseNum;j++){
				int hiddenDim=(int) CommonConfigure.getPara("hiddenDim"+j);
				is[i][j]=new Matrix(hiddenDim,1);
				fs[i][j]=new Matrix(hiddenDim,1);
				as[i][j]=new Matrix(hiddenDim,1);
				n1s[i][j]=new Matrix(hiddenDim,1);
				n2s[i][j]=new Matrix(hiddenDim,1);
				cs[i][j]=new Matrix(hiddenDim,1);
				Rs[i][j]=new Matrix(hiddenDim,1);
				Ns[i][j]=new Matrix(hiddenDim,1);
				os[i][j]=new Matrix(hiddenDim,1);
				hs[i][j]=new Matrix(hiddenDim,1);
				
				int preNum=preLayer[j].length;
				this.rs[i][j]=new Matrix[preNum];
				for(int k=0;k<preNum;k++){
					this.rs[i][j][k]=new Matrix(hiddenDim,1);
				}				
			}
			int hiddenDim=(int) CommonConfigure.getPara("hiddenDim"+mainFlag);
			
			if(i+1<step){
				ys[i]=new Matrix(hiddenDim,1);	
			}
		
		}
	}
	
	public void update(TreeWeight weight, Matrix[] xSingle, int[][] preLayer){
		Matrix[] preHts=this.hs[currentStep-1];
		for(int i=0;i<caseNum;i++){
			this.is[currentStep][i]=this.calGate(weight.whxIs[i], preHts[i], xSingle[i], "sigmoid");
			this.fs[currentStep][i]=this.calGate(weight.whxFs[i], preHts[i], xSingle[i], "sigmoid");
			this.as[currentStep][i]=this.calGate(weight.whxAs[i], preHts[i], xSingle[i], "tanh");
			this.os[currentStep][i]=this.calGate(weight.whxOs[i], preHts[i], xSingle[i], "sigmoid");
			
			Matrix fc=fs[currentStep][i].arrayTimes(cs[currentStep-1][i]);
			Matrix ia=is[currentStep][i].arrayTimes(as[currentStep][i]);
			this.cs[currentStep][i]=fc.plus(ia);
			
			Matrix tc=Matrix.tanh(this.cs[currentStep][i]);
			this.hs[currentStep][i]=this.os[currentStep][i].arrayTimes(tc);
			
			
			
			int preNum=preLayer[i].length;
			for(int j=0;j<preNum;j++){
				int preNodeNum=preLayer[i][j];
				this.rs[currentStep][i][j]=this.calGate(weight.whxRs[i][j], preHts[i], xSingle[i], "sigmoid");
				if(j==0){
					this.Rs[currentStep][i]=this.rs[currentStep][i][j].arrayTimes(this.Ns[currentStep][preNodeNum]);
				}else{
					this.Rs[currentStep][i].plusEquals(this.rs[currentStep][i][j].arrayTimes(this.Ns[currentStep][preNodeNum]));
				}
			}
			
			this.n1s[currentStep][i]=this.calGate(weight.whxN1s[i], preHts[i], xSingle[i], "sigmoid");
			this.n2s[currentStep][i]=this.calGate(weight.whxN2s[i], preHts[i], xSingle[i], "sigmoid");
			Matrix m1=this.n1s[currentStep][i].arrayTimes(this.Rs[currentStep][i]);
			Matrix m2=this.n2s[currentStep][i].arrayTimes(this.hs[currentStep][i]);
			this.Ns[currentStep][i]=m1.plus(m2);
		}
		
		
		Matrix yz=weight.wY.w.times(this.Ns[currentStep][mainFlag]).plus(weight.wY.b);
		this.ys[currentStep-1]=Matrix.sigmoid(yz);			
		currentStep++;		
		
	}
	
	
	/**
	 * 计算门限结构
	 * @param whx
	 * @param preHt
	 * @param x
	 * @param activation
	 * @return
	 */
	private Matrix calGate(WeightHX whx, Matrix preHt, Matrix x, String activation){
		Matrix zh=whx.wh.times(preHt);
		Matrix zx=whx.wx.times(x.transpose());//自己修改处1：添加了转置
		Matrix z=zh.plus(zx).plus(whx.b);
		Matrix a=z;
		if(activation.equals("sigmoid")){
			a=Matrix.sigmoid(z);
		}else if(activation.equals("tanh")){
			a=Matrix.tanh(z);
		}else{
			throw new IllegalArgumentException("activation function not implemented!");
		}
		
		return a;
	}	
	
	
}
