/**
 * 
 */
package com.lars_albrecht.mdb.main.core.graphics;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jCharts.axisChart.AxisChart;
import org.jCharts.chartData.AxisChartDataSet;
import org.jCharts.chartData.ChartDataException;
import org.jCharts.chartData.DataSeries;
import org.jCharts.encoders.JPEGEncoder;
import org.jCharts.properties.AxisProperties;
import org.jCharts.properties.BarChartProperties;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.LegendProperties;
import org.jCharts.properties.PropertyException;
import org.jCharts.types.ChartType;

/**
 * @author lalbrecht
 * 
 */
public class Chart {

	public static void generateChart(final String[] xAxisLabels,
			final String xAxisTitle,
			final String yAxisTitle,
			final String title,
			final double[][] data,
			final String[] legendLabels,
			final File newFile) throws ChartDataException, FileNotFoundException, PropertyException, IOException {
		final DataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);

		final Paint[] paints = new Paint[] {
			Color.blue.brighter().brighter()
		};

		final BarChartProperties barChartProperties = new BarChartProperties();
		final AxisChartDataSet axisChartDataSet = new AxisChartDataSet(data, legendLabels, paints, ChartType.BAR, barChartProperties);
		dataSeries.addIAxisPlotDataSet(axisChartDataSet);
		final ChartProperties chartProperties = new ChartProperties();
		final AxisProperties axisProperties = new AxisProperties();
		final LegendProperties legendProperties = new LegendProperties();
		final AxisChart axisChart = new AxisChart(dataSeries, chartProperties, axisProperties, legendProperties, 500, 500);

		JPEGEncoder.encode(axisChart, 1.0F, new FileOutputStream(newFile));
	}

}
