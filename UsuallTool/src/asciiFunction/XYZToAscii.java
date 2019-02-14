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

	private ArrayList<String[]> xyzContent;
	// y,x,z
	private TreeMap<Integer, TreeMap<Integer, List<Double>>> outTree = new TreeMap<Integer, TreeMap<Integer, List<Double>>>();
	private List<String[]> outList = new ArrayList<>();
	private double minX = -9999;
	private double maxY = -9999;
	private double minY;
	private double maxX;
	private double cellSize = 1.0;
	private String noData = "-99";

	private int coordinateScale = 4;
	private int valeuScale = 3;

	private List<Double> xList = new ArrayList<Double>();
	private List<Double> yList = new ArrayList<Double>();
	private List<Double> zList = new ArrayList<Double>();

	public XYZToAscii(String fileAdd) throws IOException {
		this.xyzContent = new ArrayList<String[]>(Arrays.asList(new AtFileReader(fileAdd).getCsv()));
		this.setXYZList();
	}

	public XYZToAscii(String[][] content) {
		this.xyzContent = new ArrayList<String[]>(Arrays.asList(content));
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
		Double minX = Double.parseDouble(this.xyzContent.get(0)[0]);
		Double maxX = Double.parseDouble(this.xyzContent.get(0)[0]);
		Double minY = Double.parseDouble(this.xyzContent.get(0)[1]);
		Double maxY = Double.parseDouble(this.xyzContent.get(0)[1]);

		while (this.xyzContent.size() > 0) {
			double temptX = Double.parseDouble(this.xyzContent.get(0)[0]);
			double temptY = Double.parseDouble(this.xyzContent.get(0)[1]);

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
			this.zList.add(Double.parseDouble(this.xyzContent.get(0)[2]));
			this.xyzContent.remove(0);
		}
		System.out.println("xyzList complete");
	}

	private void setProperty() {
		// get the boundary
		AtCommonMath xMath = new AtCommonMath(this.xList);
		AtCommonMath yMath = new AtCommonMath(this.yList);

		this.maxX = new BigDecimal(xMath.getMax()).setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		this.minY = new BigDecimal(yMath.getMin()).setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		// if the startXY is extinct pass it
		// or recalculate it
		if (Math.abs(this.minX + 9999) < 1 || Math.abs(this.maxY + 9999) < 1) {
			this.minX = new BigDecimal(new AtCommonMath(this.xList).getMin())
					.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();
			this.maxY = new BigDecimal(new AtCommonMath(this.yList).getMax())
					.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		xMath.clear();
		yMath.clear();

		// set the column and row
		// reBoundary the xyList
		int row = new BigDecimal((this.maxY - this.minY) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue();
		int column = new BigDecimal((this.maxX - this.minX) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue();
		this.maxX = new BigDecimal(column * this.cellSize + this.minX)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();
		this.minY = new BigDecimal(this.maxY - row * this.cellSize)
				.setScale(this.coordinateScale, BigDecimal.ROUND_HALF_UP).doubleValue();

		this.property.put("bottomX", this.minX - this.cellSize / 2 + "");
		this.property.put("bottomY", this.minY - this.cellSize / 2 + "");
		this.property.put("topX", this.maxX + this.cellSize / 2 + "");
		this.property.put("topY", this.maxY + this.cellSize / 2 + "");
		this.property.put("cellSize", this.cellSize + "");
		this.property.put("noData", this.noData);
		this.property.put("row", row + 1 + "");
		this.property.put("column", column + 1 + "");
	}

	// the order of tree , y,x,z
	private void initialTreeMap() {
		this.outTree.clear();
		int row = new BigDecimal((this.maxY - this.minY) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue() + 1;
		int column = new BigDecimal((this.maxX - this.minX) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue() + 1;
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
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int temptColumn = new BigDecimal((this.xList.get(index) - boundaryMinX) / this.cellSize)
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
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
		System.out.println("complete set property");
		initialTreeMap();
		System.out.println("complete initial treeMap");
		getSortedTree();
		System.out.println("complete storeTree");
		setAsciiContent();
		System.out.println("complete ascii Content");
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
