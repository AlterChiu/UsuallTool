package testFolder;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.google.gson.JsonObject;

import Drawing.Excel.ChartImplemetns;
import Drawing.JFreeChart.ChartImplement;
import FEWS.Rinfall.BUI.BuiTranslate;
import Netcdf.NetCDFReader;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiToPath;
import asciiFunction.XYZToAscii;
import geo.gdal.DBFToGeoJson;
import geo.path.IntersectLine;
import geo.path.PathToGeoJson;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;
import usualTool.AtDeterminant;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.ExcelBasicControl;
import usualTool.FileFunction;
import usualTool.RandomMaker;
import usualTool.TimeTranslate;

public class testAtCommon {

	public static void main(String[] args) throws IOException, InvalidRangeException, OperationNotSupportedException,
			ParseException, EncryptedDocumentException, InvalidFormatException {
		// TODO Auto-generated method
		String fileAdd = "E:\\HomeWork\\很瑣碎\\EMIC_2018\\1DStation\\";

		ExcelBasicControl excel = new ExcelBasicControl(fileAdd + "StaitonList.xlsx");
		excel.selectSheet("Sheet2");
		String[][] content = excel.getSheetContent();

		for (int column = 5; column < content[0].length; column++) {

			List<String[]> zoneList = new ArrayList<String[]>();
			zoneList.add(new String[] { "stationID", "modelID" });

			for (int row = 1; row < content.length; row++) {
				System.out.println(row + "\t" + column);
				try {
					if (!content[row][column].equals("")) {
						zoneList.add(new String[] { content[row][1], content[row][column] });
					}
				} catch (Exception e) {
				}
			}

			new AtFileWriter(zoneList.parallelStream().toArray(String[][]::new), fileAdd + content[0][column] + ".csv")
					.csvWriter();
		}

	}
}