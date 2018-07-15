package Drawing.JFreeChart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.TextAnchor;

public class ChartBasicControl extends ChartImplement {
	private XYLineAndShapeRenderer renderer;
	private ChartImplement chartImplement;
	private LabelXYDataset seriesDatas = new LabelXYDataset();
	private List<DataSetSetting> seriesSettings = new ArrayList<DataSetSetting>();

	public ChartBasicControl(ChartImplement chartImplement) {
		this.chartImplement = chartImplement;

		// global setting
		this.renderer = new XYLineAndShapeRenderer(true, false);
		this.renderer.setBaseItemLabelGenerator(new LabelGenerator());
		this.renderer.setBaseItemLabelPaint(chartImplement.getLabelColor());
		this.renderer
				.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE2, TextAnchor.TOP_LEFT));
		this.renderer.setBaseItemLabelFont(chartImplement.getChartFont());
		this.renderer.setBaseItemLabelsVisible(true);
		this.renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		this.renderer.setUseFillPaint(true);
		this.renderer.setUseOutlinePaint(chartImplement.getOutLinevisible());
	}

	public void addDataSet(DataSetSetting seriesSetting) {
		seriesSettings.add(seriesSetting);
		List<Double> xList = seriesSetting.getXList();
		List<Double> yList = seriesSetting.getYList();
		List<String> labelList = seriesSetting.getLabelList();
		for (int index = 0; index < xList.size(); index++) {
			this.seriesDatas.add(xList.get(index), yList.get(index), labelList.get(index));
		}
		this.seriesDatas.endSeries(seriesDatas.getSeriesCount() - 1);
	}

	private void drawing() {
		JFreeChart chart = ChartFactory.createXYLineChart(chartImplement.getChartTitle(), chartImplement.getXBarTitle(),
				chartImplement.getYBarTitle(), seriesDatas, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = plotSetting(chart.getXYPlot());

		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.setTitle(new TextTitle(chartImplement.getChartTitle(), chartImplement.getChartFont()));

	}

	private XYPlot plotSetting(XYPlot plot) {
		// xAxis setting
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(this.chartImplement.getXBarTitleFont());
		domainAxis.setTickLabelFont(this.chartImplement.getXBarLabelFont());
		domainAxis.setTickLabelPaint(this.chartImplement.getXBarColor());
		domainAxis.setVisible(true);
		// domainAxis.setRange(0);

		// Y Axis
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelFont(this.chartImplement.getYBarTitleFont());
		rangeAxis.setTickLabelFont(this.chartImplement.getYBarLabelFont());
		rangeAxis.setLabelPaint(this.chartImplement.getYBarColor());
		rangeAxis.setRange(-0.5, 0.5);

		// save render
		plot.setRenderer(renderer);
		plot.setBackgroundPaint(this.chartImplement.getBackGroundColor());
		// the scalar line of chart base (row)
		plot.setRangeGridlinesVisible(chartImplement.getXGridLineVisible());
		plot.setRangeGridlinePaint(Color.black);
		// the scalar line of chart base (column)
		plot.setDomainGridlinesVisible(chartImplement.getYGridLineVisible());
		plot.setDomainGridlinePaint(Color.black);

		return plot;
	}

	public XYLineAndShapeRenderer getRenderer() {
		return this.renderer;
	}

}
