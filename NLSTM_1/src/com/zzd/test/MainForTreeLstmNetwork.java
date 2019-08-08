package com.zzd.test;

import java.util.Map;

import com.zzd.model.data.TimeSeriesData;
import com.zzd.model.nLstmNetwork.TreeLstmNetwork;
import com.zzd.model.utils.CommonConfigure;
import com.zzd.model.utils.FileOperatorUtil;
import com.zzd.model.utils.LossFunction;
import com.zzd.model.utils.Matrix;
import com.zzd.model.utils.MyChart;

public class MainForTreeLstmNetwork {
	
	public static void main(String[] args) {

	}


	/**
	 * predict Wind Speed After Shallow Purning
	 */
	public double[][] model5(String timeFlag, boolean isDraw) {
		int[] rowIndex=new int[747];
		int start=0;
		for(int i=1;i<=rowIndex.length;i++){
			rowIndex[i-1]=start+i;
		}
		int[] columnIndex={8, 9, 8, 9, 4, 3, 7, 4, 3, 5, 5, 7, 6};
		String path="resources/data/nData/sample.xlsx";
		Matrix nData=FileOperatorUtil.readExcel(path, rowIndex, columnIndex, 0);
		
		// child nodes
		int[][] preLayer=new int[][]{{}, {}, {}, {}, {}, {}, {}, {0, 1}, {2, 3}, {4, 5},
			{6, 7, 8}, {9}, {10, 11}};
		// parent node
		int[] nextLayer=new int[]{7, 7, 8, 8, 9, 9, 10, 10, 10, 11, 12, 12, -1};
		// nodes in each layer
		int[][] caseRecord=new int[][]{{0, 1, 2, 3, 4, 5}, {6, 7, 8, 9}, {10, 11}, {12}};
		// index of each node in parent node's child nodes
		int[] indexInNext=new int[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 0, 0, 1, -1};

		int epochs=1000;
		double lr=0.01;
		
		int caseNum=nData.getColumnDimension();
		CommonConfigure.caseNum=caseNum;
		CommonConfigure.mainFlag=caseNum-1;
		for(int i=0;i<caseNum;i++){
			int xDim=2;
			CommonConfigure.configurePara("xDim"+i, xDim);
			CommonConfigure.configurePara("hiddenDim"+i, 4);				
		}
		
		int times=1;
		double[][][] contents=new double[times][][];
		double[][] metrics = new double[times][];
		for(int t=0;t<times;t++){
			TreeLstmNetwork nlstm=new TreeLstmNetwork(preLayer, nextLayer, caseRecord, indexInNext, epochs, lr);
			TimeSeriesData[] orgDatas = new TimeSeriesData[caseNum];
			for(int i=0;i<caseNum;i++){
				double[][] dataTwo= nData.getMatrix(0, nData.getRowDimension()-1, i, i).getArray();
				double[] data=new double[dataTwo.length];
				for(int j=0;j<data.length;j++){
					data[j]=dataTwo[j][0];
				}
				int step=(int) CommonConfigure.getPara("xDim"+i);
				TimeSeriesData orgData=new TimeSeriesData(data,step,0.8);
				orgDatas[i] = orgData;
				Matrix trainX=orgData.getTrainX();
				Matrix trainY=orgData.getTrainY();
				Matrix validateX=orgData.getValidateX();
				Matrix validateY=orgData.getValidateY();
				nlstm.addCase(i,trainX,trainY,validateX,validateY);			
			}
			
			nlstm.train(32);
			
			Map<String,double[]> result=nlstm.validateOutput();
			
			double[] preNorm=result.get("preNorm");
			double[] yNorm=result.get("yNorm");
			double[] pre=orgDatas[caseNum-1].restore(preNorm);
			double[] y=orgDatas[caseNum-1].restore(yNorm);
			
			Matrix preMatrix = new Matrix(pre, 1);
			Matrix labelMatrix = new Matrix(y, 1);
			if(isDraw){
				MyChart.drawCompare(preMatrix.transpose().getArray(), labelMatrix.transpose().getArray(), "model 5");				
			}
			metrics[t] = LossFunction.calLoss(preMatrix.transpose(), labelMatrix.transpose(), true);
			int len=preNorm.length;
			double[][] content=new double[len][4];
			for(int i=0;i<len;i++){
				content[i][0]=preNorm[i];
				content[i][1]=yNorm[i];
				content[i][2]=pre[i];
				content[i][3]=y[i];
			}		
			contents[t]=content;
		}
			
		String excelPath="resources/data/result/"+timeFlag+"/NLSTM_AfterShallowPurning_"+timeFlag+".xls";
		FileOperatorUtil.exportPredictions(contents, excelPath);
		return metrics;
	}

