package asciiFunction;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.path.IntersectLine;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;

public class AsciiBasicControl {
	private String[][] asciiContent = null;
	private Map<String, String> property;
	private Map<String, Double> boundary;
	private String[][] asciiGrid;

	/*
	 * 
	 */
	// <=============================================>
	// < =================Constructor Function==============>
	// <=============================================>

	// <________________________________________________________________________>
	public AsciiBasicControl(String[][] asciiContent) throws IOException {
		this.asciiContent = asciiContent;
		cutFirstColumn();
		setProperty();
		setBoundary();
		this.asciiGrid = this.getAsciiGrid();

	}

	public AsciiBasicControl(String fileAdd) throws IOException {
		this.asciiContent = new AtFileReader(fileAdd).getStr();
		cutFirstColumn();
		setProperty();
		setBoundary();
		this.asciiGrid = this.getAsciiGrid();
	}

	// < using while ascii file start by a space >
	// <________________________________________________________________________>
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
	// <=============================================>

	/*
	 * 
	 */
	// <=============================================>
	// < ==================Boundary Function==============>
	// <=============================================>

	// <________________________________________________________________________>
	public Map<String, Double> getBoundary() {
		return this.boundary;
	}

	private void setBoundary() {
		Map<String, Double> boundary = new TreeMap<String, Double>();
		double cellSize = Double.parseDouble(this.property.get("cellSize"));

		double minX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double maxX = Double.parseDouble(this.property.get("topX")) + 0.5 * cellSize;
		double minY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * cellSize;
		double maxY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;

		boundary.put("minX", minX);
		boundary.put("maxX", maxX);
		boundary.put("minY", minY);
		boundary.put("maxY", maxY);
		this.boundary = boundary;
	}
	// <=============================================>

	/*
	 * 
	 */
	// <=============================================>
	// < ===================Property Function==============>
	// <=============================================>

