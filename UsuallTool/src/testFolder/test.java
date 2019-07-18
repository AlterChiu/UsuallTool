package testFolder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import geo.gdal.CsvToSpatialFile;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialFileTranslater;
import geo.gdal.Interpolation.AtInterpolation;
import geo.gdal.Interpolation.InterPolationKriging;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.TimeTranslate;

public class test {
	/*
	 * krigingFunction parameter
	 */
	// model
	public static String VAR_MODEL_Mult1 = "a + b * x";
	public static String VAR_MODEL_Mult2 = "a + b * x + c * x^2";
	public static String VAR_MODEL_Mult3 = "a + b * x + c * x^2 + d * x^3";
	public static String VAR_MODEL_Mult4 = "a + b * x + c * x^2 + d * x^3 + e * x^4";
	public static String VAR_MODEL_SQRT = "a + b * sqrt(c + x)";
	public static String VAR_MODEL_LOG = "a + b * ln(x)";
	public static String VAR_MODEL_EXP = "a + b * x^c";
	public static String VAR_MODEL_GUSS = "a + b * (1 - exp(-(x/b)^2))";
	public static String VAR_MODEL_CIRCULAR = "a + b * ifelse(x >c , 1, 1.5*x/c - 0.5*x^3/c^3)";
	private static String VAR_MODEL_SELECTED = VAR_MODEL_Mult1;

	// definition
	public static int DEFINITION_STD = 0;
	public static int DEFINITION_VAR = 1;
	private static int DEFINITION_SELECTED = DEFINITION_STD;

	// target boundary
	private static Map<String, Double> targetBoundary = new TreeMap<>();

	// search point
	private static int SEARCH_POINTS_MIN = 4;
	private static int SEARCH_POINTS_MAX = 20;

	public static int SEARCH_POINTS_RADIUS = 0;
	public static int SEARCH_POINTS_ALL = 1;
	private static int SEARCH_POINTS_SELECTED = SEARCH_POINTS_RADIUS;

	// search radius
	private static double SEARCH_RADIUS = 9999.;

	// fitting
	public static int FITTING_NODES = 0;
	public static int FITTING_CELLS = 1;
	private static int FITTING_SELECTED = FITTING_NODES;

	// cellSize
	private static double cellSize = 100.;

	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
		// TODO Auto-generated method stud

//		String timeStart = "2019/03/04 20:20";
//		String timeEnd = "2019/06/24 23:50";
//		String temptTime = timeStart;
//
//		Map<String, Double[]> outList = new TreeMap<>();
//		String[][] content = new AtFileReader("C:\\Users\\alter\\Desktop\\xdd.txt").getCsv();
//
//		for (String[] temptLine : content) {
//			outList.put(TimeTranslate.StringGetSelected(temptLine[0], "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm"),
//					new Double[] { Double.parseDouble(temptLine[1]), Double.parseDouble(temptLine[4]),
//							Double.parseDouble(temptLine[5]) });
//		}
//
//		while (!temptTime.equals(timeEnd)) {
//			if (!outList.containsKey(timeStart)) {
//				outList.put(TimeTranslate.StringGetSelected(temptTime, "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm"),
//						new Double[] {});
//			}
//			temptTime = TimeTranslate.addMinute(temptTime, "yyyy/MM/dd HH:mm", 10);
//		}
//
//		Double[] lastValue = new Double[] {};
//		List<String> keyList = new ArrayList<>(outList.keySet());
//		for (int index = 0; index < keyList.size(); index++) {
//
//			if (outList.get(keyList.get(index)).length < 1) {
//
//				int skip = 0;
//				Double[] nextValue = new Double[] {};
//
//				// search
//				for (int search = index + 1; search < keyList.size(); search++) {
//					if (outList.get(keyList.get(search)).length > 1) {
//						skip = search - index + 1;
//						nextValue = outList.get(keyList.get(search));
//					}
//				}
//
//				// input
//				Double[] spaceValue = new Double[] { (nextValue[0] - lastValue[0]) / skip,
//						(nextValue[1] - lastValue[1]) / skip, (nextValue[2] - lastValue[2]) / skip };
//				for (int search = 1; search < skip; search++) {
//					outList.put(keyList.get(index + search), new Double[] { lastValue[0] + spaceValue[0] * search,
//							lastValue[1] + spaceValue[1] * search, lastValue[2] + spaceValue[2] * search });
//				}
//
//				index = index + skip;
//			}
//			lastValue = outList.get(keyList.get(index));
//		}
//
//		List<String[]> outPut = new ArrayList<>();
//		for (String key : outList.keySet()) {
//			Double[] temptValue = outList.get(key);
//			outPut.add(new String[] { key, temptValue[0] + "", temptValue[1] + "",temptValue[2] + ""  });
//		}
//		new AtFileWriter(outPut.parallelStream().toArray(String[][]::new), "C:\\Users\\alter\\Desktop\\xdd.csv")
//				.csvWriter();