	/**
	 * predict Wind Speed After Depth Purning
	 */
	public double[][] model8(String timeFlag, boolean isDraw) {
		int[] rowIndex=new int[747];
		int start=0;
		for(int i=1;i<=rowIndex.length;i++){
			rowIndex[i-1]=start+i;
		}
		int[] columnIndex={7, 4, 3, 5, 5, 7, 6};
		String path="resources/data/nData/sample.xlsx";
		Matrix nData=FileOperatorUtil.readExcel(path, rowIndex, columnIndex, 0);
		

		int[][] preLayer=new int[][]{{}, {}, {}, {}, {0,1,2}, {3}, {4, 5}};
		int[] nextLayer=new int[]{4, 4, 4, 5, 6, 6, -1};
		int[][] caseRecord=new int[][]{{0, 1, 2, 3}, {4, 5}, {6}};
		int[] indexInNext=new int[]{0, 1, 2, 0, 0, 1, -1};
		
		int epochs=1000;
		double lr=0.01;
		
		int caseNum=nData.getColumnDimension();
		CommonConfigure.caseNum=caseNum;
		CommonConfigure.mainFlag=caseNum-1;
		for(int i=0;i<caseNum;i++){
			int xDim=2;
			CommonConfigure.configurePara("xDim"+i, xDim);
			CommonConfigure.configurePara("hiddenDim"+i, 4);				
		}
		
		int times=1;
		double[][][] contents=new double[times][][];
		double[][] metrics = new double[times][];
		for(int t=0;t<times;t++){
			TreeLstmNetwork nlstm=new TreeLstmNetwork(preLayer, nextLayer, caseRecord, indexInNext, epochs, lr);
			TimeSeriesData[] orgDatas = new TimeSeriesData[caseNum];
			for(int i=0;i<caseNum;i++){
				double[][] dataTwo= nData.getMatrix(0, nData.getRowDimension()-1, i, i).getArray();
				double[] data=new double[dataTwo.length];
				for(int j=0;j<data.length;j++){
					data[j]=dataTwo[j][0];
				}
				int step=(int) CommonConfigure.getPara("xDim"+i);
				TimeSeriesData orgData=new TimeSeriesData(data,step,0.8);
				orgDatas[i] = orgData;
				Matrix trainX=orgData.getTrainX();
				Matrix trainY=orgData.getTrainY();
				Matrix validateX=orgData.getValidateX();
				Matrix validateY=orgData.getValidateY();
				nlstm.addCase(i,trainX,trainY,validateX,validateY);			
			}
			
			nlstm.train(32);
			
			Map<String,double[]> result=nlstm.validateOutput();
			
			double[] preNorm=result.get("preNorm");
			double[] yNorm=result.get("yNorm");
			double[] pre=orgDatas[caseNum-1].restore(preNorm);
			double[] y=orgDatas[caseNum-1].restore(yNorm);
			
			Matrix preMatrix = new Matrix(pre, 1);
			Matrix labelMatrix = new Matrix(y, 1);
			if(isDraw){
				MyChart.drawCompare(preMatrix.transpose().getArray(), labelMatrix.transpose().getArray(), "model 8");				
			}
			metrics[t] = LossFunction.calLoss(preMatrix.transpose(), labelMatrix.transpose(), true);
			int len=preNorm.length;
			double[][] content=new double[len][4];
			for(int i=0;i<len;i++){
				content[i][0]=preNorm[i];
				content[i][1]=yNorm[i];
				content[i][2]=pre[i];
				content[i][3]=y[i];
			}		
			contents[t]=content;
		}
			
		String excelPath="resources/data/result/"+timeFlag+"/NLSTM_AfterDepthPurning_"+timeFlag+".xls";
		FileOperatorUtil.exportPredictions(contents, excelPath);
		return metrics;
	}
	


}
