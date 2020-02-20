package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class XYZToAscii {
	private TreeMap<String, String> property = new TreeMap<String, String>();

	private List<Double[]> xyzContent;
	// y,x,z
	private TreeMap<Integer, TreeMap<Integer, List<Double>>> outTree = new TreeMap<Integer, TreeMap<Integer, List<Double>>>();
	private List<String[]> outList = new ArrayList<>();
	private double minX = -9999;
	private double maxY = -9999;
	private double minY;
	private double maxX;
	private double cellSize = 1.0;
	private String noData = "-99";

	private int coordinateScale = 5;
	private int valeuScale = 3;

	private List<Double> xList = new ArrayList<Double>();
	private List<Double> yList = new ArrayList<Double>();
	private List<Double> zList = new ArrayList<Double>();

	public XYZToAscii(List<Double[]> xyzContent) throws IOException {
		this.xyzContent = xyzContent;
		this.setXYZList();
	}

	public XYZToAscii(String[][] content) {
		List<String[]> temptContent = new ArrayList<String[]>(Arrays.asList(content));
		xyzContent = new ArrayList<>();
		for (String[] temptLine : temptContent) {
			xyzContent.add(new Double[] { Double.parseDouble(temptLine[0]), Double.parseDouble(temptLine[1]),
					Double.parseDouble(temptLine[2]) });
		}
		this.setXYZList();
	}

	// <===============================================>
	// < output function >
	// <===============================================>
	public TreeMap<String, String> getProperty() {
		return this.property;
	}

	public void saveAscii(String fileAdd) throws IOException {
		new AtFileWriter(this.getAsciiFile(), fileAdd).textWriter("  ");
	}

	public String[][] getAsciiGrid() {
		return this.outList.parallelStream().toArray(String[][]::new);
	}

	public String[][] getAsciiFile() {
		ArrayList<String[]> outArray = new ArrayList<String[]>();
		outArray.add(new String[] { "NCOLS", this.property.get("column") });
		outArray.add(new String[] { "NROWS", this.property.get("row") });
		outArray.add(new String[] { "XLLCENTER", this.property.get("bottomX") });
		outArray.add(new String[] { "YLLCENTER", this.property.get("bottomY") });
		outArray.add(new String[] { "CELLSIZE", this.property.get("cellSize") });
		outArray.add(new String[] { "NODATA_VALUE", this.property.get("noData") });

		this.outList.forEach(lineData -> outArray.add(lineData));
		return outArray.parallelStream().toArray(String[][]::new);
	}
	// <===============================================>

	/**
	 * 
	 * 
	 */
	// <================================================>
	// < private function >
	// <================================================>
	private void setXYZList() {
		for (Double[] temptLine : this.xyzContent) {
			// get the xyzList
			this.xList.add(temptLine[0]);
			this.yList.add(temptLine[1]);
			this.zList.add(temptLine[2]);
		}
		this.xyzContent.clear();
	}

	private void setProperty() {
		// get the boundary
		AtCommonMath xMath = new AtCommonMath(this.xList);
		AtCommonMath yMath = new AtCommonMath(this.yList);

		this.maxX = new BigDecimal(xMath.getMax() + 0.5 * this.cellSize)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).doubleValue();
		this.minY = new BigDecimal(yMath.getMin() - 0.5 * this.cellSize)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).doubleValue();

		this.minX = new BigDecimal(new AtCommonMath(this.xList).getMin() - 0.5 * this.cellSize)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).doubleValue();
		this.maxY = new BigDecimal(new AtCommonMath(this.yList).getMax() + 0.5 * this.cellSize)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).doubleValue();

		xMath.clear();
		yMath.clear();

		// set the column and row
		// reBoundary the xyList
		int row = new BigDecimal((this.maxY - this.minY) / this.cellSize)
				.setScale(this.coordinateScale, RoundingMode.UP).intValue() + 2;
		int column = new BigDecimal((maxX - minX) / this.cellSize).setScale(this.coordinateScale, RoundingMode.UP)
				.intValue() + 2;
		this.maxX = new BigDecimal((column) * this.cellSize + this.minX)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).doubleValue();
		this.minY = new BigDecimal(this.maxY - (row) * this.cellSize)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).doubleValue();

		this.property.put("bottomX", new BigDecimal(this.minX + this.cellSize / 2)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).toString());
		this.property.put("bottomY", new BigDecimal(this.minY + this.cellSize / 2)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).toString());
		this.property.put("topX", new BigDecimal(this.maxX - this.cellSize / 2)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).toString());
		this.property.put("topY", new BigDecimal(this.maxY - this.cellSize / 2)
				.setScale(this.coordinateScale, RoundingMode.HALF_UP).toString());

		this.property.put("cellSize", this.cellSize + "");
		this.property.put("noData", this.noData);
		this.property.put("row", row + "");
		this.property.put("column", column + "");
	}

	// the order of tree , y,x,z
	private void initialTreeMap() {
		this.outTree.clear();
		int row = Integer.parseInt(this.property.get("row"));
		int column = Integer.parseInt(this.property.get("column"));
		for (int temptRow = 0; temptRow < row; temptRow++) {
			TreeMap<Integer, List<Double>> rowTree = new TreeMap<>();
			for (int temptColumn = 0; temptColumn < column; temptColumn++) {
				rowTree.put(temptColumn, new ArrayList<Double>());
			}
			this.outTree.put(temptRow, rowTree);
		}
	}

	private void getSortedTree() {
		double boundaryMinX = this.minX;
		double boundaryMaxY = this.maxY;

		for (int index = 0; index < this.xList.size(); index++) {
			int temptRow = new BigDecimal((boundaryMaxY - this.yList.get(index)) / this.cellSize)
					.setScale(this.coordinateScale, RoundingMode.HALF_UP).intValue();
			int temptColumn = new BigDecimal((this.xList.get(index) - boundaryMinX) / this.cellSize)
					.setScale(this.coordinateScale, RoundingMode.HALF_UP).intValue();
			this.outTree.get(temptRow).get(temptColumn).add(this.zList.get(index));
		}
	}

	private void setAsciiContent() {
		Integer[] rowList = this.outTree.keySet().parallelStream().toArray(Integer[]::new);
		for (int row : rowList) {

			List<String> temptList = new ArrayList<String>();
			Integer[] columnList = this.outTree.get(row).keySet().parallelStream().toArray(Integer[]::new);
			for (int column : columnList) {

				List<Double> valueList = this.outTree.get(row).get(column);

				if (valueList.size() == 1) {
					temptList.add(AtCommonMath.getDecimal_String(valueList.get(0), this.valeuScale));
				} else if (valueList.size() == 0) {
					temptList.add(this.noData);
				} else {
					temptList.add(new AtCommonMath(valueList).getMean(this.valeuScale) + "");
				}

				this.outTree.get(row).remove(column);
			}
			this.outList.add(temptList.parallelStream().toArray(String[]::new));
			this.outTree.remove(row);
		}
	}
	// <============================================================>

	/*
	 * 
	 * 
	 */
	// <=======================================>
	// <start working>
	// <=======================================>
	public XYZToAscii start() {
		long start = System.currentTimeMillis();
		setProperty();
		System.out.println(System.currentTimeMillis() - start);
		initialTreeMap();
		System.out.println(System.currentTimeMillis() - start);
		getSortedTree();
		System.out.println(System.currentTimeMillis() - start);
		setAsciiContent();
		System.out.println(System.currentTimeMillis() - start);
		return this;
	}
	// <================================================>

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	// <==============================================>
	// < public setting >
	// <==============================================>
	public XYZToAscii setStartXY(double x, double y) {
		this.minX = x;
		this.maxY = y;
		return this;
	}

	public XYZToAscii setCellSize(double cellSize) {
		this.cellSize = cellSize;
		return this;
	}

	public XYZToAscii setCoordinateScale(int scale) {
		this.coordinateScale = scale;
		return this;
	}

	public XYZToAscii setValueScale(int scale) {
		this.valeuScale = scale;
		return this;
	}

	public XYZToAscii setNullValue(String nullValue) {
		this.noData = nullValue;
		return this;
	}
	// <================================================>
}
