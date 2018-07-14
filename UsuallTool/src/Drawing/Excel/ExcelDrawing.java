package Drawing.Excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;

public class ExcelDrawing {
	private Workbook workBook;
	private Sheet currentSheet;

	public ExcelDrawing() {
		this.workBook = new XSSFWorkbook();
		this.workBook.createSheet("alterTempNew");
		selectSheet("alterTempNew");
	}

	// <==================================>
	// < Set the value in current Cell >
	// <==================================>
	public void setValue(int row, int column, String value) {
		SetCellValue.setSheet(this.currentSheet);
		SetCellValue.setValue(row, column, value);
	}

	public void setValue(int row, int column, double value) {
		SetCellValue.setSheet(this.currentSheet);
		SetCellValue.setValue(row, column, value);
	}

	public void setValue(int row, int column, Date value) {
		SetCellValue.setSheet(this.currentSheet);
		SetCellValue.setValue(row, column, value);
	}
	// <===================================>

	// <====================================>
	// < Sheet Function >
	// <====================================>

	// create a new sheet and select it
	// <====================================>
	public void newSheet(String sheet) {
		try {
			this.workBook.removeSheetAt(this.workBook.getSheetIndex("alterTempNew"));
		} catch (Exception e) {
		}
		this.workBook.createSheet(sheet);
		selectSheet(sheet);
	}

	// select sheet
	// <======================================>
	public void selectSheet(String sheet) {
		try {
			this.currentSheet = this.workBook.getSheet(sheet);
		} catch (Exception e) {
			newSheet(sheet);
			this.currentSheet = this.workBook.getSheet(sheet);
		}

	}
	// <======================================>

	public void Output(String path) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(path);
        this.workBook.write(fileOut);
        fileOut.close();
	}

	public void chartCreater(ChartImplemetns chartProperty) {
		// setting the position of the chart
		Drawing drawing = this.currentSheet.createDrawingPatriarch();
		ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, chartProperty.getStartColumn(),
				chartProperty.getStartRow(), chartProperty.getStartColumn() + chartProperty.getWidth(),
				chartProperty.getStartRow() + chartProperty.getHeight());

		// setting the basic property of chart
		Chart chart = drawing.createChart(anchor);
		chart.getOrCreateLegend().setPosition(chartProperty.getLegendPosition());

		ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(chartProperty.getXBarPosition());
		ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(chartProperty.getYBarPosition());
		leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

		LineChartData dataSeires = chart.getChartDataFactory().createLineChartData();

		// Add the value list to the collections ===> x
		int[] xRange = chartProperty.getXBarValue();
		ChartDataSource<Number> seriesXRange = DataSources.fromNumericCellRange(this.currentSheet,
				new CellRangeAddress(xRange[0], xRange[1], xRange[2], xRange[3]));

		// Add the value list to the collections ===> y
		List<ChartDataSource<Number>> seriesValueList = new ArrayList<ChartDataSource<Number>>();
		for (int index = 0; index < chartProperty.getYBarValue().size(); index++) {
			Integer[] seriesRange = chartProperty.getYBarValue().get(index);
			seriesValueList.add(DataSources.fromNumericCellRange(this.currentSheet,
					new CellRangeAddress(seriesRange[0], seriesRange[1], seriesRange[2], seriesRange[3])));
		}

		// put the value to the chart
		for (int index = 0; index < seriesValueList.size(); index++) {
			dataSeires.addSeries(seriesXRange, seriesValueList.get(index))
					.setTitle(chartProperty.getSeriesName().get(index));
		}

		// if want to smooth
		if (chartProperty.getSmooth()) {
			XSSFChart xssfChart = (XSSFChart) chart;
			CTPlotArea plotArea = xssfChart.getCTChart().getPlotArea();
			plotArea.getLineChartArray()[0].getSmooth();
			CTBoolean ctBool = CTBoolean.Factory.newInstance();
			ctBool.setVal(false);
			plotArea.getLineChartArray()[0].setSmooth(ctBool);
			for (CTLineSer ser : plotArea.getLineChartArray()[0].getSerArray()) {
				ser.setSmooth(ctBool);
			}
		}

	}

}
