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
import usualTool.TimeTranslate;

public class testAtCommon {

	private static TreeMap<String, String> timesTree = new TreeMap<String, String>();

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stu
		String chinese[] = new String[] {""};
		String content[] = new AtFileReader("C:\\Sobek213\\Active12.lit\\5\\NETWORK.NTW",AtFileReader.ANSI).getContain();
		String fileAdd = "S:\\Users\\alter\\Desktop\\新增資料夾\\";
		
		new AtFileWriter(content , fileAdd + "ANSI.txt").setEncoding(AtFileWriter.ANSI).textWriter("");
	
		
	}
}