		String fileAdd = "E:/LittleProject/ReurnPeriodComparision/test/StaionComparision_test.csv";
		String content[][] = new AtFileReader(fileAdd).getCsv(1, 0);
//
		List<Double[]> xyzList = new ArrayList<>();
		for (String temptLine[] : content) {
			xyzList.add(new Double[] { Double.parseDouble(temptLine[2]), Double.parseDouble(temptLine[3]),
					Double.parseDouble(temptLine[4]) });
		}
//
		InterPolationKriging interpolation = new InterPolationKriging(xyzList);
		interpolation.setCellSize(0.0125);
		new AtFileWriter(interpolation.getAscii(xyzList).getAsciiFile(),
				"E:/LittleProject/ReurnPeriodComparision/test/StaionComparision_test.asc").textWriter(" ");

//		List<String[]> temptCsvList = new ArrayList<>();
//		temptCsvList.add(new String[] { "x", "y", "z" });
//		xyzList.forEach(e -> {
//			temptCsvList.add(new String[] { e[0] + "", e[1] + "", e[2] + "" });
//		});
//
//		CsvToSpatialFile csvConverter = new CsvToSpatialFile(temptCsvList, true, 0, 1);
//		Map<String, String> csvTitleType = new TreeMap<>();
//		csvTitleType.put("x", "Double");
//		csvTitleType.put("y", "Double");
//		csvTitleType.put("z", "Double");
//		csvConverter.setFieldType(csvTitleType);
//		csvConverter.saveAsShp(GdalGlobal.temptFile + ".shp");

		/*
		 * SAGA CommandLine
		 */
//		List<String> sagaCmd = new ArrayList<>();
//
//		// command line started
//		sagaCmd.add("cmd");
//		sagaCmd.add("/c");
//		sagaCmd.add("start");
//		sagaCmd.add("/wait");
//		
//		sagaCmd.add("saga_cmd.exe");
//		sagaCmd.add("statistics_kriging");
//		sagaCmd.add("\"Ordinary Kriging\"");
//		sagaCmd.add("-TARGET_DEFINITION");
//		sagaCmd.add("0");
//		sagaCmd.add("-POINTS");
//		sagaCmd.add("\"F:\\Qgis\\test\\tempt.shp\"");
//		sagaCmd.add("-FIELD");
//		sagaCmd.add("\"z\"");
//		sagaCmd.add("-VAR_MODEL");
//		sagaCmd.add("\"a + b * x\"");
//		sagaCmd.add("-TARGET_USER_XMIN");
//		sagaCmd.add("119.49432");
//		sagaCmd.add("-TARGET_USER_XMAX");
//		sagaCmd.add("122.08595");
//		sagaCmd.add("-TARGET_USER_YMIN");
//		sagaCmd.add("21.98575");
//		sagaCmd.add("-TARGET_USER_YMAX");
//		sagaCmd.add("25.63425");
//		sagaCmd.add("-TARGET_USER_SIZE");
//		sagaCmd.add("0.0125");
//		sagaCmd.add("-TARGET_USER_FITS");
//		sagaCmd.add("0");
//		sagaCmd.add("-SEARCH_RADIUS");
//		sagaCmd.add("9999.0");
//		sagaCmd.add("-SEARCH_POINTS_ALL");
//		sagaCmd.add("0");
//		sagaCmd.add("-SEARCH_POINTS_MIN");
//		sagaCmd.add("4");
//		sagaCmd.add("-SEARCH_POINTS_MAX");
//		sagaCmd.add("20");
//		sagaCmd.add("-PREDICTION");
//		sagaCmd.add("\"F:\\Qgis\\test\\tempt_pridiction.sdat\"");
//		sagaCmd.add("-VARIANCE");
//		sagaCmd.add("\"F:\\Qgis\\test\\tempt_variance.sdat\"");
//		
//		ProcessBuilder saga_builder = new ProcessBuilder();
//		saga_builder.directory(new File(GdalGlobal.sagaBinFolder));
//		saga_builder.command(sagaCmd);
//		Process saga_process = saga_builder.start();
//		saga_process.waitFor();

	}

}
