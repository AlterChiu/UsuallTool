package Drawing.JFreeChart;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import com.sun.prism.paint.Color;

import usualTool.AtCommonMath;

public class DataSetSetting {
	private String seriesName = "";
	private Color seriesLineFill = Color.BLACK;
	private Color seriesOutLinePaint = Color.BLACK;
	private Color seriesShapeFille = Color.BLACK;
	private Shape seriesShape = new Ellipse2D.Double(-4, -4, 8, 8);
	private Stroke sereiesStroke = new BasicStroke(5);

	private List<Double> xList = new ArrayList<Double>();
	private List<Double> yList = new ArrayList<Double>();
	private List<String> labelList = new ArrayList<String>();

	// <============================================>
	// <set the value>
	// <============================================>
	public void add(double x, double y) {
		this.xList.add(x);
		this.yList.add(y);
		this.labelList.add("");
	}

	public DataSetSetting(List<Double> xList, List<Double> yList) {
		this.xList = xList;
		this.yList = yList;
		this.xList.forEach(e -> this.labelList.add(""));
	}

	public void add(double x, double y, String label) {
		this.xList.add(x);
		this.yList.add(y);
		this.labelList.add(label);
	}

	public DataSetSetting(List<Double> xList, List<Double> yList, List<String> labelList) {
		this.xList = xList;
		this.yList = yList;
		this.labelList = labelList;
	}
	// <=====================================================>

	// <=============================>
	// <series setting>
	// <=============================>
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public void setSeriesLineFill(Color seriesLineFill) {
		this.seriesLineFill = seriesLineFill;
	}

	public void setSeriesOutLinePaint(Color seriesOutLinePaint) {
		this.seriesOutLinePaint = seriesOutLinePaint;
	}

	public void setSeriesShapeFille(Color seriesShapeFille) {
		this.seriesShapeFille = seriesShapeFille;
	}

	public void setSeriesShape(Shape seriesShape) {
		this.seriesShape = seriesShape;
	}

	public void setSereiesStroke(Stroke sereiesStroke) {
		this.sereiesStroke = sereiesStroke;
	}
	// <================================>

	public String getSeriesName() {
		return this.seriesName;
	}

	public Color getSeriesLineFill() {
		return this.seriesLineFill;
	}

	public Color getSeriesOutLinePaint() {
		return this.seriesOutLinePaint;
	}

	public Color getSeriesShapeFille() {
		return this.seriesShapeFille;
	}

	public Shape getSeriesShape() {
		return this.seriesShape;
	}

	public Stroke getSereiesStroke() {
		return this.sereiesStroke;
	}

	public List<Double> getXList() {
		return this.xList;
	}

	public List<Double> getYList() {
		return this.yList;
	}

	public List<String> getLabelList() {
		return this.labelList;
	}

	public double getXmax() {
		return new AtCommonMath(this.xList).getMax();
	}

	public double getXmin() {
		return new AtCommonMath(this.xList).getMin();
	}

	public double getYmax() {
		return new AtCommonMath(this.yList).getMax();
	}

	public double getYmin() {
		return new AtCommonMath(this.yList).getMin();
	}
}
