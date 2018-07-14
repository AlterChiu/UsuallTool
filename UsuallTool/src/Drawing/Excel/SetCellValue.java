package Drawing.Excel;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SetCellValue {
	private static Row row;
	private static Cell cell;
	private static Sheet dataSheet;

	public static void setSheet(Sheet dataSheet) {
		SetCellValue.dataSheet = dataSheet;
	}

	public static void setValue(int row, int column, String value) {
		SetCellValue.row = SetCellValue.dataSheet.createRow(row);
		SetCellValue.cell = SetCellValue.row.createCell((short) column);
		SetCellValue.cell.setCellValue(value);
	}

	public static void setValue(int row, int column, double value) {
		SetCellValue.row = SetCellValue.dataSheet.createRow(row);
		SetCellValue.cell = SetCellValue.row.createCell((short) column);
		SetCellValue.cell.setCellValue(value);
	}

	public static void setValue(int row, int column, Date value) {
		SetCellValue.row = SetCellValue.dataSheet.createRow(row);
		SetCellValue.cell = SetCellValue.row.createCell((short) column);
		SetCellValue.cell.setCellValue(value);
	}
}
