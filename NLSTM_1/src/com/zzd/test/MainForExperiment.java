/**
 * @author  zzd E-mail: zzd_zzd@hust.edu.cn
 * @date 创建时间：2019年8月7日 下午4:47:13
 * @version 1.0
 * @parameter 
 * @return 
 */
package com.zzd.test;

import com.zzd.model.utils.FileOperatorUtil;

/**
 * @author zzd
 *
 */
public class MainForExperiment {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
		String timeFlag = ""+time;
		boolean isDraw = true;
		double[][][] metrics = new double[1][5][];
		MainForTreeLstmNetwork exe1 = new MainForTreeLstmNetwork();
		double[][] metric = exe1.model5(timeFlag, isDraw);
		metrics[0][0] = metric[0];
		metric = exe1.model8(timeFlag, isDraw);
		metrics[0][1] = metric[0];
		MainForMyLstm exe2 = new MainForMyLstm();
		metric = exe2.model1(timeFlag, isDraw);
		metrics[0][2] = metric[0];
		metric = exe2.model2(timeFlag, isDraw);
		metrics[0][3] = metric[0];
		MainForMyGru exe3 = new MainForMyGru();
		metric = exe3.model6(timeFlag, isDraw);
		metrics[0][4] = metric[0];
		
		String excelPath="resources/data/result/"+timeFlag+"/Metrics_"+timeFlag+".xls";
		FileOperatorUtil.exportMetrics(metrics, excelPath);
		
		
		
		
	}

}
