package geo.gdal.Interpolation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import geo.gdal.CsvToSpatialFile;
import geo.gdal.GdalGlobal;
import usualTool.AtCommonMath;
import usualTool.FileFunction;

public class InterPolationKriging implements AtInterpolation {
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
	private String VAR_MODEL_SELECTED = VAR_MODEL_Mult1;

	// definition
	public static int DEFINITION_STD = 0;
	public static int DEFINITION_VAR = 1;
	private int DEFINITION_SELECTED = DEFINITION_STD;

	// target boundary
	private Map<String, Double> targetBoundary = new TreeMap<>();

	// search point
	private int SEARCH_POINTS_MIN = 4;
	private int SEARCH_POINTS_MAX = 20;

	public static int SEARCH_POINTS_RADIUS = 0;
	public static int SEARCH_POINTS_ALL = 1;
	private int SEARCH_POINTS_SELECTED = SEARCH_POINTS_RADIUS;

	// search radius
	private double SEARCH_RADIUS = 9999;

	// fitting
	public static int FITTING_NODES = 0;
	public static int FITTING_CELLS = 1;
	private int FITTING_SELECTED = FITTING_NODES;

	// cellSize
	private double cellSize = 100.;

	// xyzList
	private List<Double[]> xyzList;

	/*
	 ** InterPolationKriging
	 * 
	 * @ 1. Double list to point shapeFile
	 * 
	 * @ 2. using gisFunction SAGA(in QGIS)
	 */

	public InterPolationKriging(List<Double[]> xyzList) throws InterruptedException {
		this.xyzList = xyzList;
		initialize();
	}

	public InterPolationKriging(List<Double[]> xyzList, String VAR_MODEL_SELECTED) throws InterruptedException {
		this.VAR_MODEL_SELECTED = VAR_MODEL_SELECTED;
		this.xyzList = xyzList;
		initialize();
	}

	private void initialize() throws InterruptedException {
		// clear temptFolder
		for (String fileName : new File(GdalGlobal.temptFolder).list()) {
			FileFunction.delete(GdalGlobal.temptFolder + fileName);
		}
		Thread.sleep(500);

		// convert xyzList to csvFile, which save in temptFile
		List<String[]> temptCsvList = new ArrayList<>();
		temptCsvList.add(new String[] { "x", "y", "z" });
		this.xyzList.forEach(e -> {
			temptCsvList.add(new String[] { e[0] + "", e[1] + "", e[2] + "" });
		});
		CsvToSpatialFile csvConverter = new CsvToSpatialFile(temptCsvList, true, 0, 1);
		Map<String, String> csvTitleType = new TreeMap<>();
		csvTitleType.put("x", "Double");
		csvTitleType.put("y", "Double");
		csvTitleType.put("z", "Double");
		csvConverter.setFieldType(csvTitleType);
		csvConverter.saveAsShp(GdalGlobal.temptFile + ".shp");
	}

	private AsciiBasicControl Krigingmethold() throws IOException, InterruptedException {
		/*
		 * 1. clear temptFolder
		 * 
		 * 2. run SAGA, to do kriging
		 * 
		 * 3. run gdal_translate convert .sdat to .asc
		 */

		sagaCmd();
//		while (!new File(GdalGlobal.temptFile + "_pridiction.sdat").exists()) {
//			sagaCmd();
//		}

		gdal_translateCmd();
//		while (!new File(GdalGlobal.temptFile + "_pridiction.asc").exists()) {
//			gdal_translateCmd();
//		}

		return new AsciiBasicControl(GdalGlobal.temptFile + "_pridiction.asc");
	}

