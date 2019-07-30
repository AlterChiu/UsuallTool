package usualTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;

import Drawing.Excel.ChartImplemetns;

public class ExcelBasicControl {
	private Workbook workBook;
	private Sheet currentSheet;
	private Row createRow;
	private Cell createCell;

	public ExcelBasicControl() {
		this.workBook = new XSSFWorkbook();
		this.workBook.createSheet("alterTempNew");
		selectSheet("alterTempNew");
	}

	public ExcelBasicControl(String excelFileAdd)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		this.workBook = WorkbookFactory.create(new File(excelFileAdd));
		this.currentSheet = this.workBook.getSheetAt(0);
	}

	// <==================================>
	// < Set the value in current Cell >
	// <==================================>
	public void setValue(int row, int column, String value) {
		setValueWork(row, column);
		createCell.setCellValue(value);
	}

	public void setValue(int row, int column, double value) {
		setValueWork(row, column);
		createCell.setCellValue(value);
	}

	public void setValue(int row, int column, Date value) {
		setValueWork(row, column);
		createCell.setCellValue(value);
	}

	private void setValueWork(int row, int column) {
		createRow = this.currentSheet.getRow(row);
		if (createRow == null) {
			createRow = this.currentSheet.createRow(row);
		}

		createCell = createRow.getCell(column);
		if (createCell == null) {
			createCell = createRow.createCell(column);
		}
	}

	public Workbook getWorkBook() {
		return this.workBook;
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

	public List<String> getSheetList() {
		List<String> sheetList = new ArrayList<String>();
		for (int index = 0; index < this.workBook.getNumberOfSheets(); index++) {
			sheetList.add(this.workBook.getSheetName(index));
		}
		return sheetList;
	}

	public String[][] getSheetContent() {
		return new AtExcelReader(this.workBook).getContent(this.currentSheet.getSheetName());
	}

	public Sheet getCurrentSheet() {
		return this.currentSheet;
	}

	// <======================================>

	public void Output(String path) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(path);
		this.workBook.write(fileOut);
		fileOut.close();
	}

}
