package usualTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class AtExcelReader {
	private List<String[]> outPut = new ArrayList<String[]>();
	private Workbook workBook;

	public AtExcelReader(String fileAdd) throws EncryptedDocumentException, InvalidFormatException, IOException {
		FileInputStream excelFile = new FileInputStream(new File(fileAdd));
		workBook = WorkbookFactory.create(excelFile);

	}

	public AtExcelReader(Workbook workBook) {
		this.workBook = workBook;
	}

	public String[][] getContent(String sheetName) {
		int sheetOrder = this.workBook.getSheetIndex(sheetName);
		return getList(sheetOrder).parallelStream().toArray(String[][]::new);
	}
	
	public String[][] getContent(int sheetOrder) {
		return getList(sheetOrder).parallelStream().toArray(String[][]::new);
	}
	
	public List<String[]> getList(String sheetName) {
		int sheetOrder = this.workBook.getSheetIndex(sheetName);
		return getList(sheetOrder);
	}

	public List<String[]> getList(int sheetOrder) {
		this.outPut.clear();
		FormulaEvaluator formulaEval = workBook.getCreationHelper().createFormulaEvaluator();
		Iterator<Row> rowValue = workBook.getSheetAt(sheetOrder).iterator();

		while (rowValue.hasNext()) {
			ArrayList<String> temptList = new ArrayList<String>();
			Row temptRowValue = rowValue.next();
			Iterator<Cell> cellValue = temptRowValue.iterator();

			while (cellValue.hasNext()) {
				Cell value = cellValue.next();

				if (value.getCellTypeEnum() == CellType.STRING) {
					temptList.add(value.getStringCellValue());
				} else if (value.getCellTypeEnum() == CellType.NUMERIC) {
					temptList.add(value.getNumericCellValue() + "");
				} else if (value.getCellTypeEnum() == CellType.FORMULA) {
					temptList.add(formulaEval.evaluate(value).formatAsString());
				} else {
					temptList.add("");
				}
			}
			outPut.add(temptList.parallelStream().toArray(String[]::new));
		}
		return this.outPut;
	}

}
