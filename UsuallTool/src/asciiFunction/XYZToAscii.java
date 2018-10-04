package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	private double minX;
	private double maxY;
	private double minY;
	private double maxX;
	private double cellSize = 1.0;
	private String noData = "-99";

	private List<Double> xList = new ArrayList<Double>();
	private List<Double> yList = new ArrayList<Double>();
	private List<Double> zList = new ArrayList<Double>();

	public XYZToAscii(String fileAdd) throws IOException {
		this.xyzContent = new ArrayList<String[]>(Arrays.asList(new AtFileReader(fileAdd).getCsv()));
		this.setXYZList();
	}

	// <===============================================>
	// < output function >
	// <===============================================>
	public TreeMap<String, String> getProperty() {
		return this.property;
	}

	public void saveAscii(String fileAdd) throws IOException {
		new AtFileWriter(this.getAsciiFile(), fileAdd).textWriter("    ");
	}

	public String[][] getAsciiGrid() {
		return this.outList.parallelStream().toArray(String[][]::new);
	}

	public String[][] getAsciiFile() {
		ArrayList<String[]> outArray = new ArrayList<String[]>();
		outArray.add(new String[] { "ncols", this.property.get("column") });
		outArray.add(new String[] { "nrows", this.property.get("row") });
		outArray.add(new String[] { "xllCenter", this.property.get("bottomX") });
		outArray.add(new String[] { "yllCenter", this.property.get("bottomY") });
		outArray.add(new String[] { "cellsize", this.property.get("cellSize") });
		outArray.add(new String[] { "nodata_value", this.property.get("noData") });

		Arrays.asList(this.outList).forEach(line -> outArray.add(line.parallelStream().toArray(String[]::new)));
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
			this.zList.add(Double.parseDouble(this.xyzContent.get(0)[3]));
			this.xyzContent.remove(0);
		}
	}

	private void setProperty() {
		AtCommonMath xMath = new AtCommonMath(this.xList);
		AtCommonMath yMath = new AtCommonMath(this.yList);
		try {
			this.maxX = xMath.getMax();
			this.minY = yMath.getMin();
			String.valueOf(this.minX);
			String.valueOf(this.maxY);
		} catch (Exception e) {
			this.minX = new AtCommonMath(this.xList).getMin();
			this.maxY = new AtCommonMath(this.yList).getMax();
		}
		xMath.clear();
		yMath.clear();

		// set the column and row
		// reBoundary the xyList
		int row = new BigDecimal((this.maxY - this.minY) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue() + 1;
		int column = new BigDecimal((this.maxX - this.minX) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP)
				.intValue() + 1;
		this.maxX = column * this.cellSize + this.minX;
		this.minY = this.maxY - row * this.cellSize;

		this.property.put("bottomX", this.minX + "");
		this.property.put("bottomY", this.minY + "");
		this.property.put("topX", this.maxX + "");
		this.property.put("topY", this.maxY + "");
		this.property.put("cellSize", this.cellSize + "");
		this.property.put("noData", this.noData);
		this.property.put("row", row + "");
		this.property.put("column", column + "");
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
		double boundaryMinX = this.minX - this.cellSize * 0.5;
		double boundaryMaxY = this.maxY + this.cellSize * 0.5;

		for (int index = 0; index < this.xList.size(); index++) {
			int temptRow = new BigDecimal((boundaryMaxY - this.yList.get(index)) / this.cellSize)
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int temptColumn = new BigDecimal((this.xList.get(index) - boundaryMinX) / this.cellSize)
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			this.outTree.get(temptRow).get(temptColumn).add(this.zList.get(index));
		}
	}

	private void setAsciiProperty() {
		Integer[] rowList = this.outTree.keySet().parallelStream().toArray(Integer[]::new);
		for (int row : rowList) {
			List<String> temptList = new ArrayList<String>();
			Integer[] columnList = this.outTree.get(row).keySet().parallelStream().toArray(Integer[]::new);
			for (int column : columnList) {
				temptList.add(new AtCommonMath(this.outTree.get(row).get(column)).getMean() + "");
				this.outTree.get(row).remove(column);
			}
			this.outTree.remove(row);
		}
	}

	public void start() {
		setProperty();
		initialTreeMap();
		getSortedTree();
		setAsciiProperty();
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

	// <================================================>
}
