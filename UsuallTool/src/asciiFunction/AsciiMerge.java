package asciiFunction;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
	// <================================================>

	/*
	 * user function
	 */
	// <===============================================>
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

	public AsciiBasicControl saveMergedAscii(String saveAdd) throws IOException {
		AsciiBasicControl temptList = this.getMergedAscii();
		new AtFileWriter(temptList.getAsciiFile(), saveAdd).textWriter("    ");
		return temptList;
	}

	public AsciiBasicControl getMergedAscii() throws IOException {
		getMergeBoundary();
		resetBoundary();
		List<String[]> asciiGrid = setGridValue();

		asciiGrid.add(0, new String[] { "nodata_value", this.outNullValue });
		asciiGrid.add(0, new String[] { "cellSize", this.cellSize + "" });
		asciiGrid.add(0, new String[] { "yllCenter", this.boundaryMap.get("bottomY") + "" });
		asciiGrid.add(0, new String[] { "xllCenter", this.boundaryMap.get("bottomX") + "" });
		asciiGrid.add(0, new String[] { "nrows", this.boundaryMap.get("row").intValue() + "" });
		asciiGrid.add(0, new String[] { "ncols", this.boundaryMap.get("column").intValue() + "" });
		return new AsciiBasicControl(asciiGrid.parallelStream().toArray(String[][]::new));
	}

	private List<String[]> setGridValue() {
		// outList
		List<String[]> outList = new ArrayList<String[]>();

		// get the total grid
		for (int row = 0; row < this.boundaryMap.get("row"); row++) {
			List<String> temptRow = new ArrayList<String>();
			double temptCenterY = this.boundaryMap.get("topY") - row * this.cellSize;

			for (int column = 0; column < this.boundaryMap.get("column"); column++) {
				List<Double> valueList = new ArrayList<Double>();
				double temptCenterX = this.boundaryMap.get("bottomX") + column * this.cellSize;

				// check for each asciiList
				for (AsciiBasicControl ascii : this.asciiList) {

					if (ascii.isContain(temptCenterX, temptCenterY)) {
						String nullValue = ascii.getProperty().get("noData");

						int topLeftPosition[] = ascii.getPosition(temptCenterX - this.cellSize * 0.49999,
								temptCenterY + this.cellSize * 0.49999);
						int bottomRightPosition[] = ascii.getPosition(temptCenterX + this.cellSize * 0.49999,
								temptCenterY - this.cellSize * 0.49999);

						for (int asciiRow = topLeftPosition[1]; asciiRow <= bottomRightPosition[1]; asciiRow++) {
							for (int asciiColumn = topLeftPosition[0]; asciiColumn <= bottomRightPosition[0]; asciiColumn++) {
								String temptValue = ascii.getValue(asciiColumn, asciiRow);
								if (!temptValue.equals(nullValue)) {
									valueList.add(Double.parseDouble(temptValue));
								}
							}
						}
					}
				}

				// if there is no value in this grid
				// set the grid by null value
				try {
					temptRow.add(new AtCommonMath(valueList).getMean() + "");
				} catch (Exception e) {
					temptRow.add(this.outNullValue);
				}
			}

			// add the row values to the asiiList
			outList.add(temptRow.parallelStream().toArray(String[]::new));
		}
		return outList;
	}
	// <=============================================================>

	/*
	 * Boundary
	 */
	// <=============================================================>
	private void resetBoundary() {
		double minX = this.boundaryMap.get("minX");
		double maxX = this.boundaryMap.get("maxX");
		double minY = this.boundaryMap.get("minY");
		double maxY = this.boundaryMap.get("maxY");

		int column = new BigDecimal((maxX - minX) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int row = new BigDecimal((maxY - minY) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
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
		this.boundaryMap.put("topX",
				new BigDecimal(maxX - 0.5 * this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		this.boundaryMap.put("topY",
				new BigDecimal(maxY - 0.5 * this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	private void getMergeBoundary() {
		this.boundaryMap = new TreeMap<String, Double>();
		Map<String, Double> asciiBoundary = this.asciiList.get(0).getBoundary();
		double minX = asciiBoundary.get("minX");
		double minY = asciiBoundary.get("minY");
		double maxX = asciiBoundary.get("maxX");
		double maxY = asciiBoundary.get("maxY");

		for (int index = 1; index < this.asciiList.size(); index++) {
			Map<String, Double> temptBoundary = this.asciiList.get(index).getBoundary();
			double temptMinX = temptBoundary.get("minX");
			double temptMinY = temptBoundary.get("minY");
			double temptMaxX = temptBoundary.get("maxX");
			double temptMaxY = temptBoundary.get("maxY");

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
//<====================================================================>
}
