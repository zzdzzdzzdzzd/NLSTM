package com.zzd.model.data;

import com.zzd.model.utils.Matrix;

public class TimeSeriesData{

	/**
	 * features of training set
	 */
	protected Matrix trainX;
	/**
	 * labels of training set
	 */
	protected Matrix trainY;
	/**
	 * features of validation set
	 */
	protected Matrix validateX;
	/**
	 * labels of validation set
	 */
	protected Matrix validateY;
	
	/**
	 * original time series
	 */
	private double[] timeSeries;
	/**
	 * extreme values		index  0:min; 1:max
	 */
	private double[] extremeValue;
	/**
	 * all time series are used as training set
	 * @param timeSeries
	 * @param step : historical data step
	 */
	public TimeSeriesData(double[] timeSeries, int step){
		this.timeSeries=timeSeries.clone();
		double[] extremeValue=findExtremeValue(timeSeries);
		this.extremeValue=extremeValue;
		double[] timeSeriesNorm=normalize(timeSeries);
		createTrainData(timeSeriesNorm, step);
	}
	/**
	 * divide training set and validation set using ratio sep
	 * @param timeSeries
	 * @param step
	 * @param sep
	 */
	public TimeSeriesData(double[] timeSeries, int step, double sep){
		this.timeSeries=timeSeries.clone();
		double[] extremeValue=findExtremeValue(timeSeries);
		this.extremeValue=extremeValue;
		double[] timeSeriesNorm=normalize(timeSeries);
		createTrainData(timeSeriesNorm, step,sep);
		createValidateData(timeSeriesNorm, step,sep);
	}
	/**
	 * divide training set and validation set using ratio sep, multi-factors
	 * @param timeSeries
	 * @param step
	 * @param sep
	 * @param mainFlag
	 */
	public TimeSeriesData(double[][] timeSeries, int step, double sep, int mainFlag){
		Matrix trainX=null;
		Matrix trainY=null;
		Matrix validateX=null;
		Matrix validateY=null;
		for(int i=0;i<timeSeries[0].length;i++){
			double[] series=new double[timeSeries.length];
			for(int j=0;j<timeSeries.length;j++){
				series[j]=timeSeries[j][i];
			}
			
			this.timeSeries=series.clone();
			double[] extremeValue=findExtremeValue(series);
			this.extremeValue=extremeValue;
			double[] seriesNorm=normalize(series);
			createTrainData(seriesNorm, step,sep);
			createValidateData(seriesNorm, step,sep);
			if(i==0){
				trainX=this.trainX.copy();
				validateX=this.validateX.copy();
			}else{
				trainX=trainX.hContact(this.trainX);
				validateX=validateX.hContact(this.validateX);
			}
			
			if(i==mainFlag){
				trainY=this.trainY.copy();
				validateY=this.validateY.copy();
			}
			
		}
		
		this.trainX=trainX.copy();
		this.trainY=trainY.copy();
		this.validateX=validateX.copy();
		this.validateY=validateY.copy();
		
	}
	
	/**
	 * normalization
	 * @param data
	 * @return
	 */
	private double[] normalize(double[] data){
		double[] dataNorm=new double[data.length];		
		double min=extremeValue[0];
		double max=extremeValue[1];
		for(int i=0;i<data.length;i++){
			dataNorm[i]=(data[i]-min)/(max-min);
		}
		
		return dataNorm;
	}
	/**
	 * restore normalization
	 * @param dataNorm
	 * @return
	 */
	public double[] restore(double[] dataNorm){
		double[] data=new double[dataNorm.length];
		double min=extremeValue[0];
		double max=extremeValue[1];
		for(int i=0;i<dataNorm.length;i++){
			data[i]=min+(max-min)*dataNorm[i];
		}
		
		return data;
	}
	
	/**
	 * obtain min and max
	 * @param data
	 * @return 0:min  1：max
	 */
	private double[] findExtremeValue(double[] data){
		double[] extremeValue=new double[2];
		extremeValue[0]=Double.POSITIVE_INFINITY;
		extremeValue[1]=Double.NEGATIVE_INFINITY;
		
		for(int i=0;i<data.length;i++){
			if(data[i]<extremeValue[0]){
				extremeValue[0]=data[i];
			}
			
			if(data[i]>extremeValue[1]){
				extremeValue[1]=data[i];
			}
			
		}
		
		return extremeValue;
		
	}
	
	/**
	 * construct training set
	 * @param data
	 * @param step
	 */
	private void createTrainData(double[] data, int step){
		int len=data.length;
		trainX=new Matrix(len-step, step);
		trainY=new Matrix(len-step,1);
		
		for(int i=0;i<trainX.getRowDimension();i++){
			for(int j=0;j<step;j++){
				trainX.set(i, j, data[i+j]);
			}
			trainY.set(i, 0, data[i+step]);
		}
	}
	/**
	 * construct training set
	 * @param data
	 * @param step
	 */
	private void createTrainData(double[] data, int step, double sep){
		int len=(int) (data.length*sep)-1;
		trainX=new Matrix(len-step, step);
		trainY=new Matrix(len-step,1);
		
		for(int i=0;i<trainX.getRowDimension();i++){
			for(int j=0;j<step;j++){
				trainX.set(i, j, data[i+j]);
			}
			trainY.set(i, 0, data[i+step]);
		}
	}
	
	/**
	 * construct validation set
	 * @param data
	 * @param step
	 */
	private void createValidateData(double[] data, int step, double sep){
		int sepIndex=(int) (data.length*sep)-1;
		int len=data.length-sepIndex;
		validateX=new Matrix(len-step, step);
		validateY=new Matrix(len-step,1);
		
		for(int i=0;i<validateX.getRowDimension();i++){
			for(int j=0;j<step;j++){
				validateX.set(i, j, data[sepIndex+i+j-1]);
			}
			validateY.set(i, 0, data[sepIndex+i+step-1]);
		}
	}
	/***********getters and setters************/
	public Matrix getTrainX() {
		return trainX;
	}

	public void setTrainX(Matrix trainX) {
		this.trainX = trainX;
	}

	public Matrix getTrainY() {
		return trainY;
	}

	public void setTrainY(Matrix trainY) {
		this.trainY = trainY;
	}

	public double[] getTimeSeries() {
		return timeSeries;
	}

	public void setTimeSeries(double[] timeSeries) {
		this.timeSeries = timeSeries;
	}

	public double[] getExtremeValue() {
		return extremeValue;
	}

	public void setExtremeValue(double[] extremeValue) {
		this.extremeValue = extremeValue;
	}

	public Matrix getValidateX() {
		return validateX;
	}

	public void setValidateX(Matrix validateX) {
		this.validateX = validateX;
	}

	public Matrix getValidateY() {
		return validateY;
	}

	public void setValidateY(Matrix validateY) {
		this.validateY = validateY;
	}
	/***********getters and setters************/	
	
}
