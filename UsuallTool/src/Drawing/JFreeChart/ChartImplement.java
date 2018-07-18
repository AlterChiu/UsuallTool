package Drawing.JFreeChart;

import java.awt.Color;
import java.awt.Font;

public class ChartImplement {
	private String yBarTitle = "";
	private String xBarTitle = "";
	private String chartTitle = "";

	private Font xBarTitleFont = new Font("宋體", Font.BOLD, 20);
	private Font xBarLabelFont = new Font("宋體", Font.BOLD, 12);
	private Font yBarTitleFont = new Font("宋體", Font.BOLD, 20);
	private Font yBarLabelFont = new Font("宋體", Font.BOLD, 12);
	private Font chartTitleFont = new Font("Serif", Font.BOLD, 18);

	private double[] xBarRange = new double[] { 0, 1 };
	private double[] yBarRange = new double[] { 0, 1 };
	private Boolean autoXRange = true;
	private Boolean autoYRange = true;

	private Color labelColor = Color.BLACK;
	private Color xBarColor = Color.BLACK;
	private Color yBarColor = Color.BLACK;
	private Color backGroundColor = Color.WHITE;

	private Boolean setXGridLine = false;
	private Boolean setYGridLine = false;
	private Boolean setOutLine = false;

	private int outPutWide = 1080;
	private int outPutHeight = 740;

	// <==================================>
	// < Setting Size >
	// <==================================>
	// <=========================================>
	public void setWide(int wide) {
		this.outPutWide = wide;
	}

	public void setHeight(int height) {
		this.outPutHeight = height;
	}

	// <==================================>
	// < Setting Title >
	// <==================================>
	// <=========================================>
	public void setYBarTitle(String yBarTitle) {
		this.yBarTitle = yBarTitle;
	}

	public void setXBarTitle(String xBarTitle) {
		this.xBarTitle = xBarTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}
	// <==========================================>

	// <==================================>
	// < Setting Font >
	// <==================================>
	// <==========================================>
	public void setChartFont(Font chartFont) {
		this.chartTitleFont = chartFont;
	}

	public void setXBarTitleFont(Font xBarTitleFont) {
		this.xBarTitleFont = xBarTitleFont;
	}

	public void setXBarLabelFont(Font xBarLabelFont) {
		this.xBarLabelFont = xBarLabelFont;
	}

	public void setYBarTitleFont(Font yBarTitleFont) {
		this.yBarTitleFont = yBarTitleFont;
	}

	public void setYBarLabelFont(Font yBarLabelFont) {
		this.yBarLabelFont = yBarLabelFont;
	}
	// <==========================================>

	// <==========================================>
	// < Setting color >
	// <==========================================>
	// <==============================================>
	public void setLabelColor(Color color) {
		this.labelColor = color;
	}

	public void setXBarColor(Color color) {
		this.xBarColor = color;
	}

	public void setYBarColor(Color color) {
		this.yBarColor = color;
	}

	public void setBackGroundColor(Color color) {
		this.backGroundColor = color;
	}

	// <==============================================>
	// <========================>
	// <special setting>
	// <========================>
	// <==============================================>
	public void setXGridVisible(Boolean bool) {
		this.setXGridLine = bool;
	}

	public void setYFridVisible(Boolean bool) {
		this.setYGridLine = bool;
	}

	public void setOutLineUse(Boolean bool) {
		this.setOutLine = bool;
	}

	public void setXRange(double min, double max) {
		this.xBarRange = new double[] { min, max };
	}

	public void setYRange(double min, double max) {
		this.yBarRange = new double[] { min, max };
	}

	public void setAutoXRange(Boolean bool) {
		this.autoXRange = bool;
	}

	public void setAutoYRange(Boolean bool) {
		this.autoYRange = bool;
	}
	// <=============================================>

	// <=========================================>
	// < GET SETTING >
	// <=========================================>

	protected String getYBarTitle() {
		return this.yBarTitle;
	}

	protected String getXBarTitle() {
		return this.xBarTitle;
	}

	protected String getChartTitle() {
		return this.chartTitle;
	}

	protected Font getXBarTitleFont() {
		return this.xBarTitleFont;
	}

	protected Font getXBarLabelFont() {
		return this.xBarLabelFont;
	}

	protected Font getYBarTitleFont() {
		return this.yBarTitleFont;
	}

	protected Font getYBarLabelFont() {
		return this.yBarLabelFont;
	}

	protected Font getChartFont() {
		return this.chartTitleFont;
	}

	protected double[] getXBarRange() {
		return this.xBarRange;
	}

	protected double[] getYBarRange() {
		return this.yBarRange;
	}

	protected Boolean getAutoXRange() {
		return this.autoXRange;
	}

	protected Boolean getAutoYRange() {
		return this.autoYRange;
	}

	protected Boolean getXGridLineVisible() {
		return this.setXGridLine;
	}

	protected Boolean getYGridLineVisible() {
		return this.setYGridLine;
	}

	protected Color getLabelColor() {
		return this.labelColor;
	}

	protected Color getXBarColor() {
		return this.xBarColor;
	}

	protected Color getBackGroundColor() {
		return this.backGroundColor;
	}

	protected Color getYBarColor() {
		return this.yBarColor;
	}

	protected Boolean getOutLinevisible() {
		return this.setOutLine;
	}

	protected int width() {
		return this.outPutWide;
	}

	protected int height() {
		return this.outPutHeight;
	}
}
