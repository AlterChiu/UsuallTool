package asciiFunction;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileWriter;

public class AsciiMerge {
	private List<AsciiBasicControl> asciiList;
	private Map<String, Double> boundaryMap;
	private double cellSize;
	private String outNullValue;

	// <===================>
	// < this is the construct >
	// <===================>
	// <==========================================>
	public AsciiMerge(String file1, String file2) throws IOException {
		this.asciiList = new ArrayList<AsciiBasicControl>();
		this.asciiList.add(new AsciiBasicControl(file1));
		this.asciiList.add(new AsciiBasicControl(file2));
		this.cellSize = Double.parseDouble(this.asciiList.get(0).getProperty().get("cellSize"));
		this.outNullValue = this.asciiList.get(0).getProperty().get("noData");
	}
	
	public AsciiMerge(String[][] file1, String[][] file2) throws IOException {
		this.asciiList = new ArrayList<AsciiBasicControl>();
		this.asciiList.add(new AsciiBasicControl(file1));
		this.asciiList.add(new AsciiBasicControl(file2));
		this.cellSize = Double.parseDouble(this.asciiList.get(0).getProperty().get("cellSize"));
		this.outNullValue = this.asciiList.get(0).getProperty().get("noData");
	}
	
	public AsciiMerge(String file1, String[][] file2) throws IOException {
		this.asciiList = new ArrayList<AsciiBasicControl>();
		this.asciiList.add(new AsciiBasicControl(file1));
		this.asciiList.add(new AsciiBasicControl(file2));
		this.cellSize = Double.parseDouble(this.asciiList.get(0).getProperty().get("cellSize"));
		this.outNullValue = this.asciiList.get(0).getProperty().get("noData");
	}
	
	public AsciiMerge(String[][] file1, String file2) throws IOException {
		this.asciiList = new ArrayList<AsciiBasicControl>();
		this.asciiList.add(new AsciiBasicControl(file1));
		this.asciiList.add(new AsciiBasicControl(file2));
		this.cellSize = Double.parseDouble(this.asciiList.get(0).getProperty().get("cellSize"));
		this.outNullValue = this.asciiList.get(0).getProperty().get("noData");
	}

	public AsciiMerge(AsciiBasicControl ascii1, AsciiBasicControl ascii2) {
		this.asciiList = new ArrayList<AsciiBasicControl>();
		this.asciiList.add(ascii1);
		this.asciiList.add(ascii2);
		this.cellSize = Double.parseDouble(this.asciiList.get(0).getProperty().get("cellSize"));
		this.outNullValue = this.asciiList.get(0).getProperty().get("noData");
	}

	public AsciiMerge(List<AsciiBasicControl> asciiList) {
		this.asciiList = asciiList;
		this.cellSize = Double.parseDouble(this.asciiList.get(0).getProperty().get("cellSize"));
		this.outNullValue = this.asciiList.get(0).getProperty().get("noData");
	}

	public void addAscii(String fileAdd) throws IOException {
		this.asciiList.add(new AsciiBasicControl(fileAdd));
	}

	public void addAscii(AsciiBasicControl ascii) {
		this.asciiList.add(ascii);
	}

