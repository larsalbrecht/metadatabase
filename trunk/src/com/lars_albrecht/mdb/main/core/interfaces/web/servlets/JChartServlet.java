/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jCharts.axisChart.AxisChart;
import org.jCharts.axisChart.customRenderers.axisValue.renderers.ValueLabelPosition;
import org.jCharts.axisChart.customRenderers.axisValue.renderers.ValueLabelRenderer;
import org.jCharts.chartData.AxisChartDataSet;
import org.jCharts.chartData.DataSeries;
import org.jCharts.chartData.interfaces.IAxisDataSeries;
import org.jCharts.encoders.ServletEncoderHelper;
import org.jCharts.properties.AxisProperties;
import org.jCharts.properties.BarChartProperties;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.DataAxisProperties;
import org.jCharts.properties.LegendProperties;
import org.jCharts.properties.PropertyException;
import org.jCharts.properties.util.ChartFont;
import org.jCharts.types.ChartType;

import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.controller.MainController;

/**
 * @author lalbrecht
 * 
 */
public class JChartServlet extends HttpServlet {

	public static final int				TYPE_ADDSPERDAY		= 10;
	private int							type				= -1;

	private MainController				mainController		= null;

	private BarChartProperties			barChartProperties;

	private LegendProperties			legendProperties	= null;
	private AxisProperties				axisProperties		= null;
	private ChartProperties				chartProperties		= null;

	private final int					width				= 550;
	private final int					height				= 360;

	ConcurrentHashMap<Integer, Integer>	valuesAddsPerDay	= null;

	public JChartServlet(final MainController mainController, final int type) {
		this.mainController = mainController;
		this.type = type;
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setStatus(HttpServletResponse.SC_OK);

			switch (this.type) {
				case TYPE_ADDSPERDAY:
					this.initTypeAddsPerDay(request, response);
					this.generateTypeAddsPerDay(request, response);
					break;
				default:
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void generateTypeAddsPerDay(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			final ConcurrentHashMap<Integer, Integer> valueList = this.mainController.getDataHandler().getUpdatedCountByDay();

			final String[] xAxisLabels = new String[valueList.size()];
			int i = 0;

			for (final Integer valueEntry : valueList.keySet()) {
				xAxisLabels[i] = Helper.getFormattedTimestamp(valueEntry.longValue(), "dd.MM.yyyy");
				i++;
			}

			final String xAxisTitle = "Tage";
			final String yAxisTitle = "Hinzugefügte Dateien";
			final String title = "Hinzugefügte Dateien pro Tag";
			final IAxisDataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);

			final double[][] data = new double[1][valueList.size()];
			i = 0;
			for (final Integer dataEntry : valueList.values()) {
				data[0][i] = dataEntry;
				i++;
			}

			final String[] legendLabels = {
				"Anzahl der Dateien"
			};
			final Paint[] paints = new Paint[] {
				Helper.hex2Rgb("#49B3FF")
			};
			dataSeries.addIAxisPlotDataSet(new AxisChartDataSet(data, legendLabels, paints, ChartType.BAR, this.barChartProperties));

			final AxisChart axisChart = new AxisChart(dataSeries, this.chartProperties, this.axisProperties, this.legendProperties,
					this.width, this.height);
			ServletEncoderHelper.encodeJPEG13(axisChart, 1.0f, response);
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void initTypeAddsPerDay(final HttpServletRequest request, final HttpServletResponse response) {
		this.valuesAddsPerDay = this.mainController.getDataHandler().getCreatedCountByDay();

		this.legendProperties = new LegendProperties();
		this.chartProperties = new ChartProperties();
		this.axisProperties = new AxisProperties(false);
		final ChartFont axisScaleFont = new ChartFont(new Font("Verdana", Font.PLAIN, 13), Color.black);
		this.axisProperties.getXAxisProperties().setScaleChartFont(axisScaleFont);
		this.axisProperties.getYAxisProperties().setScaleChartFont(axisScaleFont);

		final ChartFont axisTitleFont = new ChartFont(new Font("Verdana", Font.PLAIN, 14), Color.black);
		this.axisProperties.getXAxisProperties().setTitleChartFont(axisTitleFont);
		this.axisProperties.getYAxisProperties().setTitleChartFont(axisTitleFont);

		final DataAxisProperties dataAxisProperties = (DataAxisProperties) this.axisProperties.getYAxisProperties();

		int maxValue = 0;
		for (final Integer value : this.valuesAddsPerDay.values()) {
			if (value > maxValue) {
				maxValue = value;
			}
		}

		try {
			dataAxisProperties.setUserDefinedScale(0, maxValue);
		} catch (final PropertyException propertyException) {
			propertyException.printStackTrace();
		}

		dataAxisProperties.setRoundToNearest(2);

		final ChartFont titleFont = new ChartFont(new Font("Verdana", Font.PLAIN, 14), Color.black);
		this.chartProperties.setTitleFont(titleFont);

		this.barChartProperties = new BarChartProperties();

		final ValueLabelRenderer valueLabelRenderer = new ValueLabelRenderer(false, false, true, -1);
		valueLabelRenderer.setValueLabelPosition(ValueLabelPosition.ON_TOP);
		valueLabelRenderer.useVerticalLabels(false);
		this.barChartProperties.addPostRenderEventListener(valueLabelRenderer);
	}

}