	private void gdal_translateCmd() throws InterruptedException, IOException {
		/*
		 * GDAL_TANS CommandLine
		 */
		FileFunction.delete(GdalGlobal.temptFile + "_pridiction.asc");

		List<String> transCmd = new ArrayList<>();
		transCmd.add("cmd");
		transCmd.add("/c");
		transCmd.add("start");
		transCmd.add("/wait");
		transCmd.add("gdal_translate.exe");
		transCmd.add("-of");
		// convert .sdat to .asc
		transCmd.add("AAIGrid");
		transCmd.add(GdalGlobal.temptFile + "_pridiction.sdat");
		transCmd.add(GdalGlobal.temptFile + "_pridiction.asc");

		ProcessBuilder trans_builder = new ProcessBuilder();
		trans_builder.directory(new File(GdalGlobal.sagaBinFolder));
		trans_builder.command(transCmd);
		Process trans_process = trans_builder.start();
		trans_process.waitFor();
	}

	private void sagaCmd() throws InterruptedException, IOException {
		/*
		 * SAGA CommandLine
		 */
		List<String> sagaCmd = new ArrayList<>();

		// command line started
		sagaCmd.add("cmd");
		sagaCmd.add("/c");
		sagaCmd.add("start");
		sagaCmd.add("/wait");

		// SAGA api
		sagaCmd.add("saga_cmd.exe");
		sagaCmd.add("statistics_kriging");
		sagaCmd.add("\"Ordinary Kriging\"");

		// definition , 0 : standard deviation , 1 : variance
		sagaCmd.add("-TARGET_DEFINITION");
		sagaCmd.add(this.DEFINITION_SELECTED + "");

		// shape file add
		sagaCmd.add("-POINTS");
		sagaCmd.add("\"" + GdalGlobal.temptFile + ".shp\"");

		// field
		sagaCmd.add("-FIELD");
		sagaCmd.add("\"z\"");

		// variance model
		sagaCmd.add("-VAR_MODEL");
		sagaCmd.add("\"" + this.VAR_MODEL_SELECTED + "\"");

		// target boundary
		sagaCmd.add("-TARGET_USER_XMIN");
		sagaCmd.add(new BigDecimal(this.targetBoundary.get("minX") + 0.5 * this.cellSize)
				.setScale(5, BigDecimal.ROUND_HALF_UP).toString());
		sagaCmd.add("-TARGET_USER_XMAX");
		sagaCmd.add(new BigDecimal(this.targetBoundary.get("maxX") - 0.5 * this.cellSize)
				.setScale(5, BigDecimal.ROUND_HALF_UP).toString());
		sagaCmd.add("-TARGET_USER_YMIN");
		sagaCmd.add(new BigDecimal(this.targetBoundary.get("minY") + 0.5 * this.cellSize)
				.setScale(5, BigDecimal.ROUND_HALF_UP).toString());
		sagaCmd.add("-TARGET_USER_YMAX");
		sagaCmd.add(new BigDecimal(this.targetBoundary.get("maxY") - 0.5 * this.cellSize)
				.setScale(5, BigDecimal.ROUND_HALF_UP).toString());

		// cellSize
		sagaCmd.add("-TARGET_USER_SIZE");
		sagaCmd.add(this.cellSize + "");

		// fitting
		sagaCmd.add("-TARGET_USER_FITS");
		sagaCmd.add(this.FITTING_SELECTED + "");

		// search radius
		sagaCmd.add("-SEARCH_RADIUS");
		sagaCmd.add(this.SEARCH_RADIUS + "");

		// search point method
		sagaCmd.add("-SEARCH_POINTS_ALL");
		sagaCmd.add(this.SEARCH_POINTS_SELECTED + "");

		// search point min
		sagaCmd.add("-SEARCH_POINTS_MIN");
		sagaCmd.add(this.SEARCH_POINTS_MIN + "");

		// search point max
		sagaCmd.add("-SEARCH_POINTS_MAX");
		sagaCmd.add(this.SEARCH_POINTS_MAX + "");

		// saveAdd
		sagaCmd.add("-PREDICTION");
		sagaCmd.add("\"" + GdalGlobal.temptFile + "_pridiction.sdat\"");
		sagaCmd.add("-VARIANCE");
		sagaCmd.add("\"" + GdalGlobal.temptFile + "_variance.sdat\"");

		ProcessBuilder saga_builder = new ProcessBuilder();
		saga_builder.directory(new File(GdalGlobal.sagaBinFolder));
		saga_builder.command(sagaCmd);
		Process saga_process = saga_builder.start();
		saga_process.waitFor();
	}

