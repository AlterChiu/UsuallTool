package Microsoft.Office.Excel.Chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import Microsoft.Office.Excel.ExcelBasicControl;
import geo.gdal.GdalGlobal;
import geo.grass.GrassGlobal;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class ExcelChart {
	public static String MODULE_COPYPASTE = "copyPaste";

	public static void copyPaste(String excelFilePath) throws IOException, InterruptedException {

		// get excel file properties
		File excelFile = new File(excelFilePath);
		String excelFolder = excelFile.getParent() + "\\";
		String excelName = excelFile.getName();

		// copy initialFile to selected folder
		List<String> copyPasteVBS = createModuleVBS(excelName, MODULE_COPYPASTE);
		new AtFileWriter(copyPasteVBS.parallelStream().toArray(String[]::new), excelFolder + MODULE_COPYPASTE + ".vbs")
				.textWriter("");

		// run vbs on command line
		runVBS(excelFolder + MODULE_COPYPASTE + ".vbs", excelFolder);

		// remove vbs file
		FileFunction.delete(excelFolder + MODULE_COPYPASTE + ".vbs");
	}

	public static void toPNG(String excelFilePath, String targetSheet) throws IOException, InterruptedException {
		outputChart(excelFilePath, targetSheet, "PNG");
	}
	
	public static void toJPEG(String excelFilePath, String targetSheet) throws IOException, InterruptedException {
		outputChart(excelFilePath, targetSheet, "JPEG");
	}
	
	public static void toGIF(String excelFilePath, String targetSheet) throws IOException, InterruptedException {
		outputChart(excelFilePath, targetSheet, "GIF");
	}
	
	private static void outputChart(String excelFilePath, String targetSheet, String outputFormat)
			throws IOException, InterruptedException {

		// get excel file properties
		File excelFile = new File(excelFilePath);
		String excelFolder = excelFile.getParent() + "\\";
		String excelName = excelFile.getName();

		// setting vbs initial
		List<String> vbsContent = new ArrayList<>();
		vbsContent.add("Option Explicit");
		vbsContent.add("runVBA");

		vbsContent.add("Sub runVBA()");
		vbsContent.add("    Dim xl1");
		vbsContent.add("    Dim xlBook");
		vbsContent.add("    Dim FolderFromPath");
		vbsContent.add("    Set xl1 = CreateObject(\"Excel.application\")");

		vbsContent.add("    FolderFromPath = Replace(WScript.ScriptFullName, WScript.ScriptName, \"\")");
		vbsContent.add("    set xlBook = xl1.Workbooks.Open(FolderFromPath & \"" + excelName + "\")");

		// setting vbs chart output command
		vbsContent.add("	Dim targetSheet");
		vbsContent.add("	Dim outputName");
		vbsContent.add("	Dim targetChart");
		vbsContent.add("	Dim chart");

		vbsContent.add("    Set targetSheet = xlBook.Sheets(\"" + targetSheet + "\")");
		vbsContent.add("    For Each chart In targetSheet.ChartObjects()");
		vbsContent.add("        Set targetChart = chart.chart");
		vbsContent.add("        outputName = targetChart.ChartTitle.Text");
		vbsContent.add("        targetChart.Export \"" + excelFolder + "\" & outputName & \"." + outputFormat + "\", \""
				+ outputFormat + "\"");
		vbsContent.add("	Next");

		// close excel file
		vbsContent.add("    xl1.Application.Quit");
		vbsContent.add("End Sub");
		
		// output vbsFile
		new AtFileWriter(vbsContent.parallelStream().toArray(String[]::new), excelFolder + "outputChart.vbs")
				.textWriter("");

		// run vbs on command line
		runVBS(excelFolder + "outputChart.vbs", excelFolder);

		// remove vbs file
		FileFunction.delete(excelFolder + "outputChart.vbs");
	}

	private static List<String> createModuleVBS(String excelFileName, String module) {
		List<String> outContent = new ArrayList<>();
		outContent.add("Option Explicit");
		outContent.add("runVBA");

		outContent.add("Sub runVBA()");
		outContent.add("    Dim xl1");
		outContent.add("    Dim xlBook");
		outContent.add("    Dim FolderFromPath");
		outContent.add("    Set xl1 = CreateObject(\"Excel.application\")");

		outContent.add("    FolderFromPath = Replace(WScript.ScriptFullName, WScript.ScriptName, \"\")");
		outContent.add("    set xlBook = xl1.Workbooks.Open(FolderFromPath & \"" + excelFileName + "\")");
		outContent.add("    xl1.Application.run \"'\" & xlBook.Name & \"'!AlterChiu." + module + "\"");
		outContent.add("    xlBook.Save");
		outContent.add("    xl1.Application.Quit");

		outContent.add("End Sub");

		return outContent;
	}

	private static void runVBS(String vbsFile, String workSpace) throws InterruptedException, IOException {
		List<String> runCommand = new ArrayList<>();

		// start cmd
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/wait");
		runCommand.add("/B");
		runCommand.add(vbsFile);

		// setting run direction
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(workSpace));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();
	}

}
