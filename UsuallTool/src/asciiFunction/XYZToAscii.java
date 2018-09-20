package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class XYZToAscii {
	private TreeMap<String, String> property = new TreeMap<String, String>();
	private ArrayList<String[]> xyzContent;
	private HashMap<String, String> valueList = new HashMap<String, String>();
	private int scale = 3;
	private String[][] asciiGrid;
	private int row;
	private int column;
	private double cellSize = 1.0;
	private String noData = "-99";

	public XYZToAscii(String fileAdd) throws IOException {
		this.xyzContent = new ArrayList<String[]>(Arrays.asList(new AtFileReader(fileAdd).getCsv()));
		this.setProperty();
		this.setGrid();
	}

	public TreeMap<String, String> getProperty() {
		return this.property;
	}

	public String[][] getAsciiGrid() {
		return this.asciiGrid;
	}

	public String[][] getAscii() {
		ArrayList<String[]> outArray = new ArrayList<String[]>();
		outArray.add(new String[] { "ncols", this.property.get("column") });
		outArray.add(new String[] { "nrows", this.property.get("row") });
		outArray.add(new String[] { "xllCenter", this.property.get("bottomX") });
		outArray.add(new String[] { "yllCenter", this.property.get("bottomY") });
		outArray.add(new String[] { "cellsize", this.property.get("cellSize") });
		outArray.add(new String[] { "nodata_value", this.property.get("noData") });

		Arrays.asList(this.asciiGrid).forEach(line -> outArray.add(line));
		return outArray.parallelStream().toArray(String[][]::new);
	}

	public void saveAscii(String fileAdd) throws IOException {
		new AtFileWriter(this.getAscii(), fileAdd).textWriter("    ");
	}

	private void setGrid() {
		System.out.println("setGrid");
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));

		ArrayList<String[]> outArray = new ArrayList<String[]>();
		for (int row = 0; row < this.row; row++) {
			String temptY = new BigDecimal(startY - this.cellSize * row).setScale(this.scale, BigDecimal.ROUND_HALF_UP)
					.toString();
			ArrayList<String> tempArray = new ArrayList<String>();
			for (int column = 0; column < this.column; column++) {
				String temptX = new BigDecimal(startX + this.cellSize * column)
						.setScale(this.scale, BigDecimal.ROUND_HALF_UP).toString();
				String temptPosition = temptX + "_" + temptY;
				if (this.valueList.containsKey(temptPosition)) {
					tempArray.add(this.valueList.get(temptPosition));
					this.valueList.remove(temptPosition);
				} else {
					tempArray.add(this.noData);
				}
			}
			outArray.add(tempArray.parallelStream().toArray(String[]::new));
		}
		this.asciiGrid = outArray.parallelStream().toArray(String[][]::new);
		System.out.println("complete setGrid");
	}

	private void setProperty() {
		Double minX = 99999999999.;
		Double maxX = -99999999999.;
		Double minY = 99999999999.;
		Double maxY = -99999999999.;
		for (String[] line : this.xyzContent) {
			double temptX = Double.parseDouble(line[0]);
			double temptY = Double.parseDouble(line[1]);

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

			this.valueList.put(new BigDecimal(temptX).setScale(this.scale, BigDecimal.ROUND_HALF_UP).toString() + "_"
					+ new BigDecimal(temptY).setScale(this.scale, BigDecimal.ROUND_HALF_UP), line[2]);
		}
		this.xyzContent.clear();
		System.out.println("set boundary");

		int row = new BigDecimal((maxY - minY) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue()+1;
		int column = new BigDecimal((maxX - minX) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue()+1;

		System.out.println("setProperty");
		property.put("bottomX", new BigDecimal(minX).setScale(this.scale, BigDecimal.ROUND_HALF_UP).toString());
		property.put("topX", new BigDecimal(maxX).setScale(this.scale, BigDecimal.ROUND_HALF_UP).toString());
		property.put("bottomY", new BigDecimal(minY).setScale(this.scale, BigDecimal.ROUND_HALF_UP).toString());
		property.put("topY", new BigDecimal(maxY).setScale(this.scale, BigDecimal.ROUND_HALF_UP).toString());
		property.put("noData", noData);
		property.put("cellSize", new BigDecimal(this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
		property.put("column", column + "");
		property.put("row", row + "");

		this.row = row;
		this.column = column;
	}

	public XYZToAscii setCellSize(double cellSize) {
		this.cellSize = cellSize;
		return this;
	}

	public XYZToAscii setScale(int scale) {
		this.scale = scale;
		return this;
	}
}
