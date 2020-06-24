package geo.gdal;

import java.util.ArrayList;
import java.util.List;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;

import usualTool.AtCommonMath;

public class RasterReader {
	private String fileAdd;
	private double noDataValue;
	private int dataDecimal = 4;

	// gdal raster object
	private Dataset rasterData;
	private Band rasterBand;

	// raster properties
	private double minX; // value of center cell
	private double maxX; // value of center cell
	private double minY; // value of center cell
	private double maxY; // value of center cell

	private double ySize;
	private double xSize;

	// start from left top point
	private int row; // start from west , W -> E
	private int column; // start from north, N -> S

	// only for modified
	List<Double[]> valueList = new ArrayList<>();

	public RasterReader(String fileAdd) {
		this.fileAdd = fileAdd;

		// initial raster file
		gdal.AllRegister();
		this.rasterData = gdal.Open(this.fileAdd);
		this.rasterBand = this.rasterData.GetRasterBand(1);

		// get noData value
		Double[] nullVlaue = new Double[1];
		this.rasterBand.GetNoDataValue(nullVlaue);
		this.noDataValue = nullVlaue[0];

		// get raster properties
		double[] dataProperies = this.rasterData.GetGeoTransform();
		this.minX = AtCommonMath.getDecimal_Double(dataProperies[0], this.dataDecimal);
		this.maxY = AtCommonMath.getDecimal_Double(dataProperies[3], this.dataDecimal);

		this.xSize = AtCommonMath.getDecimal_Double(dataProperies[1], this.dataDecimal);
		this.ySize = AtCommonMath.getDecimal_Double(dataProperies[4], this.dataDecimal);

		this.column = this.rasterBand.getXSize();
		this.row = this.rasterBand.getYSize();

		this.maxX = AtCommonMath.getDecimal_Double(this.minX + (this.column - 1) * this.xSize, this.dataDecimal);
		this.minY = AtCommonMath.getDecimal_Double(this.maxY + (this.row - 1) * this.ySize, this.dataDecimal);
	}

	// <+++++++++++++++++++++++++++++++++++++>
	// <++++++++++ Read Raster file ++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++>

	// start from left top
	public double getValue(int row, int column) {
		double[] value = new double[1];
		if (row < 0 || column < 0) {
			return this.noDataValue;

		} else if (this.valueList.isEmpty()) {
			this.rasterBand.ReadRaster(column, row, 1, 1, value);
			return value[0];

		} else {
			return this.valueList.get(row)[column];
		}
	}

	public double getValue(double x, double y) {
		int[] position = getPosition(x, y);
		int row = position[0];
		int column = position[1];
		return getValue(row, column);
	}

	public List<Double[]> getValues(int row, int column, int xCount, int yCount) {
		List<Double[]> outList = new ArrayList<>();

		if (this.valueList.isEmpty()) {

			// read from raster
			double[] valueArray = new double[xCount * yCount];
			this.rasterBand.ReadRaster(row, column, xCount, yCount, valueArray);

			// translate to list
			for (int outRow = 0; outRow < yCount; outRow++) {

				List<Double> temptList = new ArrayList<>();
				for (int outColumn = 0; outColumn < xCount; outColumn++) {
					int index = outRow * outColumn + outColumn;
					temptList.add(valueArray[index]);
				}
				outList.add(temptList.parallelStream().toArray(Double[]::new));
			}

			// read from vlaueList
		} else {
			for (int outRow = 0; outRow < yCount; outRow++) {
				List<Double> temptList = new ArrayList<>();
				for (int outColumn = 0; outColumn < xCount; outColumn++) {
					temptList.add(this.valueList.get(row)[outColumn]);
				}
				outList.add(temptList.parallelStream().toArray(Double[]::new));
			}
		}

		return outList;
	}

	public List<Double[]> getValues(double x, double y, int xCount, int yCount) {
		int[] position = getPosition(x, y);
		int row = position[0];
		int column = position[1];
		return getValues(row, column, xCount, yCount);
	}

	public List<Double[]> getVlaues(double minX, double minY, double maxX, double maxY) {
		int[] position = getPosition(minX, maxY);
		int row = position[0];
		int column = position[1];
		int xCount = AtCommonMath.getDecimal_Int((maxX - minX) / this.xSize, 1);
		int yCount = AtCommonMath.getDecimal_Int((minY - maxY) / this.ySize, 1);
		
		return getValues(row, column, xCount, yCount);
	}

	public double getNullValue() {
		return this.noDataValue;
	}

	public double getMinX() {
		return this.minX;
	}

	public double getMaxX() {
		return this.minX;
	}

	public double getMinY() {
		return this.minY;
	}

	public double getMaxY() {
		return this.maxY;
	}

	public double getXSize() {
		return this.xSize;
	}

	public double getYSize() {
		return this.ySize;
	}

	public int getRow() {
		return this.row;
	}

	public int getColumn() {
		return this.column;
	}
	// <+++++++++++++++++++++++++++++++++++++>

	// <+++++++++++++++++++++++++++++++++++++>
	// <++++++++++ modify Raster file +++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++>

	public void setValue(int row, int column, double value) {
		initiallizeValueArray();
		this.valueList.get(row)[column] = value;
	}

	public void setValue(double x, double y, double value) {
		int[] position = getPosition(x, y);
		int row = position[0];
		int column = position[1];
		this.setValue(row, column, value);
	}

	private void initiallizeValueArray() {
		if (this.valueList.isEmpty()) {
			this.valueList = this.getValues(0, 0, this.column, this.row);
		}
	}

	// <+++++++++++++++++++++++++++++++++++++>
	// <+++++++++++ Global Function +++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++>
	public int[] getPosition(double x, double y) {
		int[] position = new int[2];
		int column = AtCommonMath.getDecimal_Int((x - this.minX) / this.xSize, this.dataDecimal);
		int row = AtCommonMath.getDecimal_Int((y - this.maxY) / this.ySize, this.dataDecimal);

		position[0] = row;
		position[1] = column;
		return position;
	}
}
