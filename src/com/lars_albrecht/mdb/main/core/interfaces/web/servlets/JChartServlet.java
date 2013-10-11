/**
 * 
 */
package com.lars_albrecht.mdb.main.core.interfaces.web.servlets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.util.LinkedHashMap;

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
import org.jCharts.properties.AxisTypeProperties;
import org.jCharts.properties.BarChartProperties;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.DataAxisProperties;
import org.jCharts.properties.LabelAxisProperties;
import org.jCharts.properties.PropertyException;
import org.jCharts.properties.util.ChartFont;
import org.jCharts.properties.util.ChartStroke;
import org.jCharts.types.ChartType;

import com.lars_albrecht.general.utilities.Helper;
import com.lars_albrecht.mdb.main.core.controller.MainController;

/**
 * @author lalbrecht
 * 
 *         TODO refactor
 */
public class JChartServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long				serialVersionUID		= -5540792818589675356L;

	public static final int					TYPE_ADDSPERDAY			= 10;
	public static final int					TYPE_UPDATESPERDAY		= 11;
	private int								type					= -1;

	private MainController					mainController			= null;

	private BarChartProperties				barChartProperties		= null;

	private AxisProperties					axisProperties			= null;
	private ChartProperties					chartProperties			= null;

	private final int						width					= 500;
	private final int						height					= 600;

	private LinkedHashMap<Integer, Integer>	valuesAddsUpdatesPerDay	= null;

	public JChartServlet(final MainController mainController, final int type) {
		this.mainController = mainController;
		this.type = type;
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setStatus(HttpServletResponse.SC_OK);

			switch (this.type) {
				case TYPE_UPDATESPERDAY:
				case TYPE_ADDSPERDAY:
					this.initTypeAddsUpdatesPerDay(request, response);
					this.generateTypeAddsUpdatesPerDay(request, response);
					break;
				default:
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void generateTypeAddsUpdatesPerDay(final HttpServletRequest request, final HttpServletResponse response) {
		try {

			final String[] xAxisLabels = new String[this.valuesAddsUpdatesPerDay.size()];
			int i = 0;

			for (final Integer valueEntry : this.valuesAddsUpdatesPerDay.keySet()) {
				xAxisLabels[i] = Helper.getFormattedTimestamp(valueEntry.longValue(), "dd.MM.yyyy");
				i++;
			}

			final String xAxisTitle = "Dateien";
			final String yAxisTitle = "Tage";
			final String title = "Dateien pro Tag";
			final IAxisDataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);

			final double[][] data = new double[1][this.valuesAddsUpdatesPerDay.size()];
			i = 0;
			for (final Integer dataEntry : this.valuesAddsUpdatesPerDay.values()) {
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
			final AxisChart axisChart = new AxisChart(dataSeries, this.chartProperties, this.axisProperties, null, this.width, this.height);

			ServletEncoderHelper.encodeJPEG13(axisChart, 1.0f, response);
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void initTypeAddsUpdatesPerDay(final HttpServletRequest request, final HttpServletResponse response) {
		// get data
		switch (this.type) {
			default:
			case TYPE_ADDSPERDAY:
				this.valuesAddsUpdatesPerDay = this.mainController.getDataHandler().getCreatedCountByDay();
				break;
			case TYPE_UPDATESPERDAY:
				this.valuesAddsUpdatesPerDay = this.mainController.getDataHandler().getUpdatedCountByDay();
				break;
		}

		this.chartProperties = new ChartProperties();
		this.axisProperties = new AxisProperties(true);

		// set scale fonts
		final ChartFont axisScaleFont = new ChartFont(new Font("Verdana", Font.PLAIN, 10), Color.black);
		this.axisProperties.getXAxisProperties().setScaleChartFont(axisScaleFont);
		this.axisProperties.getYAxisProperties().setScaleChartFont(axisScaleFont);

		// set title fonts
		final ChartFont axisTitleFont = new ChartFont(new Font("Verdana", Font.PLAIN, 12), Color.black);
		this.axisProperties.getXAxisProperties().setTitleChartFont(axisTitleFont);
		this.axisProperties.getYAxisProperties().setTitleChartFont(axisTitleFont);

		//

		final LabelAxisProperties dataYLabeProperties = (LabelAxisProperties) this.axisProperties.getYAxisProperties();
		dataYLabeProperties.setShowEndBorder(false);

		final DataAxisProperties dataXAxisProperties = (DataAxisProperties) this.axisProperties.getXAxisProperties();
		dataXAxisProperties.setShowTicks(AxisTypeProperties.TICKS_ALL);
		dataXAxisProperties.setUseCommas(false);
		int maxValue = 0;
		for (final Integer value : this.valuesAddsUpdatesPerDay.values()) {
			if (value > maxValue) {
				maxValue = value;
			}
		}

		try {
			dataXAxisProperties.setUserDefinedScale(0, 10);
		} catch (final PropertyException propertyException) {
			propertyException.printStackTrace();
		}

		final ChartStroke xAxisGridLines = new ChartStroke(new BasicStroke(1.0f), Color.LIGHT_GRAY);
		this.axisProperties.getXAxisProperties().setGridLineChartStroke(xAxisGridLines);
		this.axisProperties.getXAxisProperties().setShowGridLines(AxisTypeProperties.GRID_LINES_ONLY_WITH_LABELS);

		final ChartFont titleFont = new ChartFont(new Font("Verdana", Font.PLAIN, 14), Color.black);
		this.chartProperties.setTitleFont(titleFont);

		this.barChartProperties = new BarChartProperties();
		this.barChartProperties.setWidthPercentage(.75f);
		this.barChartProperties.setShowOutlinesFlag(false);

		final ValueLabelRenderer valueLabelRenderer = new ValueLabelRenderer(false, false, true, -1);
		valueLabelRenderer.setValueLabelPosition(ValueLabelPosition.ON_TOP);
		valueLabelRenderer.useVerticalLabels(false);
		this.barChartProperties.addPostRenderEventListener(valueLabelRenderer);
	}
}
