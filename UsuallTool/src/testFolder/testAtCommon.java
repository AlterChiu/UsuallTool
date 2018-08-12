package testFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Drawing.Excel.ChartImplemetns;
import Drawing.Excel.ExcelBasicControl;
import Drawing.JFreeChart.ChartBasicControl;
import Drawing.JFreeChart.ChartImplement;
import Drawing.JFreeChart.DataSetSetting;
import FEWS.PIXml.AtPiXmlReader;
import FEWS.Rinfall.BUI.BuiTranslate;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiGridChange;
import asciiFunction.AsciiMerge;
import asciiFunction.XYZToAscii;
import gdal.DBFToGeoJson;
import gdal.GeoJsonToShp;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import sun.reflect.generics.tree.Tree;
import usualTool.AtCommonMath;
import usualTool.AtDeterminant;
import usualTool.AtExcelReader;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.FileFunction;
import usualTool.RandomMaker;
import usualTool.TimeTranslate;

public class testAtCommon {

	private static TreeMap<String, String> timesTree = new TreeMap<String, String>();
	private static AtDeterminant deter = new AtDeterminant();
	private static TimeTranslate tt = new TimeTranslate();

	public static void main(String[] args) throws IOException, ParseException, OperationNotSupportedException {
		// TODO Auto-generated method
		AsciiBasicControl delicateAscii = new AsciiBasicControl("S:\\HomeWork\\mapReduce\\OriginalDEM\\ZoneU1_20m.asc");

		String[][] content = delicateAscii.getAsciiGrid();

		for (int row = 0; row < content.length; row++) {
			for (int column = 0; column < content[0].length; column++) {
				System.out.print(content[row][column]);
			}
			System.out.println();
		}

	}

	// <====================================================================>

	// private static void getMaxD0() {
	// String fileAdd =
	// "S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Tainan\\Result\\0618\\";
	// String[] folderList = new File(fileAdd).list();
	//
	// for (String folder : folderList) {
	// List<AsciiBasicControl> asciiList = new ArrayList<AsciiBasicControl>();
	// for (int index = 1; index <= 49; index++) {
	// asciiList.add(
	// new AsciiBasicControl(fileAdd + folder + "\\dm1d" + String.format("%04d",
	// index) + ".asc"));
	// }
	//
	// AsciiBasicControl ascii = new AsciiBasicControl(
	// fileAdd + folder + "\\dm1d" + String.format("%04d", 0) + ".asc");
	// String[][] content = ascii.getAsciiGrid();
	//
	// for (int row = 0; row < content.length; row++) {
	// for (int column = 0; column < content[0].length; column++) {
	//
	// List<Double> valueList = new ArrayList<Double>();
	// for (AsciiBasicControl temptAscii : asciiList) {
	// valueList.add(Double.parseDouble(temptAscii.getValue(column, row)));
	// }
	// ascii.setValue(column, row, new AtCommonMath(valueList).getMax() + "");
	// }
	// }
	//
	// new AtFileWriter(ascii.getAsciiFile(), fileAdd + folder +
	// "\\dm1dmaxd0.asc").textWriter(" ");
	// }
	//
	// }

	// <=========================================================================================>

	// private static void getBoundary() throws IOException {
	// List<String> boundaryValues = new ArrayList<String>(Arrays
	// .asList(new
	// AtFileReader("S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Kao\\Boundary.DAT").getContain()));
	// String moduleFolder =
	// "S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Kao\\module\\";
	//
	// for (int zone = 1; zone <= 4; zone++) {
	// String litFile = moduleFolder + "Zone" + zone + "\\KAO40Z0" + zone +
	// ".lit\\";
	// String caseList[] = new File(litFile).list();
	//
	// for (String caseFile : caseList) {
	// try {
	// Double.parseDouble(caseFile);
	// List<String> caseBoundary = new ArrayList<String>(Arrays
	// .asList(new AtFileReader(litFile + caseFile +
	// "\\BOUNDARY.DAT").getContainWithOut("<")));
	//
	// for (int index = 0; index < caseBoundary.size(); index++) {
	// if (caseBoundary.get(index).contains("TBLE")) {
	// for (int temptIndex = boundaryValues.size() - 1; temptIndex >= 0;
	// temptIndex--) {
	// caseBoundary.add(index + 1, boundaryValues.get(temptIndex));
	// }
	// }
	// }
	// new AtFileWriter(caseBoundary.parallelStream().toArray(String[]::new),
	// "S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Kao\\Boundary\\Z" + zone +
	// "_BOUNDARY.DAT")
	// .textWriter("");
	// ;
	// } catch (Exception e) {
	// }
	// }
	// }
	// }
}