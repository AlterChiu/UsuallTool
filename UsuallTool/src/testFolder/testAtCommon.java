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

import com.google.gson.JsonObject;

import FEWS.Rinfall.BUI.BuiTranslate;
import Netcdf.NetCDFReader;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiToPath;
import asciiFunction.XYZToAscii;
import geo.gdal.DBFToGeoJson;
import geo.path.PathToGeoJson;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;
import usualTool.AtDeterminant;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.FileFunction;
import usualTool.RandomMaker;
import usualTool.TimeTranslate;

public class testAtCommon {

	public static void main(String[] args)
			throws IOException, InvalidRangeException, OperationNotSupportedException, ParseException {
		// TODO Auto-generated method
		Path2D path = new AsciiToPath(
				"E:\\HomeWork\\mapReduce\\OriginalDEM\\PintongZone2\\97(KP_DG_unit2)(80mDEM)_clip(unit2-1).asc").getAsciiPath();

		PathToGeoJson toGeoJson = new PathToGeoJson(path);
		toGeoJson.saveGeoJson("S:\\Users\\alter\\Desktop\\garbage\\test841.geoJson");

		// String fileAdd = "E:\\HomeWork\\mapReduce\\RainfallData\\200y_12_3_TN.BUI";
		// String content[] = new AtFileReader(fileAdd).getContain();
		//
		//// for (int column = 0; column < content[7722].length; column++) {
		//// double start = Double.parseDouble(content[7722][column]);
		//// double high = Double.parseDouble(content[7728][column]);
		//// double end = Double.parseDouble(content[7733][column]);
		////
		//// for (int index = 0; index < 3; index++) {
		//// content[7722 + index][column] = new BigDecimal(start + (high - start) / 2 *
		// index)
		//// .setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		//// }
		//// for (int index = 2; index < 12; index++) {
		//// content[7722 + index][column] = new BigDecimal(high - (high - end) / 9 *
		// (index - 2))
		//// .setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		//// }
		//// }
		// for (int index = 7724; index < content.length; index++) {
		// content[index] = content[7724];
		// }
		//
		// new AtFileWriter(content,
		// "E:\\HomeWork\\mapReduce\\RainfallData\\200yMax_12_3_TN.BUI").textWriter("");

	}
}