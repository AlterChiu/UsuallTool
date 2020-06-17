package testFolder;

import java.io.FileWriter;

import Microsoft.Office.Excel.Chart.CopyPaste.ExcelChart;

public class testAtCommon {
	public static void main(String[] args) throws Exception {

		String fileAdd = "C:\\Users\\alter\\Downloads\\ExcelTemplate000.xlsm";
		ExcelChart.toPNG(fileAdd , "targetSheet");
		
	}
}
