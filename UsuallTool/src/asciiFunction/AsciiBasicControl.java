package asciiFunction;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtCommonMath.StaticsModel;

public class AsciiBasicControl implements Cloneable {
	private String[][] asciiContent = null;
	private Map<String, String> property;
	private Map<String, Double> boundary;

	public static String minCenterX = "bottomX";
	public static String maxCenterX = "topX";
	public static String minCenterY = "bottomY";
	public static String maxCenterY = "topY";

	public static String minCornerX = "minX";
	public static String maxCornerX = "maxX";
	public static String minCornerY = "minY";
	public static String maxCornerY = "maxY";

	public static String columnKey = "column";
	public static String rowKey = "row";
	public static String cellSizeKey = "cellSize";
	public static String nullValueKey = "noData";

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

	}

	public AsciiBasicControl(String fileAdd) throws IOException {
		this.asciiContent = new AtFileReader(fileAdd).getStr();
		cutFirstColumn();
		setProperty();
		setBoundary();
	}

	public AsciiBasicControl(AsciiBasicControl ascii) {
		this.asciiContent = Arrays.asList(ascii.getAsciiFile()).parallelStream().map(stringArray -> stringArray.clone())
				.collect(Collectors.toList()).parallelStream().toArray(String[][]::new);
		setProperty();
		setBoundary();
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
		double cellSize = Double.parseDouble(this.property.get(cellSizeKey));

		double minX = Double.parseDouble(this.property.get(minCenterX)) - 0.5 * cellSize;
		double maxX = Double.parseDouble(this.property.get(maxCenterX)) + 0.5 * cellSize;
		double minY = Double.parseDouble(this.property.get(minCenterY)) - 0.5 * cellSize;
		double maxY = Double.parseDouble(this.property.get(maxCenterY)) + 0.5 * cellSize;

		boundary.put(minCornerX, minX);
		boundary.put(maxCornerX, maxX);
		boundary.put(minCornerY, minY);
		boundary.put(maxCornerY, maxY);
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
		temptTree.add(new String[] { cellSizeKey, this.asciiContent[4][1] });
		temptTree.add(new String[] { "NODATA_value", this.asciiContent[5][1] });

		return temptTree.parallelStream().toArray(String[][]::new);
	}

	private void setProperty() {
		TreeMap<String, String> temptTree = new TreeMap<String, String>();
		double cellSize = new BigDecimal(this.asciiContent[4][1]).setScale(globalAscii.scale, RoundingMode.HALF_UP)
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

		temptTree.put(columnKey, this.asciiContent[0][1]);
		temptTree.put(rowKey, this.asciiContent[1][1]);
		temptTree.put(minCenterX, this.asciiContent[2][1]);
		temptTree.put(minCenterY, this.asciiContent[3][1]);
		temptTree.put(cellSizeKey, this.asciiContent[4][1]);
		temptTree.put(nullValueKey, this.asciiContent[5][1]);

		temptTree.put(maxCenterX,
				new BigDecimal(Double.parseDouble(this.asciiContent[2][1])
						+ cellSize * (Integer.parseInt(temptTree.get(columnKey)) - 1))
								.setScale(globalAscii.scale, RoundingMode.HALF_UP).toString());

		temptTree.put(maxCenterY,
				new BigDecimal(Double.parseDouble(this.asciiContent[3][1])
						+ cellSize * (Integer.parseInt(temptTree.get(rowKey)) - 1))
								.setScale(globalAscii.scale, RoundingMode.HALF_UP).toString());

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
		double cellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double startX = Double.parseDouble(this.property.get(minCenterX)) - 0.5 * cellSize;
		double startY = Double.parseDouble(this.property.get(maxCenterY)) + 0.5 * cellSize;

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_DOWN).intValue();
		try {
			return this.asciiContent[row + 6][column];
		} catch (Exception e) {
			return this.property.get(nullValueKey);
		}
	}

	public String getValue(int column, int row) {
		if (row <= 6) {
			return this.getNullValue();
		} else {
			try {
				return this.asciiContent[row + 6][column];
			} catch (Exception e) {
				return this.property.get(nullValueKey);
			}
		}
	}

	public String getNullValue() {
		return this.getProperty().get(nullValueKey);
	}

	public String getValue(Path2D path) {
		return getValue(path, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public String getValue(Path2D path, double minValue, double maxValue) {
		List<Double> valueList = getPolygonValueList(path, minValue, maxValue);

		/*
		 * if there isn't any grid center is inside the polygon
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

	public int getCount(Path2D path) {
		return getPolygonValueList(path).size();
	}

	public int getCount(Path2D path, double minValue, double maxValue) {
		return getPolygonValueList(path, minValue, maxValue).size();
	}

	public List<Double> getValueList(double x, double y, int gridCount) {
		int[] position = this.getPosition(x, y);
		return this.getValueList(position[0], position[1], gridCount);
	}

	public List<Double> getValueList(double x, double y, double distance) throws IOException {
		Geometry geo = GdalGlobal.CreatePoint(x, y).Buffer(distance);
		return this.getPolygonValueList(geo);
	}

	public List<Double> getValueList(int column, int row, double distance) throws IOException {
		double[] coordinate = this.getCoordinate(column, row);
		return this.getValueList(coordinate[0], coordinate[1], distance);
	}

	public List<Double> getValueList(int column, int row, int gridCount) {
		List<Double> outList = new ArrayList<>();

		for (int rowCount = -1 * gridCount; rowCount < gridCount; rowCount++) {
			for (int columnCount = -1 * gridCount; columnCount < gridCount; columnCount++) {
				String temptValue = this.getValue(columnCount + column, rowCount + row);

				if (!temptValue.equals(this.getNullValue())) {
					outList.add(Double.parseDouble(temptValue));
				}
			}
		}

		return outList;
	}

	public List<Double> getPolygonValueList(Path2D path) {
		return getPolygonValueList(path, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public List<Double> getPolygonValueList(Path2D path, double minValue, double maxValue) {
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
						double temptDoubleValue = Double.parseDouble(temptValue);
						if (temptDoubleValue >= minValue && temptDoubleValue < maxValue) {
							valueList.add(temptDoubleValue);
						}
					}
				}
			}
		}
		return valueList;
	}

	public List<Integer[]> getPolygonPositionList(Path2D path) {
		Rectangle pathBoundary = path.getBounds();
		double maxX = pathBoundary.getMaxX();
		double minX = pathBoundary.getMinX();
		double maxY = pathBoundary.getMaxY();
		double minY = pathBoundary.getMinY();

		/*
		 * get the mean value in the
		 */
		List<Integer[]> valueList = new ArrayList<Integer[]>();
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
						valueList.add(new Integer[] { column, row });
					}
				}
			}
		}
		return valueList;
	}

	public String getValue(Geometry geometry) throws IOException {
		return getValue(geometry, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public String getValue(Geometry geometry, double minValue, double maxValue) throws IOException {
		List<Double> polygonValueList = getPolygonValueList(geometry);

		try {
			return new BigDecimal(new AtCommonMath(polygonValueList).getMean()).setScale(3, RoundingMode.HALF_UP)
					.toString();
		} catch (Exception e) {
			return this.getNullValue();
		}
	}

	public int getCount(Geometry geometry) throws IOException {
		return this.getPolygonValueList(geometry).size();
	}

	public int getCount(Geometry geometry, double minValue, double maxValue) throws IOException {
		return this.getPolygonValueList(geometry, minValue, maxValue).size();
	}

	public List<Double> getPolygonValueList(Geometry geometry) throws IOException {
		return getPolygonValueList(geometry, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public List<Double> getPolygonValueList(Geometry geometry, double minValue, double maxValue) throws IOException {
		List<Double> polygonValueList = new ArrayList<Double>();
		List<Path2D> pathList = GdalGlobal.GeomertyToPath2D(geometry);

		for (int index = 0; index < pathList.size(); index++) {
			List<Double> temptValueList = this.getPolygonValueList(pathList.get(index), minValue, maxValue);
			temptValueList.forEach(e -> polygonValueList.add(e));
		}
		return polygonValueList;
	}

	public AsciiBasicControl setValue(double x, double y, String value) {
		int[] position = this.getPosition(x, y);
		try {
			this.asciiContent[position[1] + 6][position[0]] = value;
		} catch (Exception e) {
		}
		return this;
	}

	public AsciiBasicControl setValue(int x, int y, String value) {
		this.asciiContent[y + 6][x] = value;
		return this;
	}

	public AsciiBasicControl setValue(Path2D path, String value) {
		List<Integer[]> positions = this.getPolygonPositionList(path);
		positions.forEach(position -> {
			int column = position[0];
			int row = position[1];

			this.setValue(column, row, value);
		});
		return this;
	}

	public AsciiBasicControl setValue(Geometry geo, String value) throws IOException {
		GdalGlobal.GeomertyToPath2D(geo).forEach(path -> {
			this.setValue(path, value);
		});
		return this;
	}

	public Boolean isNull(double x, double y) {
		if (this.getValue(x, y).equals(this.getNullValue())) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isNull(int column, int row) {
		if (this.getValue(column, row).equals(this.getNullValue())) {
			return true;
		} else {
			return false;
		}
	}

	public AsciiBasicControl setStartPoint(String[] coordinate) {
		this.asciiContent[2][1] = coordinate[0];
		this.asciiContent[3][1] = coordinate[1];

		setProperty();
		setBoundary();
		return this;
	}

	public AsciiBasicControl setStartPoint(String[] coordinate, String cellSize) {
		this.asciiContent[2][1] = coordinate[0];
		this.asciiContent[3][1] = coordinate[1];
		this.asciiContent[4][1] = cellSize;

		setProperty();
		setBoundary();
		return this;
	}

	public double getCellSize() {
		return Double.parseDouble(this.property.get(cellSizeKey));
	}

	/*
	 * 
	 */
	// <=============================================>
	// < ===============Get Position Function===============>
	// <=============================================>

	// <==============================================>
	public int[] getPosition(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double startX = Double.parseDouble(this.property.get(minCenterX));
		double startY = Double.parseDouble(this.property.get(maxCenterY));

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, RoundingMode.HALF_UP).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, RoundingMode.HALF_UP).intValue();
		return new int[] { column, row };
	}

	public double[] getCoordinate(int column, int row) {
		double startX = Double.parseDouble(this.property.get(minCenterX));
		double startY = Double.parseDouble(this.property.get(maxCenterY));
		double cellSize = Double.parseDouble(this.property.get(cellSizeKey));

		double x = new BigDecimal(startX + column * cellSize).setScale(globalAscii.scale, RoundingMode.HALF_UP)
				.doubleValue();
		double y = new BigDecimal(startY - row * cellSize).setScale(globalAscii.scale, RoundingMode.HALF_UP)
				.doubleValue();

		return new double[] { x, y };
	}

	public double[] getClosestCoordinate(double x, double y) {
		double cellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double startX = Double.parseDouble(this.property.get(minCenterX)) - 0.5 * cellSize;
		double startY = Double.parseDouble(this.property.get(maxCenterY)) + 0.5 * cellSize;

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

	public int getRow() {
		return Integer.parseInt(this.property.get(rowKey));
	}

	public int getColumn() {
		return Integer.parseInt(this.property.get(columnKey));
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

	public AsciiBasicControl getClipAsciiFile(Path2D path) throws IOException {

		List<Double[]> outList = new ArrayList<>();
		Rectangle rec = path.getBounds();

		int[] bottomLeftPosition = this.getPosition(rec.getMinX(), rec.getMinY());
		int[] topRightPosition = this.getPosition(rec.getMaxX(), rec.getMaxY());

		String nullValue = this.getNullValue();
		for (int temptColumn = bottomLeftPosition[0]; temptColumn <= topRightPosition[0]; temptColumn++) {
			for (int temptRow = topRightPosition[1]; temptRow <= bottomLeftPosition[1]; temptRow++) {

				double coordinate[] = this.getCoordinate(temptColumn, temptRow);
				String temptValue = this.getValue(temptColumn, temptRow);
				if (!temptValue.equals(nullValue)) {
					if (path.contains(coordinate[0], coordinate[1])) {
						outList.add(new Double[] { coordinate[0], coordinate[1], Double.parseDouble(temptValue) });
					}
				}
			}
		}
		return new AsciiBasicControl(new XYZToAscii(outList).setCellSize(this.getCellSize()).start().getAsciiFile());
	}

	public AsciiBasicControl getClipAsciiFile(Map<String, Double> boundary) throws IOException {
		double minX = boundary.get(minCornerX);
		double maxX = boundary.get(maxCornerX);
		double minY = boundary.get(minCornerY);
		double maxY = boundary.get(maxCornerY);

		return getClipAsciiFile(minX, minY, maxX, maxY);
	}

	public AsciiBasicControl getClipAsciiFile(Rectangle rec) throws IOException {
		return this.getClipAsciiFile(rec.getMinX(), rec.getMinY(), rec.getMaxX(), rec.getMaxY());
	}

	public AsciiBasicControl getClipAsciiFile(double minX, double minY, double maxX, double maxY) throws IOException {
		ArrayList<String[]> asciiGrid = new ArrayList<String[]>();
		double cellSize = Double.parseDouble(property.get(cellSizeKey));

		minX = new BigDecimal(minX + 0.5 * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue();
		minY = new BigDecimal(minY + 0.5 * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue();
		maxX = new BigDecimal(maxX - 0.5 * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue();
		maxY = new BigDecimal(maxY - 0.5 * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue();

		int[] bottomPosition = this.getPosition(minX, minY);
		int[] topPosition = this.getPosition(maxX, maxY);
		double[] outllCenter = this.getCoordinate(bottomPosition[0], bottomPosition[1]);

		asciiGrid.add(new String[] { "ncols", topPosition[0] - bottomPosition[0] + 1 + "" });
		asciiGrid.add(new String[] { "nrows", -topPosition[1] + bottomPosition[1] + 1 + "" });
		asciiGrid.add(new String[] { "xllcenter", outllCenter[0] + "" });
		asciiGrid.add(new String[] { "yllcenter", outllCenter[1] + "" });
		asciiGrid.add(new String[] { cellSizeKey, property.get(cellSizeKey) });
		asciiGrid.add(new String[] { "nodata_value", property.get(nullValueKey) });

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
		outList.add(new String[] { cellSizeKey, cellSize + "" });
		outList.add(new String[] { "nodata_value", this.property.get(nullValueKey) });

		for (int row = 0; row < totalRow; row++) {
			List<String> rowContent = new ArrayList<String>();
			for (int column = 0; column < totalColumn; column++) {
				// get the position in the original ascii of the boundary
				int originalStartPoint[] = this.getPosition(
						new BigDecimal(minX + column * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue(),
						new BigDecimal(maxY - row * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue());
				int originalEndPoint[] = this.getPosition(
						new BigDecimal(minX + (column + 1) * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue(),
						new BigDecimal(maxY - (row + 1) * cellSize).setScale(3, RoundingMode.HALF_UP).doubleValue());
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
						if (!this.asciiContent[targetRow + 6][targetColumn].equals(this.property.get(nullValueKey))
								&& temptPath.contains(cordinate[0], cordinate[1])) {
							try {
								cellValueList.add(Double.parseDouble(this.asciiContent[targetRow + 6][targetColumn]));
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
					rowContent.add(this.property.get(nullValueKey));
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
		double cellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double boundaryMaxX = new BigDecimal(Double.parseDouble(this.property.get(maxCenterX)) + cellSize * 0.5)
				.setScale(3, RoundingMode.HALF_UP).doubleValue();
		double boundaryMaxY = new BigDecimal(Double.parseDouble(this.property.get(maxCenterY)) + cellSize * 0.5)
				.setScale(3, RoundingMode.HALF_UP).doubleValue();
		double boundaryMinX = new BigDecimal(Double.parseDouble(this.property.get(minCenterX)) - cellSize * 0.5)
				.setScale(3, RoundingMode.HALF_UP).doubleValue();
		double boundaryMinY = new BigDecimal(Double.parseDouble(this.property.get(minCenterY)) - cellSize * 0.5)
				.setScale(3, RoundingMode.HALF_UP).doubleValue();
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

	public AsciiBasicControl getIntersectAscii(double minX, double maxX, double minY, double maxY) throws IOException {
		return this.getClipAsciiFile(this.intersectBoundary(minX, maxX, minY, maxY));
	}

	public AsciiBasicControl getIntersectAscii(AsciiBasicControl boundaryAscii) throws IOException {
		return this.getClipAsciiFile(this.intersectBoundary(boundaryAscii.getBoundary()));
	}

	public AsciiBasicControl getIntersectAscii(Map<String, Double> boundary) throws IOException {
		return this.getClipAsciiFile(this.intersectBoundary(boundary));
	}

	/*
	 * 
	 */
	// <===========================>
	// < get the asciiFile which split by given line>
	// <===========================>
	public Boolean isIntersect(double xCoefficient, double yCoefficient, double intersectCoefficient) {
		Map<String, Double> boundary = this.getBoundary();

		double crossTopX = -1 * (yCoefficient * boundary.get(maxCornerY) + intersectCoefficient) / xCoefficient;
		double crossBottomX = -1 * (yCoefficient * boundary.get(minCornerY) + intersectCoefficient) / xCoefficient;
		double crossRightY = -1 * (xCoefficient * boundary.get(maxCornerX) + intersectCoefficient) / yCoefficient;
		double crossLeftY = -1 * (xCoefficient * boundary.get(maxCornerX) + intersectCoefficient) / yCoefficient;

		if (crossTopX <= boundary.get(maxCornerX) && crossTopX >= boundary.get(minCornerX)) {
			return true;
		} else if (crossBottomX <= boundary.get(maxCornerX) && crossBottomX >= boundary.get(minCornerX)) {
			return true;
		} else if (crossRightY <= boundary.get(maxCornerY) && crossRightY >= boundary.get(minCornerY)) {
			return true;
		} else if (crossLeftY <= boundary.get(maxCornerY) && crossLeftY >= boundary.get(minCornerY)) {
			return true;
		} else {
			return false;
		}
	}

	public List<Map<String, Double>> getIntersectSideBoundary(double xCoefficient, double yCoefficient,
			double intersectCoefficient) {
		List<Map<String, Double>> outBoundary = new ArrayList<>();

		/*
		 * get bounday
		 */
		Map<String, Double> boundary = this.getBoundary();
		Path2D temptPath = new Path2D.Double();
		temptPath.moveTo(boundary.get(minCornerX), boundary.get(maxCornerY));
		temptPath.lineTo(boundary.get(minCornerX), boundary.get(minCornerY));
		temptPath.lineTo(boundary.get(maxCornerX), boundary.get(minCornerY));
		temptPath.lineTo(boundary.get(maxCornerX), boundary.get(maxCornerY));
		Geometry temptGeo = GdalGlobal.Path2DToGeometry(temptPath);

		/*
		 * get cross line
		 */
		double interMaxX = Double.POSITIVE_INFINITY / 100000;
		double interMaxY = (-1 * intersectCoefficient - interMaxX * xCoefficient) / yCoefficient;
		double interMinX = Double.NEGATIVE_INFINITY / 100000;
		double interMinY = (-1 * intersectCoefficient - interMinX * xCoefficient) / yCoefficient;
		Geometry interLine = GdalGlobal.CreateLine(interMaxX, interMaxY, interMinX, interMinY);
		Geometry crossPoints = temptGeo.Intersection(interLine);

		/*
		 * get differ side
		 */
		List<List<Double[]>> differSidePoints = new ArrayList<>();
		List<Double[]> positivePoints = new ArrayList<>();
		List<Double[]> negetivePoints = new ArrayList<>();

		for (int pointIndex = 0; pointIndex < crossPoints.GetGeometryCount(); pointIndex++) {
			positivePoints.add(new Double[] { crossPoints.GetX(pointIndex), crossPoints.GetY(pointIndex) });
			negetivePoints.add(new Double[] { crossPoints.GetX(pointIndex), crossPoints.GetY(pointIndex) });
		}

		// maxX maxY
		if ((boundary.get(maxCornerX) * xCoefficient + boundary.get(maxCornerY) * yCoefficient
				+ intersectCoefficient) > 0) {
			positivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(maxCornerY) });
		} else if ((boundary.get(maxCornerX) * xCoefficient + boundary.get(maxCornerY) * yCoefficient
				+ intersectCoefficient) < 0) {
			negetivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(maxCornerY) });
		} else {
			positivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(maxCornerY) });
			negetivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(maxCornerY) });
		}

		// maxX minY
		if ((boundary.get(maxCornerX) * xCoefficient + boundary.get(minCornerY) * yCoefficient
				+ intersectCoefficient) > 0) {
			positivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(minCornerY) });
		} else if ((boundary.get(maxCornerX) * xCoefficient + boundary.get(minCornerY) * yCoefficient
				+ intersectCoefficient) < 0) {
			negetivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(minCornerY) });
		} else {
			positivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(minCornerY) });
			negetivePoints.add(new Double[] { boundary.get(maxCornerX), boundary.get(minCornerY) });
		}

		// minX minY
		if ((boundary.get(minCornerX) * xCoefficient + boundary.get(minCornerY) * yCoefficient
				+ intersectCoefficient) > 0) {
			positivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(minCornerY) });
		} else if ((boundary.get(minCornerX) * xCoefficient + boundary.get(minCornerY) * yCoefficient
				+ intersectCoefficient) < 0) {
			negetivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(minCornerY) });
		} else {
			positivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(minCornerY) });
			negetivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(minCornerY) });
		}

		// minX maxY
		if ((boundary.get(minCornerX) * xCoefficient + boundary.get(maxCornerY) * yCoefficient
				+ intersectCoefficient) > 0) {
			positivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(maxCornerY) });
		} else if ((boundary.get(minCornerX) * xCoefficient + boundary.get(maxCornerY) * yCoefficient
				+ intersectCoefficient) < 0) {
			negetivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(maxCornerY) });
		} else {
			positivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(maxCornerY) });
			negetivePoints.add(new Double[] { boundary.get(minCornerX), boundary.get(maxCornerY) });
		}

		/*
		 * get new boundary
		 */
		for (int index = 0; index < differSidePoints.size(); index++) {
			List<Double> temptXList = new ArrayList<Double>();
			List<Double> temptYList = new ArrayList<Double>();

			differSidePoints.get(index).forEach(e -> {
				temptXList.add(e[0]);
				temptYList.add(e[1]);
			});

			AtCommonMath xStatics = new AtCommonMath(temptXList);
			AtCommonMath yStatics = new AtCommonMath(temptYList);
			double minX = xStatics.getMin();
			double maxX = xStatics.getMax();
			double minY = yStatics.getMin();
			double maxY = yStatics.getMax();

			Map<String, Double> temptBoundary = new TreeMap<>();
			temptBoundary.put(maxCornerX, maxX);
			temptBoundary.put(minCornerX, minX);
			temptBoundary.put(minCornerY, minY);
			temptBoundary.put(maxCornerY, maxY);

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
		double tmeptCellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double temptMinX = Double.parseDouble(this.property.get(minCenterX)) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get(maxCenterX)) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get(minCenterY)) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get(maxCenterY)) + 0.5 * tmeptCellSize;

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
		double cellSize = Double.parseDouble(temptAscii.getProperty().get(cellSizeKey));
		double minX = Double.parseDouble(temptAscii.getProperty().get(minCenterX)) - 0.5 * cellSize;
		double maxX = Double.parseDouble(temptAscii.getProperty().get(maxCenterX)) + 0.5 * cellSize;
		double minY = Double.parseDouble(temptAscii.getProperty().get(minCenterY)) - 0.5 * cellSize;
		double maxY = Double.parseDouble(temptAscii.getProperty().get(maxCenterY)) + 0.5 * cellSize;

		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double temptMinX = Double.parseDouble(this.property.get(minCenterX)) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get(maxCenterX)) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get(minCenterY)) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get(maxCenterY)) + 0.5 * tmeptCellSize;

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
		double minX = boundary.get(minCornerX);
		double maxX = boundary.get(maxCornerX);
		double minY = boundary.get(minCornerY);
		double maxY = boundary.get(maxCornerY);

		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get(cellSizeKey));
		double temptMinX = Double.parseDouble(this.property.get(minCenterX)) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get(maxCenterX)) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get(minCenterY)) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get(maxCenterY)) + 0.5 * tmeptCellSize;

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
		return intersectBoundary(boundary.get(minCornerX), boundary.get(maxCornerX), boundary.get(minCornerY),
				boundary.get(maxCornerY));
	}

	private Map<String, Double> intersectBoundary(double minX, double maxX, double minY, double maxY) {
		Map<String, Double> boundary = new TreeMap<>();
		if (this.boundary.get(minCornerX) > minX) {
			boundary.put(minCornerX, this.boundary.get(minCornerX));
		} else {
			boundary.put(minCornerX, minX);
		}

		if (this.boundary.get(minCornerY) > minY) {
			boundary.put(minCornerY, this.boundary.get(minCornerY));
		} else {
			boundary.put(minCornerY, minY);
		}

		if (this.boundary.get(maxCornerX) < maxX) {
			boundary.put(maxCornerX, this.boundary.get(maxCornerX));
		} else {
			boundary.put(maxCornerX, maxX);
		}

		if (this.boundary.get(maxCornerY) < maxY) {
			boundary.put(maxCornerY, this.boundary.get(maxCornerY));
		} else {
			boundary.put(maxCornerY, maxY);
		}

		return boundary;
	}
	// <==================================================================>

	/*
	 * Converting function
	 */
	public List<Double[]> converToXYZ() {
		List<Double[]> outList = new ArrayList<>();

		for (int row = 0; row < Integer.parseInt(this.property.get(rowKey)); row++) {
			for (int column = 0; column < Integer.parseInt(this.property.get(columnKey)); column++) {
				String temptValue = this.getValue(column, row);
				if (!temptValue.equals(this.getNullValue())) {
					double[] coordinate = this.getCoordinate(column, row);
					outList.add(new Double[] { coordinate[0], coordinate[1], Double.parseDouble(temptValue) });
				}
			}
		}
		return outList;
	}

	@Override
	public AsciiBasicControl clone() {
		return new AsciiBasicControl(this);
	}

	public static AsciiBasicControl getMaxAscii(List<AsciiBasicControl> asciiList) throws Exception {
		AsciiBasicControl outAscii = asciiList.get(0).clone();

		for (int row = 0; row < outAscii.getRow(); row++) {
			for (int column = 0; column < outAscii.getColumn(); column++) {
				String temptValue = outAscii.getValue(column, row);

				if (!temptValue.equals(outAscii.getNullValue())) {
					List<Double> temptValueList = new ArrayList<>();

					for (AsciiBasicControl ascii : asciiList) {
						temptValueList.add(Double.parseDouble(ascii.getValue(column, row)));
					}

					outAscii.setValue(column, row,
							AtCommonMath.getListStatistic(temptValueList, StaticsModel.getMax) + "");
				}
			}
		}

		return outAscii;
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
