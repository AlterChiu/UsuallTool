package testFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Drawing.Excel.ChartImplemetns;
import Drawing.Excel.ExcelBasicControl;
import usualTool.AtExcelReader;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class testAtCommon {

	public static void main(String[] args)
			throws IOException, OperationNotSupportedException, EncryptedDocumentException, InvalidFormatException {
		// TODO Auto-generated method stub
		String fileAdd = "S:\\Users\\alter\\Desktop\\冠智\\outExcel.xlsx";

		ExcelBasicControl excel = new ExcelBasicControl(fileAdd);
		Workbook workBook  = excel.getWorkBook();
		
		for (String sheet : excel.getSheetList()) {
			excel.selectSheet(sheet);
			String[][] content = new AtExcelReader(workBook).getContent(sheet);

			for (int location = 1; location < content.length; location++) {
				ChartImplemetns chartTimes = new ChartImplemetns();
				chartTimes.setChartSize(8, 8);
				chartTimes.setStartPoint((location-1)*8, 10);
				chartTimes.setXBarValue(0, 1, 0, 8);
				chartTimes.setYValueList(location, 1, location, 8, content[location][0]);
				chartTimes.setSelectedSheet(sheet);
				excel.chartCreater(chartTimes);
				
				ChartImplemetns chartSum = new ChartImplemetns();
				chartSum.setChartSize(8, 8);
				chartSum.setStartPoint((location-1)*8, 19);
				chartSum.setXBarValue(0, 1, 0, 8);
				chartSum.setYValueList(location, 10, location, 17, content[location][0]);
				chartSum.setSelectedSheet(sheet);
				excel.chartCreater(chartSum);
			}
		}
		excel.Output("S:\\Users\\alter\\Desktop\\冠智\\outExcel_T.xlsx");
	}
}