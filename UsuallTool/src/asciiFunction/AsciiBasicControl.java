package asciiFunction;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;

public class AsciiBasicControl {
	private String[][] asciiContent = null;
	private TreeMap<String, String> property;
	private String[][] asciiGrid;

	// <==============>
	// < constructor function>
	// <==============>
	public AsciiBasicControl(String[][] asciiContent) throws IOException {
		this.asciiContent = asciiContent;
		cutFirstColumn();
		this.property = this.getProperty();
		this.asciiGrid = this.getAsciiGrid();

	}

	public AsciiBasicControl(String fileAdd) throws IOException {
		this.asciiContent = new AtFileReader(fileAdd).getStr();
		cutFirstColumn();
		this.property = this.getProperty();
		this.asciiGrid = this.getAsciiGrid();
	}

	// <=========================>
	// < using while ascii file start by a space >
	// <=========================>
	private AsciiBasicControl cutFirstColumn() throws IOException {
		// function for the open file
		if (this.asciiContent[6][0].equals("")) {
			for (int row = 6; row < this.asciiContent.length; row++) {
				List<String> temptList = new ArrayList<String>(Arrays.asList(this.asciiContent[row]));
				temptList.remove(0);
				this.asciiContent[row] = temptList.parallelStream().toArray(String[]::new);
			}
		}
		return this;
	}

	// <==================>
	// < get the read asciiFile property>
	// <==================>
	public TreeMap<String, String> getProperty() {
		TreeMap<String, String> temptTree = new TreeMap<String, String>();
		double cellSize = new BigDecimal(this.asciiContent[4][1]).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		// set the xllCorner to xllCenter
		if (this.asciiContent[2][0].contains("corner")) {
			this.asciiContent[2][0] = "xllcenter";
			this.asciiContent[2][1] = (Double.parseDouble(this.asciiContent[2][1]) + cellSize * 0.5) + "";
		}
		if (this.asciiContent[3][0].contains("corner")) {
			this.asciiContent[3][0] = "yllcenter";
			this.asciiContent[3][1] = (Double.parseDouble(this.asciiContent[3][1]) + cellSize * 0.5) + "";
		}

		temptTree.put("column", this.asciiContent[0][1]);
		temptTree.put("row", this.asciiContent[1][1]);
		temptTree.put("bottomX", this.asciiContent[2][1]);
		temptTree.put("bottomY", this.asciiContent[3][1]);
		temptTree.put("cellSize", this.asciiContent[4][1]);
		temptTree.put("noData", this.asciiContent[5][1]);

		temptTree.put("topX",
				new BigDecimal(Double.parseDouble(this.asciiContent[2][1])
						+ cellSize * (Integer.parseInt(temptTree.get("column")) - 1))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());

		temptTree.put("topY",
				new BigDecimal(Double.parseDouble(this.asciiContent[3][1])
						+ cellSize * (Integer.parseInt(temptTree.get("row")) - 1))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());

		return temptTree;
	}

	public String[][] getPropertyText() {
		ArrayList<String[]> temptTree = new ArrayList<String[]>();

		temptTree.add(new String[] { "ncols", this.asciiContent[0][1] });
		temptTree.add(new String[] { "nrows", this.asciiContent[1][1] });
		temptTree.add(new String[] { "xllcenter", this.asciiContent[2][1] });
		temptTree.add(new String[] { "yllcenter", this.asciiContent[3][1] });
		temptTree.add(new String[] { "cellsize", this.asciiContent[4][1] });
		temptTree.add(new String[] { "NODATA_value", this.asciiContent[5][1] });

		return temptTree.parallelStream().toArray(String[][]::new);
	}

	// <===========================>
	// < get the value by giving location of ascii >
	// <===========================>
	public String getValue(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double startX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double startY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		try {
			return this.asciiGrid[row][column];
		} catch (Exception e) {
			return "error location";
		}
	}

	public String getValue(int column, int row) {
		try {
			return this.asciiGrid[row][column];
		} catch (Exception e) {
			return "error location";
		}
	}

	public AsciiBasicControl setValue(double x, double y, String value) {
		int[] position = this.getPosition(x, y);
		try {
			this.asciiContent[position[1] + 6][position[0]] = value;
			this.asciiGrid[position[1]][position[0]] = value;
		} catch (Exception e) {
		}
		return this;
	}

	public AsciiBasicControl setValue(int x, int y, String value) {
		this.asciiContent[y + 6][x] = value;
		this.asciiGrid[y][x] = value;
		return this;
	}

	// <=================================================>
	// < get the position by giving coordinate of ascii > < x , y ><column , row>
	// <=================================================>
	public int[] getPosition(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double startX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double startY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		return new int[] { column, row };
	}

	public double[] getCoordinate(int column, int row) {
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		double cellSize = Double.parseDouble(this.property.get("cellSize"));

		double x = new BigDecimal(startX + column * cellSize).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double y = new BigDecimal(startY - row * cellSize).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		return new double[] { x, y };
	}

