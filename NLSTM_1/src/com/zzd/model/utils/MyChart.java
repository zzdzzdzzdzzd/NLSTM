package com.zzd.model.utils;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class MyChart {

	/**
	 * draw comparison between predictions and observations
	 * @param pre : predictions
	 * @param y : observations
	 */
    public static void drawCompare(double[][] pre, double[][] y, String title) {
    	String applicationTitle="comparison between predictions and observations";
    	String chartTitle=title;
    	String xLabel="periods";
    	String yLabel="wind speed (m/s)";
    	XYSeriesCollection dataSet =new XYSeriesCollection();
    	XYSeries ySeries=new XYSeries("observations");
    	XYSeries preSeries=new XYSeries("predictions");
    	for(int i=0;i<pre.length;i++){
    		ySeries.add(1.0*i, y[i][0]);
    		preSeries.add(1.0*i, pre[i][0]);
    	}
    	dataSet.addSeries(ySeries);
    	dataSet.addSeries(preSeries);
    	XYLineChartFrame chart=new XYLineChartFrame(applicationTitle, chartTitle, xLabel, yLabel, dataSet);
	    chart.pack( );          
	    RefineryUtilities.centerFrameOnScreen( chart );          
	    chart.setVisible( true ); 
    }

}