	/*
	 * Kriging Setting
	 * 
	 * @ 1. definition
	 * 
	 * @ 2. model
	 * 
	 * @ 3. search direction
	 * 
	 * @ 4. search points(min , max)
	 * 
	 * @ 4. search radius
	 * 
	 * @ 5. cellSize
	 * 
	 * @ 6. fitting
	 * 
	 * @ 7. boundary
	 */
	public void setDefinition(int DEFINITION_SELECTED) {
		this.DEFINITION_SELECTED = DEFINITION_SELECTED;
	}

	public void setModel(String VAR_MODEL_SELECTED) {
		this.VAR_MODEL_SELECTED = VAR_MODEL_SELECTED;
	}

	public void setSearchPoint_Max(int SEARCH_POINTS_MAX) {
		this.SEARCH_POINTS_MAX = SEARCH_POINTS_MAX;
	}

	public void setSearchPoint_Min(int SEARCH_POINTS_MIN) {
		this.SEARCH_POINTS_MIN = SEARCH_POINTS_MIN;
	}

	public void setSearchDirection(int SEARCH_POINTS_SELECTED) {
		this.SEARCH_POINTS_SELECTED = SEARCH_POINTS_SELECTED;
	}

	public void setFitting(int FITTING_SELECTED) {
		this.FITTING_SELECTED = FITTING_SELECTED;
	}

	public void setCellSize(double cellSize) {
		this.cellSize = cellSize;
	}

	public void setRadius(double radius) {
		this.SEARCH_RADIUS = radius;
	}

	@Override
	public List<Double[]> getXYZ(List<Double[]> xyList) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		// check for the boundary
		if (this.targetBoundary.isEmpty()) {
			List<Double> xList = new ArrayList<>();
			List<Double> yList = new ArrayList<>();
			xyList.forEach(e -> {
				xList.add(e[0]);
				yList.add(e[1]);
			});

			AtCommonMath xStatics = new AtCommonMath(xList);
			AtCommonMath yStatics = new AtCommonMath(yList);
			this.targetBoundary.put("minX", xStatics.getMin() - 0.5 * this.cellSize);
			this.targetBoundary.put("maxX", xStatics.getMax() + 0.5 * this.cellSize);
			this.targetBoundary.put("minY", yStatics.getMin() - 0.5 * this.cellSize);
			this.targetBoundary.put("maxY", yStatics.getMax() + 0.5 * this.cellSize);
		}

		// get the interpolation
		AsciiBasicControl zAscii = Krigingmethold();
		List<Double[]> outList = new ArrayList<>();
		for (int index = 0; index < xyList.size(); index++) {
			double temptX = xyList.get(index)[0];
			double temptY = xyList.get(index)[1];
			outList.add(new Double[] { temptX, temptY, Double.parseDouble(zAscii.getValue(temptX, temptY)) });
		}