	// <________________________________________________________________________>
	public Map<String, String> getProperty() {
		return this.property;
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

	private void setProperty() {
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

		this.property = temptTree;
	}
	// <=================================================>

	/*
	 * 
	 */
	// <=============================================>
	// < ==================Value Function=================>
	// <=============================================>

	// <________________________________________________________________________>
	public String getValue(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double startX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double startY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		try {
			return this.asciiGrid[row][column];
		} catch (Exception e) {
			return this.property.get("noData");
		}
	}

	public String getValue(int column, int row) {
		try {
			return this.asciiGrid[row][column];
		} catch (Exception e) {
			return this.property.get("noData");
		}
	}

	public String getNullValue() {
		return this.getProperty().get("noData");
	}

	public String getValue(Path2D path) {
		Rectangle pathBoundary = path.getBounds();
		double maxX = pathBoundary.getMaxX();
		double minX = pathBoundary.getMinX();
		double maxY = pathBoundary.getMaxY();
		double minY = pathBoundary.getMinY();

		/*
		 * get the mean value in the
		 */
		List<Double> valueList = new ArrayList<Double>();
		String nullValue = this.getNullValue();
		int[] startPosition = this.getPosition(minX, maxY);
		int[] endPosition = this.getPosition(maxX, minY);
		for (int row = startPosition[1]; row <= endPosition[1]; row++) {
			for (int column = startPosition[0]; column <= endPosition[0]; column++) {
				String temptValue = this.getValue(column, row);
				if (!temptValue.equals(nullValue)) {

					// if the grid center is inside the polygon
					double[] temptCoordinate = this.getCoordinate(column, row);
					if (path.contains(temptCoordinate[0], temptCoordinate[1])) {
						valueList.add(Double.parseDouble(temptValue));
					}
				}
			}
		}

		/*
		 * if there isn't any grid center is inside the polygo
		 */
		if (valueList.size() > 0) {
			return String.valueOf(new AtCommonMath(valueList).getMean());
		} else {
			PathIterator pathIt = path.getPathIterator(null);
			float[] temptCoordinate = new float[6];
			List<Double> xList = new ArrayList<Double>();
			List<Double> yList = new ArrayList<Double>();

			for (; !pathIt.isDone(); pathIt.next()) {
				pathIt.currentSegment(temptCoordinate);
				xList.add((double) temptCoordinate[0]);
				yList.add((double) temptCoordinate[1]);
			}
			return this.getValue(new AtCommonMath(xList).getMean(), new AtCommonMath(yList).getMean());
		}
	}

	public String getValue(Geometry geometry) {
		List<Double> polygonValueList = new ArrayList<Double>();
		List<Path2D> pathList = GdalGlobal.GeomertyToPath2D(geometry);

		for (int index = 0; index < pathList.size(); index++) {
			String temptValue = getValue(pathList.get(index));
			if (!temptValue.equals(this.getNullValue())) {
				polygonValueList.add(Double.parseDouble(temptValue));
			}
		}

		try {
			return new BigDecimal(new AtCommonMath(polygonValueList).getMean()).setScale(3, BigDecimal.ROUND_HALF_UP)
					.toString();
		} catch (Exception e) {
			return this.getNullValue();
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

	public double getCellSize() {
		return Double.parseDouble(this.property.get("cellSize"));
	}

	/*
	 * 
	 */
	// <=============================================>
	// < ===============Get Position Function===============>
	// <=============================================>

	// <==============================================>
	public int[] getPosition(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
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

	/*
	 * 
	 */
	// <=============================================>
	// < ===============Get Ascii Function==================>
	// <=============================================>

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

	/*
	 * 
	 */
	// <=============================================>
	// < =================Clip Function===================>
	// <=============================================>

	// <getting the asciiGrid by setting the coordinate>
	// <______________________________________________________________________________________________>
	public AsciiBasicControl getClipAsciiFile(AsciiBasicControl ascii) throws IOException {
		return getClipAsciiFile(ascii.getBoundary());
	}

	public AsciiBasicControl getClipAsciiFile(Map<String, Double> boundary) throws IOException {
		double minX = boundary.get("minX");
		double maxX = boundary.get("maxX");
		double minY = boundary.get("minY");
		double maxY = boundary.get("maxY");

		return getClipAsciiFile(minX, minY, maxX, maxY);
	}

	public AsciiBasicControl getClipAsciiFile(double minX, double minY, double maxX, double maxY) throws IOException {
		ArrayList<String[]> asciiGrid = new ArrayList<String[]>();
		double cellSize = Double.parseDouble(property.get("cellSize"));

		minX = new BigDecimal(minX + 0.5 * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		minY = new BigDecimal(minY + 0.5 * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		maxX = new BigDecimal(maxX - 0.5 * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		maxY = new BigDecimal(maxY - 0.5 * cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

		int[] bottomPosition = this.getPosition(minX, minY);
		int[] topPosition = this.getPosition(maxX, maxY);
		double[] outllCenter = this.getCoordinate(bottomPosition[0], bottomPosition[1]);

		asciiGrid.add(new String[] { "ncols", topPosition[0] - bottomPosition[0] + 1 + "" });
		asciiGrid.add(new String[] { "nrows", -topPosition[1] + bottomPosition[1] + 1 + "" });
		asciiGrid.add(new String[] { "xllcenter", outllCenter[0] + "" });
		asciiGrid.add(new String[] { "yllcenter", outllCenter[1] + "" });
		asciiGrid.add(new String[] { "cellsize", property.get("cellSize") });
		asciiGrid.add(new String[] { "nodata_value", property.get("noData") });

		for (int line = topPosition[1]; line <= bottomPosition[1]; line++) {
			ArrayList<String> temptLine = new ArrayList<String>();
			for (int column = bottomPosition[0]; column <= topPosition[0]; column++) {
				double coordinate[] = this.getCoordinate(column, line);
				temptLine.add(this.getValue(coordinate[0], coordinate[1]));
			}
			asciiGrid.add(temptLine.parallelStream().toArray(String[]::new));
		}

		return new AsciiBasicControl(asciiGrid.parallelStream().toArray(String[][]::new));
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

	/*
	 * 
	 */
	// <=============================================>
	// < ==============Contain Function===================>
	// <=============================================>

	// <get the boundary is inside or not>
	// <____________________________________________________________________________>
	public Boolean isContain(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double boundaryMaxX = new BigDecimal(Double.parseDouble(this.property.get("topX")) + cellSize * 0.5)
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		double boundaryMaxY = new BigDecimal(Double.parseDouble(this.property.get("topY")) + cellSize * 0.5)
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		double boundaryMinX = new BigDecimal(Double.parseDouble(this.property.get("bottomX")) - cellSize * 0.5)
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		double boundaryMinY = new BigDecimal(Double.parseDouble(this.property.get("bottomY")) - cellSize * 0.5)
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (x <= boundaryMaxX && x >= boundaryMinX && y <= boundaryMaxY && y >= boundaryMinY) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 
	 */
	// <=============================================>
	// < ==============Intersect Function===================>
	// <=============================================>

	/*
	 * 
	 */
	// <===========================>
	// < get the asciiFile which split by given line>
	// <===========================>
	public Boolean isIntersect(double xCoefficient, double yCoefficient, double intersectCoefficient) {
		Map<String, Double> boundary = this.getBoundary();

		double crossTopX = -1 * (yCoefficient * boundary.get("maxY") + intersectCoefficient) / xCoefficient;
		double crossBottomX = -1 * (yCoefficient * boundary.get("minY") + intersectCoefficient) / xCoefficient;
		double crossRightY = -1 * (xCoefficient * boundary.get("maxX") + intersectCoefficient) / yCoefficient;
		double crossLeftY = -1 * (xCoefficient * boundary.get("maxX") + intersectCoefficient) / yCoefficient;

		if (crossTopX <= boundary.get("maxX") && crossTopX >= boundary.get("minX")) {
			return true;
		} else if (crossBottomX <= boundary.get("maxX") && crossBottomX >= boundary.get("minX")) {
			return true;
		} else if (crossRightY <= boundary.get("maxY") && crossRightY >= boundary.get("minY")) {
			return true;
		} else if (crossLeftY <= boundary.get("maxY") && crossLeftY >= boundary.get("minY")) {
			return true;
		} else {
			return false;
		}
	}

	public List<Map<String, Double>> getIntersectSideBoundary(double xCoefficient, double yCoefficient,
			double intersectCoefficient) {
		List<Map<String, Double>> outBoundary = new ArrayList<>();

		Map<String, Double> boundary = this.getBoundary();
		Path2D temptPath = new Path2D.Double();
		temptPath.moveTo(boundary.get("minX"), boundary.get("maxY"));
		temptPath.lineTo(boundary.get("minX"), boundary.get("minY"));
		temptPath.lineTo(boundary.get("maxX"), boundary.get("minY"));
		temptPath.lineTo(boundary.get("maxX"), boundary.get("maxY"));

		List<List<Double[]>> sidePoints = new IntersectLine(temptPath).getSidePoints(xCoefficient, yCoefficient,
				intersectCoefficient);

		for (int index = 0; index < sidePoints.size(); index++) {
			List<Double> temptXList = new ArrayList<Double>();
			List<Double> temptYList = new ArrayList<Double>();

			List<Double[]> temptPoints = sidePoints.get(index);
			temptPoints.forEach(point -> {
				temptXList.add(point[0]);
				temptYList.add(point[1]);
			});
			AtCommonMath xStatics = new AtCommonMath(temptXList);
			AtCommonMath yStatics = new AtCommonMath(temptYList);
			double minX = xStatics.getMin();
			double maxX = xStatics.getMax();
			double minY = yStatics.getMin();
			double maxY = yStatics.getMax();

			Map<String, Double> temptBoundary = new TreeMap<>();
			temptBoundary.put("maxX", maxX);
			temptBoundary.put("minX", minX);
			temptBoundary.put("minY", minY);
			temptBoundary.put("maxY", maxY);

			outBoundary.add(temptBoundary);
		}

		return outBoundary;
	}

	public List<AsciiBasicControl> getIntersectSideAscii(double xCoefficient, double yCoefficient,
			double intersectCoefficient) throws IOException {

		List<AsciiBasicControl> outAsciiList = new ArrayList<>();
		for (Map<String, Double> temptBoundary : getIntersectSideBoundary(xCoefficient, yCoefficient,
				intersectCoefficient)) {
			outAsciiList.add(this.getClipAsciiFile(temptBoundary));
		}
		return outAsciiList;
	}

	// <=======================================================================>

	/*
	 * 
	 */
	// <=========================>
	// < get the intersect by giving boundary >
	// <=========================>
	public Boolean isIntersect(double minX, double maxX, double minY, double maxY) {
		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * tmeptCellSize;

		if (temptMinX < maxX && temptMaxX > minX) {
			if (minY < temptMaxY && maxY > temptMinY) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Double> getIntersectBoundary(double minX, double maxX, double minY, double maxY) {
		return intersectBoundary(minX, maxX, minY, maxY);
	}
	// <=======================================================================>

	/*
	 * 
	 */
	// <=======================>
	// < get the intersect by giving asciiFile>
	// <=======================>
	public Boolean isIntersect(AsciiBasicControl temptAscii) {
		double cellSize = Double.parseDouble(temptAscii.getProperty().get("cellSize"));
		double minX = Double.parseDouble(temptAscii.getProperty().get("bottomX")) - 0.5 * cellSize;
		double maxX = Double.parseDouble(temptAscii.getProperty().get("topX")) + 0.5 * cellSize;
		double minY = Double.parseDouble(temptAscii.getProperty().get("bottomY")) - 0.5 * cellSize;
		double maxY = Double.parseDouble(temptAscii.getProperty().get("topY")) + 0.5 * cellSize;

		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * tmeptCellSize;

		if (temptMinX < maxX && temptMaxX > minX) {
			if (minY < temptMaxY && maxY > temptMinY) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Double> getIntersectBoundary(AsciiBasicControl boundaryAscii) {
		return intersectBoundary(boundaryAscii.getBoundary());
	}

	// <=================================================================>

	/*
	 * 
	 */
	// <============================>
	// < get the intersect by giving boundary map>
	// <============================>
	public Boolean isIntersect(Map<String, Double> boundary) {
		double minX = boundary.get("minX");
		double maxX = boundary.get("maxX");
		double minY = boundary.get("minY");
		double maxY = boundary.get("maxY");

		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * tmeptCellSize;

		if (temptMinX < maxX && temptMaxX > minX) {
			if (minY < temptMaxY && maxY > temptMinY) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Double> getIntersectBoundary(Map<String, Double> boundary) {
		return intersectBoundary(boundary);
	}
	// <=============================================================>

	/*
	 * Intersect Boundary private function
	 */
	// <===========================================================>
	private Map<String, Double> intersectBoundary(Map<String, Double> boundary) {
		return intersectBoundary(boundary.get("minX"), boundary.get("maxX"), boundary.get("minY"),
				boundary.get("maxY"));
	}

	private Map<String, Double> intersectBoundary(double minX, double maxX, double minY, double maxY) {
		Map<String, Double> boundary = new TreeMap<>();
		if (this.boundary.get("minX") > minX) {
			boundary.put("minX", this.boundary.get("minX"));
		} else {
			boundary.put("minX", minX);
		}

		if (this.boundary.get("minY") > minY) {
			boundary.put("minY", this.boundary.get("minY"));
		} else {
			boundary.put("minY", minY);
		}

		if (this.boundary.get("maxX") < maxX) {
			boundary.put("maxX", this.boundary.get("maxX"));
		} else {
			boundary.put("maxX", maxX);
		}

		if (this.boundary.get("maxY") < maxY) {
			boundary.put("maxY", this.boundary.get("maxY"));
		} else {
			boundary.put("maxY", maxY);
		}

		return boundary;
	}
	// <==================================================================>

	/*
	 * Converting function
	 */
	public List<Double[]> converToXYZ() {
		List<Double[]> outList = new ArrayList<>();

		for (int row = 0; row < Integer.parseInt(this.property.get("row")); row++) {
			for (int column = 0; column < Integer.parseInt(this.property.get("column")); column++) {
				String temptValue = this.getValue(column, row);
				if (!temptValue.equals(this.getNullValue())) {
					double[] coordinate = this.getCoordinate(column, row);
					outList.add(new Double[] { coordinate[0], coordinate[1], Double.parseDouble(temptValue) });
				}
			}
		}
		return outList;
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
