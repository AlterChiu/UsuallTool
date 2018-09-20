package testFolder;

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

import FEWS.Rinfall.BUI.BuiTranslate;
import Netcdf.NetCDFReader;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.XYZToAscii;
import gdal.DBFToGeoJson;
import ucar.ma2.InvalidRangeException;
import usualTool.AtDeterminant;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.FileFunction;
import usualTool.TimeTranslate;

public class testAtCommon {

	public static void main(String[] args)
			throws IOException, InvalidRangeException, OperationNotSupportedException, ParseException {
		// TODO Auto-generated method
		// TimeTranslate tt = new TimeTranslate();
		// FileFunction ff = new FileFunction();
		//
		// String fileAdd = "E:\\HomeWork\\EMIC_2018\\Kaohsiung\\";
		//// String zoneFolder[] = new String[] { "Z1U1", "Z1U2", "Z2", "Z3", "Z4",
		// "Z5", "Z6", "Z7" };
		// String zoneFolder[] = new String[] { "Z1", "Z2", "Z3", "Z4" };
		//
		// for (String event : new File(fileAdd + "Z4\\merge\\").list()) {
		// String[][] temptAscii = new AsciiBasicControl(fileAdd + "Z4\\merge\\" +
		// event).getAsciiFile();
		//
		// for (int index = 0; index < zoneFolder.length - 1; index++) {
		// temptAscii = new AsciiMerge(fileAdd + "Z4\\merge\\" + event,
		// fileAdd + zoneFolder[index] + "\\merge\\" + event).getMergedAscii();
		// }
		//
		// new AtFileWriter(temptAscii, fileAdd + "merge\\" + event).textWriter(" ");
		// }
		//
		// // String temptFolder = fileAdd + "Z3\\";
		// //
		// // for (String event : new File(temptFolder).list()) {
		// // try {
		// // Double.parseDouble(event);
		// // String eventFolder = temptFolder + event + "\\";
		// //
		// // for (int index = 0; index <= 72; index++) {
		// // String filePath = eventFolder + "dm1d" + String.format("%04d", index) +
		// // ".asc";
		// // ff.copyFile(filePath, temptFolder + "merge\\"
		// // + tt.milliToDate(tt.StringToLong(event, "yyyyMMddHH") + 3600000 * index,
		// // "yyyyMMddHH")
		// // + ".asc");
		// // }
		// //
		// // } catch (Exception e) {
		// // e.printStackTrace();
		// // }
		// // }

		String fileAdd = "E:\\HomeWork\\mapReduce\\RainfallData\\200y_12_6_TN_PD.BUI";
		String content[][] = new AtFileReader(fileAdd).getStr();

		for (int column = 0; column < content[2910].length; column++) {
			double start = Double.parseDouble(content[2910][column]);
			double high = Double.parseDouble(content[2915][column]);
			double end = Double.parseDouble(content[2921][column]);

			int eventStart = 2910;
			double levelUp = (high - start) / 2;
			double levelDown = (end - high) / 9;
			for (int line = 0; line < 3; line++) {
				content[eventStart + line][column] = new BigDecimal(start + levelUp * line)
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			}
			for (int line = 3; line < 12; line++) {
				content[eventStart + line][column] = new BigDecimal(high + levelDown * (line - 2))
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			}
		}
		
		new AtFileWriter(content ,  "E:\\HomeWork\\mapReduce\\RainfallData\\200y_12_3_TN_PD.BUI").textWriter(" ");

	}
}