	public double[] getClosestCoordinate(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double startX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double startY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();

		return new double[] { startX + (column + 0.5) * cellSize, startY - (row + 0.5) * cellSize };
	}

	// <===========================>
	// < get the ASCII GRID >
	// <===========================>

	// <getting the asciiContent>
	// <____________________________________________________________________________________________________________>
	public String[][] getAsciiFile() {
		return this.asciiContent;
	}

	// <get the asciiFIle that value in range>
	public String[][] getAsciiFile(double base, double top) {
		String noData = this.asciiContent[5][1];
		String[][] tempt = this.asciiContent;
		for (int line = 6; line < this.asciiContent.length; line++) {
			for (int column = 0; column < this.asciiContent[line].length; column++) {
				try {
					if (!this.asciiContent[line][column].equals(noData)) {
						double value = Double.parseDouble(this.asciiContent[line][column]);
						if (value < base || value > top) {
							tempt[line][column] = noData;
						}
					}
				} catch (Exception e) {
				}
			}
		}
		return tempt;
	}

	public String[][] getAsciiGrid() {
		ArrayList<String[]> temptArray = new ArrayList<String[]>(Arrays.asList(this.asciiContent));
		for (int i = 0; i < 6; i++) {
			temptArray.remove(0);
		}
		return temptArray.parallelStream().toArray(String[][]::new);
	}

	// <getting the asciiGrid by setting the coordinate>
	// <______________________________________________________________________________________________>
	public String[][] getClipAsciiFile(double minX, double minY, double maxX, double maxY) {
		ArrayList<String[]> asciiGrid = new ArrayList<String[]>();
		double cellSize = Double.parseDouble(property.get("cellSize"));

		int startLine = new BigDecimal((Double.parseDouble(property.get("topY")) - maxY) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_DOWN).intValue() + 6;

		int endLine = new BigDecimal((Double.parseDouble(property.get("topY")) - minY) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_DOWN).intValue() + 6;

		int startColumn = new BigDecimal((minX - Double.parseDouble(property.get("bottomX"))) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_DOWN).intValue();

		int endColumn = new BigDecimal((maxX - Double.parseDouble(property.get("bottomX"))) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_DOWN).intValue();

		int outRow = endLine - startLine;
		int outColumn = endColumn - startColumn;
		double[] outllCenter = this.getCoordinate(startColumn, endLine);
		String xllCenter = new BigDecimal(outllCenter[0]).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
		String yllCenter = new BigDecimal(outllCenter[1]).setScale(3, BigDecimal.ROUND_HALF_UP).toString();

		asciiGrid.add(new String[] { "ncols", outColumn + "" });
		asciiGrid.add(new String[] { "nrows", outRow + "" });
		asciiGrid.add(new String[] { "xllcenter", xllCenter + "" });
		asciiGrid.add(new String[] { "yllcenter", yllCenter + "" });
		asciiGrid.add(new String[] { "cellsize", property.get("cellSize") });
		asciiGrid.add(new String[] { "nodata_value", property.get("noData") });

		for (int line = startLine; line < endLine; line++) {
			ArrayList<String> temptLine = new ArrayList<String>();
			for (int column = startColumn; column < endColumn; column++) {
				temptLine.add(this.asciiContent[line][column]);
			}
			asciiGrid.add(temptLine.parallelStream().toArray(String[]::new));
		}

		return asciiGrid.parallelStream().toArray(String[][]::new);
	}

	// <get asciiGrid by setting the position>
	// <___________________________________________________________________________>
	public String[][] getClipAsciiFile(int minX, int minY, int maxX, int maxY) {
		ArrayList<String[]> asciiGrid = new ArrayList<String[]>();

		int startLine = minY;

		int endLine = maxY;

		int startColumn = minX;

		int endColumn = maxX;

		int outRow = endLine - startLine + 1;
		int outColumn = endColumn - startColumn + 1;
		double[] outllCenter = this.getCoordinate(startColumn, endLine);
		String xllCenter = new BigDecimal(outllCenter[0]).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
		String yllCenter = new BigDecimal(outllCenter[1]).setScale(3, BigDecimal.ROUND_HALF_UP).toString();

		asciiGrid.add(new String[] { "ncols", outColumn + "" });
		asciiGrid.add(new String[] { "nrows", outRow + "" });
		asciiGrid.add(new String[] { "xllcenter", xllCenter + "" });
		asciiGrid.add(new String[] { "yllcenter", yllCenter + "" });
		asciiGrid.add(new String[] { "cellsize", this.property.get("cellSize") });
		asciiGrid.add(new String[] { "nodata_value", this.property.get("noData") });

		for (int line = startLine; line <= endLine; line++) {
			ArrayList<String> temptLine = new ArrayList<String>();
			for (int column = startColumn; column <= endColumn; column++) {
				temptLine.add(getValue(column, line));
			}
			asciiGrid.add(temptLine.parallelStream().toArray(String[]::new));
		}
		return asciiGrid.parallelStream().toArray(String[][]::new);
	}

