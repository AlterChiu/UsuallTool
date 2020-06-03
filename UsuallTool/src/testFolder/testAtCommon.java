package testFolder;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.dom4j.Element;
import org.gdal.ogr.Geometry;

import FEWS.Rinfall.BUI.BuiTranslate;
import FEWS.SOBEK.FileReader.SACRMNTO_3B;
import FEWS.SOBEK.FileReader.SOBEKREADER;
import asciiFunction.AsciiBasicControl;
import geo.common.CoordinateTranslate;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtXmlReader;
import usualTool.DateValueStorage;
import usualTool.FileFunction;
import usualTool.TimeTranslate;
import usualTool.AtCommonMath.StaticsModel;

public class testAtCommon {
	public static Map<String, String> commandMap;

	public static final String sobekDir = "-sobekDir";
	public static final String sobekConfigFile = "-sobekConfigFile";
	public static final String sobekRunBat = "temptRun.bat";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

//		String workSpace = "D:\\FEWS_Tainwan_SA_20200327\\Fews2018.02\\Taiwan\\Export\\宜蘭\\";
//		for (String fileName : new File(workSpace).list()) {
//			if (fileName.contains(".xml")) {
//				new AtFileWriter(new BuiTranslate(workSpace + fileName).getBuiRainfall(),
//						workSpace + fileName.replace(".xml", ".BUI")).textWriter("");
//			}
//		}
//		String workSpace = "C:\\Users\\alter\\Downloads\\2.DEM成果-20200522T035643Z-001\\2.DEM成果\\";
//		System.out.println("minx , maxX , minY , maxY");
//		for (String fileName : new File(workSpace).list()) {
//			List<Double> xList = new ArrayList<>();
//			List<Double> yList = new ArrayList<>();
//			if (fileName.contains(".xyz")) {
//				String[][] content = new AtFileReader(workSpace + fileName).getContent(" +");
//
//				for (String temptLine[] : content) {
//					xList.add(Double.parseDouble(temptLine[0]));
//					yList.add(Double.parseDouble(temptLine[1]));
//				}
//
//				System.out.print(AtCommonMath.getListStatistic(xList, StaticsModel.getMin) + ",");
//				System.out.print(AtCommonMath.getListStatistic(xList, StaticsModel.getMax) + ",");
//				System.out.print(AtCommonMath.getListStatistic(yList, StaticsModel.getMin) + ",");
//				System.out.print(AtCommonMath.getListStatistic(yList, StaticsModel.getMax));
//				System.out.println();
//			}
//		}

		double coordinates[][] = new double[][] { { 166285.0, 168885.0, 2552615.0, 2555415.0 },
				{ 161145.0, 163745.0, 2549875.0, 2552675.0 }, { 163710.0, 166305.0, 2549860.0, 2552660.0 },
				{ 168835.0, 171430.0, 2549830.0, 2552635.0 }, { 171395.0, 173990.0, 2549820.0, 2552620.0 },
				{ 158570.0, 161165.0, 2547120.0, 2549925.0 }, { 168820.0, 171415.0, 2547065.0, 2549865.0 },
				{ 158550.0, 161150.0, 2544661.0, 2547155.0 }, { 161115.0, 163715.0, 2544335.0, 2547140.0 },
				{ 163675.0, 166275.0, 2544320.0, 2547125.0 } };

		for (double[] coordinate : coordinates) {
			System.out.println(coordinate[0] + " " + coordinate[2]);
			System.out.println(coordinate[0] + " " + coordinate[3]);
			System.out.println(coordinate[1] + " " + coordinate[2]);
			System.out.println(coordinate[1] + " " + coordinate[3]);
		}
	}
}
