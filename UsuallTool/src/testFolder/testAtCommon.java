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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Drawing.Excel.ChartImplemetns;
import Drawing.Excel.ExcelBasicControl;
import Drawing.JFreeChart.ChartBasicControl;
import Drawing.JFreeChart.ChartImplement;
import Drawing.JFreeChart.DataSetSetting;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiBoundary;
import asciiFunction.AsciiGridChange;
import asciiFunction.XYZToAscii;
import gdal.DBFToGeoJson;
import gdal.GeoJsonToShp;
import sun.reflect.generics.tree.Tree;
import usualTool.AtExcelReader;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class testAtCommon {

	private static TreeMap<String, String> timesTree = new TreeMap<String, String>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stu

		// String D2 = "S:\\HomeWork\\mapReduce\\OriginalDEM\\ZoneU1_40m.asc";
		// String D3 = "S:\\HomeWork\\mapReduce\\split\\horizontal\\1\\delicateDem.asc";
		// String D4 = "S:\\HomeWork\\mapReduce\\split\\straight\\0\\delicateDem.asc";
		//
		// AsciiBasicControl D2_Ascii = new AsciiBasicControl(D2);
		// AsciiBasicControl D3_Ascii = new AsciiBasicControl(D3);
		// AsciiBasicControl D4_Ascii = new AsciiBasicControl(D4);
		//
		// Map<String,String> D2_Property = D2_Ascii.getProperty();
		// Map<String,String> D3_Property = D3_Ascii.getProperty();
		// Map<String,String> D4_Property = D4_Ascii.getProperty();
		//
		//
		// System.out.println("D2");
		// System.out.println("bottomX\t" +D2_Property.get("bottomX"));
		// System.out.println("bottomY\t" +D2_Property.get("bottomY"));
		// System.out.println("topX\t" +D2_Property.get("topX"));
		// System.out.println("topY\t" +D2_Property.get("topY"));
		//
		// System.out.println("D3");
		// System.out.println("bottomX\t" +D3_Property.get("bottomX"));
		// System.out.println("bottomY\t" +D3_Property.get("bottomY"));
		// System.out.println("topX\t" +D3_Property.get("topX"));
		// System.out.println("topY\t" +D3_Property.get("topY"));
		//
		// System.out.println("D4");
		// System.out.println("bottomX\t" +D4_Property.get("bottomX"));
		// System.out.println("bottomY\t" +D4_Property.get("bottomY"));
		// System.out.println("topX\t" +D4_Property.get("topX"));
		// System.out.println("topY\t" +D4_Property.get("topY"));
		//
		//
		// AsciiBoundary boundary = new AsciiBoundary(D4);
		// Map<String,String> boundaryMap = boundary.getBoundary(D3_Ascii);
		// int[] topPosition =
		// D3_Ascii.getPosition(Double.parseDouble(boundaryMap.get("maxX")),
		// Double.parseDouble(boundaryMap.get("maxY")));
		// int[] bottomPosition =
		// D3_Ascii.getPosition(Double.parseDouble(boundaryMap.get("minX")),
		// Double.parseDouble(boundaryMap.get("minY")));
		//
		// System.out.println(topPosition[0] + "\t" + topPosition[1]);
		// System.out.println(bottomPosition[0] + "\t" + bottomPosition[1]);
		//
		//
		for (int value = 20; value <= 50; value = value + 10) {
			for (int line = 0; line < 3; line++) {
				for (int index = 0; index < 553; index++) {
					System.out.print(value + " ");
				}
				System.out.println();
			}
		}
	}
}