	// <============================================================================>
	// <get asciiGrid by setting the position => displace>
	// <___________________________________________________________________________>
	public String[][] getFillBoundary(double maxX, double minX, double minY, double maxY, double cellSize) {
		List<String[]> outList = new ArrayList<String[]>();
		int totalRow = new BigDecimal((maxY - minY) / cellSize + 0.001).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		int totalColumn = new BigDecimal((maxX - minX) / cellSize + 0.001).setScale(0, BigDecimal.ROUND_DOWN)
				.intValue();

		outList.add(new String[] { "ncols", totalRow + "" });
		outList.add(new String[] { "nrows", totalColumn + "" });
		outList.add(new String[] { "xllcenter", minX + (0.5 * cellSize) + "" });
		outList.add(new String[] { "yllcenter", minY + (0.5 * cellSize) + "" });
		outList.add(new String[] { "cellsize", cellSize + "" });
		outList.add(new String[] { "nodata_value", this.property.get("noData") });

		for (int row = 0; row < totalRow; row++) {
			List<String> rowContent = new ArrayList<String>();
			for (int column = 0; column < totalColumn; column++) {
				// get the position in the original ascii of the boundary
				int originalStartPoint[] = this.getPosition(
						new BigDecimal(minX + column * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(),
						new BigDecimal(maxY - row * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
				int originalEndPoint[] = this.getPosition(
						new BigDecimal(minX + (column + 1) * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP)
								.doubleValue(),
						new BigDecimal(maxY - (row + 1) * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP)
								.doubleValue());
				int totalTimes = (originalEndPoint[0] - originalStartPoint[0])
						* (originalEndPoint[1] - originalStartPoint[1]);
				int selectimes = 0;

				// get the selected boundary in path2D
				Path2D temptPath = new Path2D.Double();
				temptPath.moveTo(minX + column * cellSize, maxY - row * cellSize);
				temptPath.lineTo(minX + (column + 1) * cellSize, maxY - row * cellSize);
				temptPath.lineTo(minX + (column + 1) * cellSize, maxY - (row + 1) * cellSize);
				temptPath.lineTo(minX + column * cellSize, maxY - (row + 1) * cellSize);
				temptPath.closePath();

				// get the original ascii cell value in boundary
				List<Double> cellValueList = new ArrayList<Double>();
				for (int targetRow = originalStartPoint[1]; targetRow <= originalEndPoint[1]; targetRow++) {
					for (int targetColumn = originalStartPoint[0]; targetColumn <= originalEndPoint[0]; targetColumn++) {
						double cordinate[] = this.getCoordinate(targetColumn, targetRow);
						if (!this.asciiGrid[targetRow][targetColumn].equals(this.property.get("noData"))
								&& temptPath.contains(cordinate[0], cordinate[1])) {
							try {
								cellValueList.add(Double.parseDouble(this.asciiGrid[targetRow][targetColumn]));
								selectimes++;
							} catch (Exception e) {
							}
						}
					}
				}

				// get the mean value of the selected cell
				// if the selected times lower than half of total cells size
				// set the value to null
				if (selectimes > 0.5 * totalTimes) {
					rowContent.add(new AtCommonMath(cellValueList).getMean() + "");
				} else {
					rowContent.add(this.property.get("noData"));
				}
			}
			outList.add(rowContent.parallelStream().toArray(String[]::new));
		}
		return outList.parallelStream().toArray(String[][]::new);
	}
	
	//<get the boundary is inside or not>
	//<____________________________________________________________________________>
	public Boolean isContain(double maxX, double minX, double minY, double maxY) {
		double boundaryMaxX = Double.parseDouble(this.property.get("topX"));
		double boundaryMaxY = Double.parseDouble(this.property.get("topY"));
		double boundaryMinX = Double.parseDouble(this.property.get("bottomX"));
		double boundaryMinY = Double.parseDouble(this.property.get("bottomY"));
		
		if(boundaryMaxX < minX) {
			return false;
		}else if(boundaryMaxY < minY) {
			return false;
		}else if(boundaryMinX >maxX) {
			return false;
		}else if(boundaryMinY >maxY) {
			return false;
		}else {
			return true;
		}
	}

	// <=================>
	// < replace the noData value>
	// <=================>
	public AsciiBasicControl changeNoDataValue(String nan) {
		String noData = this.asciiContent[5][1];
		for (int line = 0; line < this.asciiContent.length; line++) {
			for (int column = 0; column < this.asciiContent[line].length; column++) {
				if (this.asciiContent[line][column].equals(noData)) {
					this.asciiContent[line][column] = nan;
				}
			}
		}
		return this;
	}

}
