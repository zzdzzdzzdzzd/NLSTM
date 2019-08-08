package com.zzd.test;

import java.util.Map;

import com.zzd.model.data.TimeSeriesData;
import com.zzd.model.nLstmNetwork.GruNetwork;
import com.zzd.model.utils.FileOperatorUtil;
import com.zzd.model.utils.LossFunction;
import com.zzd.model.utils.Matrix;
import com.zzd.model.utils.MyChart;

public class MainForMyGru {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	}
	
	public double[][] model6(String timeFlag, boolean isDraw){
		int[] rowIndex=new int[747];
		for(int i=1;i<=rowIndex.length;i++){
			rowIndex[i-1]=i;
		}
		int[] columnIndex={3, 4, 5, 7, 8, 9, 6};
		String path="resources/data/nData/sample.xlsx";
		Matrix eData=FileOperatorUtil.readExcel(path, rowIndex, columnIndex, 0);
		
		int nodeFlag=columnIndex.length-1;
		
		int step=2;
		
		TimeSeriesData orgData=new TimeSeriesData(eData.getArray(),step,0.8, nodeFlag);
		Matrix trainX=orgData.getTrainX();
		Matrix trainY=orgData.getTrainY();
		Matrix validateX=orgData.getValidateX();
		Matrix validateY=orgData.getValidateY();
		
		int epochs=1000;
		double lr=0.01;
		int times=1;
		double[][][] contents=new double[times][][];
		double[][] metrics = new double[times][];
		for(int t=0;t<times;t++){
			GruNetwork gru=new GruNetwork(step*columnIndex.length, 16);
			gru.train(trainX, trainY, validateX, validateY, 32, lr, epochs);

			Map<String,double[]> result=gru.validateOutput(trainX, trainY, validateX, validateY);
			
			double[] preNorm=result.get("preNorm");
			double[] yNorm=result.get("yNorm");
			double[] pre=orgData.restore(preNorm);
			double[] y=orgData.restore(yNorm);
			
			Matrix preMatrix = new Matrix(pre, 1);
			Matrix labelMatrix = new Matrix(y, 1);
			if(isDraw){
				MyChart.drawCompare(preMatrix.transpose().getArray(), labelMatrix.transpose().getArray(), "model 6");				
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
			
		String excelPath="resources/data/result/"+timeFlag+"/GRU_AllFeatures_"+timeFlag+".xls";
		FileOperatorUtil.exportPredictions(contents, excelPath);
		return metrics;

	}
	
	

}
