package usualTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class AtExcelReader {
	ArrayList<String[]> outPut = new ArrayList<String[]>();

	public AtExcelReader(String fileAdd ,int sheerOrder) throws EncryptedDocumentException, InvalidFormatException, IOException {

		FileInputStream excelFile = new FileInputStream(new File(fileAdd));

		Workbook book = WorkbookFactory.create(excelFile);
		FormulaEvaluator formulaEval = book.getCreationHelper().createFormulaEvaluator();
		Iterator<Row> rowValue = book.getSheetAt(sheerOrder).iterator();

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
				} else if(value.getCellTypeEnum() == CellType.FORMULA){
					temptList.add(formulaEval.evaluate(value).formatAsString());
				}else{
					temptList.add("");
				}
			}
			outPut.add(temptList.parallelStream().toArray(String[]::new));
		}
	}
	
	public String[][] getContent(){
		return this.outPut.parallelStream().toArray(String[][]::new);
	}
	
	public ArrayList<String[]> getList(){
		return this.outPut;
	}
	
	public void toCsv(String location) throws IOException{
		new AtFileWriter(this.outPut.parallelStream().toArray(String[][]::new) , location).csvWriter();
	}
	
	

}