		return outList;
	}

	@Override
	public List<Double[]> getXYZ(Map<String, Double> boundary, double cellSize)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		this.targetBoundary = boundary;
		this.cellSize = cellSize;

		return this.Krigingmethold().converToXYZ();
	}

	@Override
	public List<Double[]> getGeometry(List<Geometry> geometryList) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		List<Double[]> xyList = new ArrayList<>();
		geometryList.forEach(e -> {
			Geometry centroid = e.Centroid();
			xyList.add(new Double[] { centroid.GetX(), centroid.GetY() });
		});

		return this.getXYZ(xyList);
	}

	@Override
	public AsciiBasicControl getAscii(Map<String, Double> boundary, double cellSize)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		this.targetBoundary = boundary;
		this.cellSize = cellSize;

		return this.Krigingmethold();
	}

	@Override
	public AsciiBasicControl getAscii(AsciiBasicControl ascii) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		this.targetBoundary = ascii.getBoundary();
		this.cellSize = ascii.getCellSize();

		return this.Krigingmethold();
	}

	public AsciiBasicControl getAscii(List<Double[]> xyList) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if (this.targetBoundary.isEmpty()) {
			List<Double> xList = new ArrayList<>();
			List<Double> yList = new ArrayList<>();
			xyList.forEach(e -> {
				xList.add(e[0]);
				yList.add(e[1]);
			});

			AtCommonMath xStatics = new AtCommonMath(xList);
			AtCommonMath yStatics = new AtCommonMath(yList);
			this.targetBoundary.put("minX", xStatics.getMin() - 0.5 * this.cellSize);
			this.targetBoundary.put("maxX", xStatics.getMax() + 0.5 * this.cellSize);
			this.targetBoundary.put("minY", yStatics.getMin() - 0.5 * this.cellSize);
			this.targetBoundary.put("maxY", yStatics.getMax() + 0.5 * this.cellSize);
		}

		return this.Krigingmethold();
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
//	private List<Double> Krigingmethold(List<Double[]> xyList) {
//		List<Double[]> propertyList = new ArrayList<>();
//		for (int index = 0; index < this.xyzList.size(); index++) {
//
//			// set regression y = ax + b
//			List<Double[]> matrixA = new ArrayList<>();
//			List<Double[]> matrixL = new ArrayList<>();
//			for (int scan = 0; scan < this.xyzList.size(); scan++) {
//
//				// skip the same point
//				if (index == scan) {
//					scan++;
//
//					// save the differ and distance
//				} else {
//					matrixA.add(new Double[] { getDis(xyzList.get(index), xyzList.get(scan)), 1. });
//					matrixL.add(new Double[] { Math.pow(xyzList.get(index)[2] - xyzList.get(scan)[2], 2) });
//				}
//			}
//
//			// weight matrix
//			List<Double[]> matrixW = new ArrayList<>();
//			for (int scan = 0; scan < matrixA.size(); scan++) {
//				List<Double> temptRow = new ArrayList<>();
//				for (int input = 0; input < matrixA.size(); input++) {
//					if (input == scan) {
//						temptRow.add(matrixA.get(scan)[0]);
//					} else {
//						temptRow.add(0.);
//					}
//				}
//				matrixW.add(temptRow.parallelStream().toArray(Double[]::new));
//			}
//
//			// start regression (A^t * A)^-1 * A^t * L
//			Double[][] matrixAt = new AtMatrix(matrixA).trans().getMatrix();
//			AtMatrix regressionFunction = new AtMatrix(matrixAt);
//
//			// ans[0] = a , ans[1] = b , y = a*x + b
//			Double[][] ans = regressionFunction.mult(matrixA).inverse().mult(matrixAt).mult(matrixL).getMatrix();
//			propertyList.add(new Double[] { ans[0][0], ans[0][1] });
//		}
//
//		// get ratio of each station
//		List<Double> zList = new ArrayList<>();
//		for (int target = 0; target < xyList.size(); target++) {
//			List<Double> ratio = new ArrayList<>();
//			for (int station = 0; station < propertyList.size(); station++) {
//				Double[] property = propertyList.get(station);
//				ratio.add(1. / (property[0] * getDis(xyList.get(target), xyzList.get(station)) + property[1]));
//			}
//			ratio = new AtCommonMath(ratio).getRatio();
//
//			// get the weighted Value
//			List<Double> temptValue = new ArrayList<>();
//			for (int station = 0; station < propertyList.size(); station++) {
//				temptValue.add(ratio.get(station) * xyzList.get(station)[2]);
//			}
//			zList.add(new AtCommonMath(temptValue).getSum());
//		}
//		return zList;
//	}

}
