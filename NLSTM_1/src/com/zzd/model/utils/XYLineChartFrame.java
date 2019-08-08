package com.zzd.model.utils;

import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

@SuppressWarnings("serial")
public class XYLineChartFrame extends JFrame {

	private JFreeChart xylineChart;

	public XYLineChartFrame(String applicationTitle, String chartTitle, String xLabel, String yLabel,
			XYDataset dataSet) {
		super(applicationTitle);
		xylineChart = ChartFactory.createXYLineChart(chartTitle, xLabel, yLabel, dataSet, PlotOrientation.VERTICAL,
				true, true, false);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		final XYPlot plot = xylineChart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.YELLOW);
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public JFreeChart getXylineChart() {
		return xylineChart;
	}

	public void setXylineChart(JFreeChart xylineChart) {
		this.xylineChart = xylineChart;
	}

}
