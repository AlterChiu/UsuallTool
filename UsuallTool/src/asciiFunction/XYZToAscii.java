package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
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

	public XYZToAscii(String fileAdd) throws IOException {
		List<String[]> temptContent = new ArrayList<String[]>(Arrays.asList(new AtFileReader(fileAdd).getCsv(1, 0)));
		xyzContent = new ArrayList<>();
		for (String[] temptLine : temptContent) {
			xyzContent.add(new Double[] { Double.parseDouble(temptLine[0]), Double.parseDouble(temptLine[1]),
					Double.parseDouble(temptLine[2]) });
		}

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
		Double minX = this.xyzContent.get(0)[0];
		Double maxX = this.xyzContent.get(0)[0];
		Double minY = this.xyzContent.get(0)[1];
		Double maxY = this.xyzContent.get(0)[1];

		while (this.xyzContent.size() > 0) {
			double temptX = this.xyzContent.get(0)[0];
			double temptY = this.xyzContent.get(0)[1];

			// get the boundary of the xyList
			if (temptX > maxX) {
				maxX = temptX;
			} else if (temptX < minX) {
				minX = temptX;
			}

			if (temptY > maxY) {
				maxY = temptY;
			} else if (temptY < minY) {
				minY = temptY;
			}

			// get the xyzList
			this.xList.add(temptX);
			this.yList.add(temptY);
			this.zList.add(this.xyzContent.get(0)[2]);
			this.xyzContent.remove(0);
		}
	}

	private void setProperty() {
		// get the boundary
		AtCommonMath xMath = new AtCommonMath(this.xList);
		AtCommonMath yMath = new AtCommonMath(this.yList);

		this.maxX = new BigDecimal(xMath.getMax() + 0.5 * this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();
		this.minY = new BigDecimal(yMath.getMin() - 0.5 * this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();

		this.minX = new BigDecimal(new AtCommonMath(this.xList).getMin() - 0.5 * this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();
		this.maxY = new BigDecimal(new AtCommonMath(this.yList).getMax() + 0.5 * this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();

		xMath.clear();
		yMath.clear();

		// set the column and row
		// reBoundary the xyList
		int row = new BigDecimal((this.maxY - this.minY) / this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_UP).intValue() + 2;
		int column = new BigDecimal((maxX - minX) / this.cellSize).setScale(this.coordinateScale, BigDecimal.ROUND_UP)
				.intValue() + 2;
		this.maxX = new BigDecimal((column ) * this.cellSize + this.minX)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();
		this.minY = new BigDecimal(this.maxY - (row ) * this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();

		this.property.put("bottomX", new BigDecimal(this.minX + this.cellSize / 2)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).toString());
		this.property.put("bottomY", new BigDecimal(this.minY + this.cellSize / 2)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).toString());
		this.property.put("topX", new BigDecimal(this.maxX - this.cellSize / 2)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).toString());
		this.property.put("topY", new BigDecimal(this.maxY - this.cellSize / 2)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).toString());
		this.property.put("cellSize", this.cellSize + "");
		this.property.put("noData", this.noData);
		this.property.put("row", row + "");
		this.property.put("column", column + "");

		System.out.println("XYZ");
		System.out.println("MinX " + this.property.get("bottomX"));
		System.out.println("MinY " + property.get("bottomY"));
		System.out.println("MaxX " + property.get("topX"));
		System.out.println("MaxY " + property.get("topY"));

		System.out.println();

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
					.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).intValue();
			int temptColumn = new BigDecimal((this.xList.get(index) - boundaryMinX) / this.cellSize)
					.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).intValue();
			this.outTree.get(temptRow).get(temptColumn).add(this.zList.get(index));
		}
	}

	private void setAsciiContent() {
		Integer[] rowList = this.outTree.keySet().parallelStream().toArray(Integer[]::new);
		for (int row : rowList) {

			List<String> temptList = new ArrayList<String>();
			Integer[] columnList = this.outTree.get(row).keySet().parallelStream().toArray(Integer[]::new);
			for (int column : columnList) {
				double temptValue = new AtCommonMath(this.outTree.get(row).get(column)).getMean();
				try {
					temptList.add(
							new BigDecimal(temptValue).setScale(this.valeuScale, BigDecimal.ROUND_HALF_UP).toString());
				} catch (Exception e) {
					temptList.add(this.noData);
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
	public void start() {
		setProperty();
		initialTreeMap();
		getSortedTree();
		setAsciiContent();
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

	// <================================================>
}
