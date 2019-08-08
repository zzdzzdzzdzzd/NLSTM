package com.zzd.model.utils;



public class LossFunction {
	
	/**
	 * calculate point prediction metrics
	 * @param pre : predictions
	 * @param label : observations
	 * @param lossFunctionName : name of metrics
	 * @return : metrics
	 */
	public static double[] calLoss(Matrix pre, Matrix label, boolean isPrint, String ... lossFunctionName){
		if(lossFunctionName==null||lossFunctionName.length==0){
			lossFunctionName=new String[]{"MAE","MSE","RMSE","MAPE","R2"};
		}
		
		int len=lossFunctionName.length;
		
		double[] losses=new double[len];
		for(int i=0;i<len;i++){
			switch(lossFunctionName[i]){
			case "MAE": losses[i]=LossFunction.calMAE(pre, label);break;
			case "MSE": losses[i]=LossFunction.calMSE(pre, label);break;
			case "RMSE": losses[i]=LossFunction.calRMSE(pre, label);break;
			case "MAPE": losses[i]=LossFunction.calMAPE(pre, label);break;
			case "R2": losses[i]=LossFunction.calRsquare(pre, label);break;
			default: throw new IllegalArgumentException("loss function name is wrong!");
			}
			if(isPrint){
				System.out.println(lossFunctionName[i] + ":" + losses[i]);				
			}
		}		
		return losses;
	}
	
	/**
	 * RMSE
	 * @param pre : predictions
	 * @param label : observations
	 * @return : RMSE
	 */
	public static double calRMSE(Matrix pre, Matrix label){
		double loss=0.0;
		for(int i=0;i<pre.getRowDimension();i++){
			loss+=Math.pow(pre.get(i, 0)-label.get(i, 0), 2);
		}
		loss/=pre.getRowDimension();
		loss=Math.sqrt(loss);
		
		return loss;
	}
	/**
	 * 1/2 MSE
	 * @param pre : predictions
	 * @param label : observations
	 * @return : 1/2 MSE
	 */
	public static double calDoubleMSE(Matrix pre, Matrix label){
		double loss=0.0;
		for(int i=0;i<pre.getRowDimension();i++){
			loss+=0.5*Math.pow(pre.get(i, 0)-label.get(i, 0), 2);
		}
		
		loss/=pre.getRowDimension();
		return loss;
	}
	/**
	 * MAE
	 * @param pre : predictions
	 * @param label : observations
	 * @return : MAE
	 */
	public static double calMAE(Matrix pre, Matrix label){
		double loss=0.0;
		for(int i=0;i<pre.getRowDimension();i++){
			loss+=Math.abs(pre.get(i, 0)-label.get(i, 0));
		}
		
		loss/=pre.getRowDimension();
		return loss;		
	}
	/**
	 * MSE
	 * @param pre : predictions
	 * @param label : observations
	 * @return : MSE
	 */
	public static double calMSE(Matrix pre, Matrix label){
		double loss=0.0;
		for(int i=0;i<pre.getRowDimension();i++){
			loss+=Math.pow(pre.get(i, 0)-label.get(i, 0), 2);
		}
		loss/=pre.getRowDimension();

		return loss;
	}
	/**
	 * MAPE
	 * @param pre : predictions
	 * @param label : observations
	 * @return : MAPE
	 */
	public static double calMAPE(Matrix pre, Matrix label){
		double loss=0.0;
		for(int i=0;i<pre.getRowDimension();i++){
			double num=(pre.get(i, 0)-label.get(i, 0))/label.get(i, 0);
			loss+=Math.abs(num);
		}
		
		loss/=pre.getRowDimension();
		return loss;
	}
	/**
	 * R2
	 * @param pre : predictions
	 * @param label : observations
	 * @return : R2
	 */
	public static double calRsquare(Matrix pre, Matrix label){
		double labelAvg=label.avg();
		double res=0.0;
		double rot=0.0;
		for(int i=0;i<pre.getRowDimension();i++){
			res+=Math.pow(pre.get(i, 0)-label.get(i, 0), 2);
			rot+=Math.pow(labelAvg-label.get(i, 0), 2);
		}
		double rSquare=1-res/rot;
		return rSquare;
	}
	
}
