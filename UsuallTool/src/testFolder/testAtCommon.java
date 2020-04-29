package testFolder;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

//		String fileAdd = "D:\\FEWS_Tainwan_SA_20200327\\Fews2018.02\\Taiwan\\modules\\WRA\\Taiwan\\Central\\Yunlin\\Sobek\\Zone\\Input\\Rainfall.xml";
//		new AtFileWriter(new BuiTranslate(fileAdd).getBuiRainfall(), "D:\\Sobek213\\Fixed\\YUL40MA.BUI").textWriter("");

		String targetAdd = "D:\\Sobek213\\YUL40ZTA.lit\\17\\BOUNDARY - Original.DAT";
		String saveAdd = "D:\\Sobek213\\YUL40ZTA.lit\\17\\BOUNDARY.DAT";
		Map<String, String> idMapping = getIdMapping();

		List<String> outList = new ArrayList<>();

		for (String key : idMapping.keySet()) {
			String id = key;

			if (idMapping.containsKey(id)) {
				String tideName = idMapping.get(id);
				List<String> tideValues = getTideString(tideName);
				getBoundaryString(id, tideValues).forEach(e -> outList.add(e));
			}
		}

		new AtFileWriter(outList.parallelStream().toArray(String[]::new), saveAdd).textWriter("");

//		String saveAdd = "C:\\Users\\alter\\Desktop\\tempt.csv";
//		List<String> outList = new ArrayList<>();
//
//		for (int time = 1; time <= 6; time++) {
//
//			List<String> temptList = new ArrayList<>();
//			for (int index = 0; index < 3369; index++) {
//				temptList.add(Math.abs(3-time) + "");
//			}
//			outList.add(String.join(" ", temptList));
//		}
//
//		new AtFileWriter(outList.parallelStream().toArray(String[]::new), saveAdd).textWriter("");
	}

	public static Map<String, String> getIdMapping() throws IOException {
		Map<String, String> outMap = new TreeMap<>();
		String[][] content = new AtFileReader("E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\淹水數據測試\\雲林潮位抽換\\潮位對應.csv")
				.getCsv();

		for (String[] temptLine : content) {
			outMap.put(temptLine[0], temptLine[1]);
		}

		return outMap;
	}

	public static List<String> getTideString(String tideName) throws IOException, ParseException {
		String fileAdd = "E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\淹水數據測試\\雲林潮位抽換\\" + tideName + ".csv";
		String inputFormat = "yyyy-MM-dd HH:mm";
		String outputFormat = "yyyy/MM/dd;HH:mm:ss";

		String[][] content = new AtFileReader(fileAdd).getCsv();
		List<String> outList = new ArrayList<>();
		for (String temptLine[] : content) {
			StringBuilder sb = new StringBuilder();
			sb.append("'");
			sb.append(TimeTranslate.getDateStringTranslte(temptLine[0], inputFormat, outputFormat));
			sb.append("' ");
			sb.append(temptLine[1]);
			sb.append(" <");

			outList.add(sb.toString());
		}
		return outList;
	}

	public static List<String> getBoundaryString(String id, List<String> tideValues) {
		List<String> outList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("FLBO id '");
		sb.append(id);
		sb.append("' st 0 ty 0 h_ wt 1 0 0 PDIN 0 0 '' pdin");
		outList.add(sb.toString());

		outList.add("TBLE");
		tideValues.forEach(e -> outList.add(e));
		outList.add("tble flobo");

		return outList;
	}

}
