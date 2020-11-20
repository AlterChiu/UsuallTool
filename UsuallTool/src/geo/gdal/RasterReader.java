
package geo.gdal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import geo.gdal.raster.GDAL_RASTER_ToPNG;
import geo.gdal.raster.GDAL_RASTER_Warp;
import usualTool.AtCommonMath;

public class RasterReader {
	private String fileAdd;
	private double noDataValue;
	private int dataDecimal = 4;

	// gdal raster object
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

	public RasterReader(String fileAdd) {
		this.fileAdd = fileAdd;

		// initial raster file
		gdal.AllRegister();
		Dataset rasterData = gdal.Open(this.fileAdd);
		this.rasterBand = rasterData.GetRasterBand(1);

		// get noData value
		Double[] nullVlaue = new Double[1];
		this.rasterBand.GetNoDataValue(nullVlaue);
		this.noDataValue = nullVlaue[0];

		// get raster properties
		double[] dataProperies = rasterData.GetGeoTransform();
		this.minX = AtCommonMath.getDecimal_Double(dataProperies[0], this.dataDecimal);
		this.maxY = AtCommonMath.getDecimal_Double(dataProperies[3], this.dataDecimal);

		this.xSize = AtCommonMath.getDecimal_Double(dataProperies[1], this.dataDecimal);
		this.ySize = AtCommonMath.getDecimal_Double(dataProperies[5], this.dataDecimal);

		this.column = this.rasterBand.getXSize();
		this.row = this.rasterBand.getYSize();

		this.maxX = AtCommonMath.getDecimal_Double(this.minX + (this.column - 1) * this.xSize, this.dataDecimal);
		this.minY = AtCommonMath.getDecimal_Double(this.maxY + (this.row - 1) * this.ySize, this.dataDecimal);
	}

	// <+++++++++++++++++++++++++++++++++++++>
	// <++++++++++ Read Raster file ++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++>

	// start from left top
	public double getValue(int column, int row) {
		if (row < 0 || column < 0) {
			return this.noDataValue;
		} else {
			return this.getValues(column, row, 1, 1).get(0)[0];
		}
	}

	public double getValue(double x, double y) {
		int[] position = getPosition(x, y);
		int row = position[0];
		int column = position[1];
		return getValue(column, row);
	}

	public List<Double[]> getValues(int column, int row, int xCount, int yCount) {
		List<Double[]> outList = new ArrayList<>();

		// read from raster
		double[] valueArray = new double[xCount * yCount];
		this.rasterBand.ReadRaster(column, row, xCount, yCount, valueArray);

		// translate to list
		for (int outRow = 0; outRow < yCount; outRow++) {

			List<Double> temptList = new ArrayList<>();
			for (int outColumn = 0; outColumn < xCount; outColumn++) {

				int index = xCount * outRow + outColumn;
				temptList.add(valueArray[index]);
			}
			outList.add(temptList.parallelStream().toArray(Double[]::new));
		}

		return outList;
	}

	public List<Double[]> getValues(double x, double y, int xCount, int yCount) {
		int[] position = getPosition(x, y);
		int row = position[0];
		int column = position[1];
		return getValues(column, row, xCount, yCount);
	}

	public List<Double[]> getVlaues(double minX, double minY, double maxX, double maxY) {
		int[] position = getPosition(minX, maxY);
		int row = position[0];
		int column = position[1];
		int xCount = AtCommonMath.getDecimal_Int((maxX - minX) / this.xSize, 1);
		int yCount = AtCommonMath.getDecimal_Int((minY - maxY) / this.ySize, 1);

		return getValues(column, row, xCount, yCount);
	}

	public double getNullValue() {
		return this.noDataValue;
	}

	public double getMinX() {
		return this.minX;
	}

	public double getMaxX() {
		return this.maxX;
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
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(64);
		byteBuffer.order(ByteOrder.nativeOrder());
		byteBuffer.put((byte) value);
		this.rasterBand.WriteRaster_Direct(column, row, 1, 1, byteBuffer);
	}

	public void setValue(double x, double y, double value) {
		int[] position = getPosition(x, y);
		int row = position[0];
		int column = position[1];
		this.setValue(row, column, value);
	}

	public void setNullValue(double nullValue) {
		this.noDataValue = nullValue;
		this.rasterBand.SetNoDataValue(nullValue);
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

	public void saveAs(String fileAdd) {

		Driver driver = gdal.GetDriverByName("GTiff");
		Dataset dataset = driver.Create(fileAdd, this.column, this.row, 1, gdalconst.GDT_Float32);
		dataset.SetGeoTransform(new double[] { this.minX, this.xSize, 0, this.maxY, 0, this.ySize });

		Band band = dataset.GetRasterBand(1);
		band.SetNoDataValue(this.noDataValue);


		List<Double[]> points = this.getValues(0, 0, this.column, this.row);
		for (int row = 0; row < points.size(); row++) {
			for (int column = 0; column < points.get(row).length; column++) {

				ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
				byteBuffer.order(ByteOrder.nativeOrder());

				byteBuffer.put((byte) points.get(row)[column].doubleValue());
				band.WriteRaster(column, row, 1, 1, new double[] { points.get(row)[column] });
			}
		}
		dataset.delete();
	}

	public void saveAsImage(String fileAdd, Map<Double, Integer[]> colorMap) throws IOException, InterruptedException {
		GDAL_RASTER_ToPNG.save(this.fileAdd, colorMap, fileAdd);
	}

	public static void saveAs(String fileAdd, String saveAdd) throws InterruptedException, IOException {
		GDAL_RASTER_Warp warp = new GDAL_RASTER_Warp(fileAdd);
		warp.save(saveAdd);
	}




}