	public void setCellSize(double cellSize) {
		this.cellSize = new BigDecimal(cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setNullValue(String outNullValue) {
		this.outNullValue = outNullValue;
	}
	// <=========================================>

	public List<String[]> saveMergeAscii(String saveAdd) throws IOException {
		List<String[]> temptList = this.getMergeAscii();
		new AtFileWriter(temptList.parallelStream().toArray(String[][]::new), saveAdd).textWriter("    ");
		return temptList;
	}

	public String[][] getMergeAsciiArray() {
		return this.getMergeAscii().parallelStream().toArray(String[][]::new);
	}

	public List<String[]> getMergeAscii() {
		getMergeBoundary();
		resetBoundary();
		List<String[]> asciiGrid = setGridValue();

		asciiGrid.add(0, new String[] { "nodata_value", this.outNullValue });
		asciiGrid.add(0, new String[] { "cellSize", this.cellSize + "" });
		asciiGrid.add(0, new String[] { "yllCenter", this.boundaryMap.get("bottomY") + "" });
		asciiGrid.add(0, new String[] { "xllCenter", this.boundaryMap.get("bottomX") + "" });
		asciiGrid.add(0, new String[] { "nrows", this.boundaryMap.get("row") + "" });
		asciiGrid.add(0, new String[] { "ncols", this.boundaryMap.get("column") + "" });
		return asciiGrid;
	}

	private List<String[]> setGridValue() {
		// outList
		List<String[]> outList = new ArrayList<String[]>();

		// get the total grid
		for (int row = 0; row < this.boundaryMap.get("row"); row++) {
			List<String> temptRow = new ArrayList<String>();

			for (int column = 0; column < this.boundaryMap.get("column"); column++) {
				List<Double> valueList = new ArrayList<Double>();
				double temptX = this.boundaryMap.get("minX") + this.cellSize * column;
				double temptY = this.boundaryMap.get("maxY") + this.cellSize * row;

				// get the grid boundary
				Path2D gridPath = new Path2D.Double();
				gridPath.moveTo(temptX, temptY);
				gridPath.lineTo(temptX + this.cellSize, temptY);
				gridPath.lineTo(temptX + this.cellSize, temptY - this.cellSize);
				gridPath.lineTo(temptX, temptY - this.cellSize);

				// check for each asciiList
				for (AsciiBasicControl ascii : this.asciiList) {
					String nullValue = ascii.getProperty().get("noData");
					int leftTop[] = ascii.getPosition(temptX, temptY);
					int rightDown[] = ascii.getPosition(temptX + this.cellSize, temptY - this.cellSize);

					// get the boundary of the ascii by the grida path
					for (int asciiRow = leftTop[1]; asciiRow >= rightDown[1]; asciiRow--) {
						for (int asciiColumn = leftTop[0]; asciiColumn <= rightDown[1]; asciiColumn++) {
							double asciiCoordinate[] = ascii.getCoordinate(asciiColumn, asciiRow);
							String temptValue = ascii.getValue(asciiColumn, asciiRow);

							if (gridPath.contains(asciiCoordinate[0], asciiCoordinate[1])
									&& !temptValue.equals(nullValue)) {
								valueList.add(Double.parseDouble(temptValue));
							}
						}
					}
				}
				temptRow.add(new AtCommonMath(valueList).getMean() + "");
			}
			outList.add(temptRow.parallelStream().toArray(String[]::new));
		}
		return outList;
	}

	private void resetBoundary() {
		double minX = new BigDecimal(this.boundaryMap.get("minX")).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		double maxX = new BigDecimal(this.boundaryMap.get("maxX")).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		double minY = new BigDecimal(this.boundaryMap.get("minY")).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		double maxY = new BigDecimal(this.boundaryMap.get("maxY")).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

		int column = new BigDecimal((maxX - minX) / cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).intValue();
		int row = new BigDecimal((maxY - minY) / cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).intValue();
		maxX = minX + column * cellSize;
		maxY = minY + row * cellSize;

		this.boundaryMap.put("minX", minX);
		this.boundaryMap.put("maxX", maxX);
		this.boundaryMap.put("minY", minY);
		this.boundaryMap.put("maxY", maxY);
		this.boundaryMap.put("row", row + 0.);
		this.boundaryMap.put("column", column + 0.);
		this.boundaryMap.put("bottomX",
				new BigDecimal(minX + 0.5 * this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		this.boundaryMap.put("bottomY",
				new BigDecimal(minY + 0.5 * this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		this.boundaryMap.put("bottomX",
				new BigDecimal(maxX - 0.5 * this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		this.boundaryMap.put("bottomX",
				new BigDecimal(maxY - 0.5 * this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	private void getMergeBoundary() {
		this.boundaryMap = new TreeMap<String, Double>();
		Map<String, String> asciiProperty = this.asciiList.get(0).getProperty();
		double minX = Double.parseDouble(asciiProperty.get("bottomX")) - 0.5 * cellSize;
		double minY = Double.parseDouble(asciiProperty.get("bottomY")) - 0.5 * cellSize;
		double maxX = Double.parseDouble(asciiProperty.get("topX")) + 0.5 * cellSize;
		double maxY = Double.parseDouble(asciiProperty.get("topY")) + 0.5 * cellSize;

		for (int index = 1; index < this.asciiList.size(); index++) {
			Map<String, String> temptProeprty = this.asciiList.get(1).getProperty();
			double temptCellSize = Double.parseDouble(temptProeprty.get("cellSize"));

			double temptMinX = Double.parseDouble(temptProeprty.get("bottomX")) - 0.5 * temptCellSize;
			double temptMinY = Double.parseDouble(temptProeprty.get("bottomY")) - 0.5 * temptCellSize;
			double temptMaxX = Double.parseDouble(temptProeprty.get("topX")) + 0.5 * temptCellSize;
			double temptMaxY = Double.parseDouble(temptProeprty.get("topY")) + 0.5 * temptCellSize;

			if (temptMinX < minX) {
				minX = temptMinX;
			}
			if (temptMaxX > maxX) {
				maxX = temptMaxX;
			}
			if (temptMinY < minY) {
				minY = temptMinY;
			}
			if (temptMaxY > maxY) {
				maxY = temptMaxY;
			}
		}

		this.boundaryMap.put("minX", minX);
		this.boundaryMap.put("maxX", maxX);
		this.boundaryMap.put("minY", minY);
		this.boundaryMap.put("maxY", maxY);
	}

}
