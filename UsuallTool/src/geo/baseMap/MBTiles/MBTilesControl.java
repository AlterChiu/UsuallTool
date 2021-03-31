package geo.baseMap.MBTiles;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.gdal.ogr.Geometry;

import database.AtDbBasicControl;
import database.AtSqlResult;
import geo.baseMap.wms.WMSBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal.EnvelopBoundary;
import geo.gdal.GdalGlobal_DataFormat;
import geo.gdal.RasterReader;
import geo.gdal.SpatialWriter;
import geo.gdal.raster.Gdal_RasterTranslateFormat;
import geo.gdal.raster.Gdal_RasterWarp;
import usualTool.AtFileFunction;

public class MBTilesControl implements Closeable {
	private AtDbBasicControl dbControl;
	private final String table = "tiles";
	private final String columnTitle = "tile_column";
	private final String rowTitle = "tile_row";
	private final String dataTitle = "tile_data";
	private final String zoomTitle = "zoom_level";

	public MBTilesControl(String fileAdd) throws ClassNotFoundException, SQLException {
		this.dbControl = new AtDbBasicControl(fileAdd, AtDbBasicControl.DbDriver.SQLITE);
	}

	// <GET PROPERTIES>
	// <========================================================>
	public List<Integer> getRows(int zoom, int column) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + this.rowTitle + " from ");
		sql.append(table + " ");
		sql.append("where ");
		sql.append(zoomTitle + "=" + zoom + " and ");
		sql.append(columnTitle + "=" + column);

		return this.getDistinctList(sql.toString(), this.rowTitle);
	}

	public List<Integer> getRows(int zoom) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + this.rowTitle + " from ");
		sql.append(table + " ");
		sql.append("where ");
		sql.append(zoomTitle + "=" + zoom);

		return this.getDistinctList(sql.toString(), this.rowTitle);
	}

	public List<Integer> getColumns(int zoom, int row) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + this.columnTitle + " from ");
		sql.append(table + " ");
		sql.append("where ");
		sql.append(zoomTitle + "=" + zoom + " and ");
		sql.append(this.rowTitle + "=" + row);

		return this.getDistinctList(sql.toString(), this.columnTitle);
	}

	public List<Integer> getColumns(int zoom) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + this.columnTitle + " from ");
		sql.append(table + " ");
		sql.append("where ");
		sql.append(zoomTitle + "=" + zoom);

		return this.getDistinctList(sql.toString(), this.columnTitle);
	}

	public List<Integer> getZooms() {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + this.zoomTitle + " from ");
		sql.append(table);

		return this.getDistinctList(sql.toString(), this.zoomTitle);
	}

	private List<Integer> getDistinctList(String sql, String positionType) {
		try {
			Set<Integer> outList = new TreeSet<>();
			AtSqlResult result = new AtSqlResult(this.dbControl.createPrepareStatement(sql.toString()).executeQuery());
			result.getResults().forEach(e -> {
				outList.add((int) e.get(positionType));
			});

			return new ArrayList<>(outList);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	// <========================================================>

	// replace a zoomLevel with wmsTiles
	public void replace(int zoom, String wmsName, String layers) throws Exception {
		// create wms connection
		WMSBasicControl wms = new WMSBasicControl(wmsName);
		wms.setLayer(layers);

		// create preparedStatements
		StringBuilder prepareSql = new StringBuilder();
		prepareSql.append("insert into " + this.table);
		prepareSql.append(" (" + this.zoomTitle + "," + this.columnTitle + "," + this.rowTitle + "," + this.dataTitle
				+ ") values(?,?,?,?)");
		PreparedStatement preparedStatement = this.dbControl.createPrepareStatement(prepareSql.toString());

		List<Integer> rows = this.getRows(zoom);
		for (int row : rows) {
			List<Integer> columns = this.getColumns(zoom, row);

			for (int column : columns) {
				System.out.println(row + "\t" + column);

				// delete old blob
				this.delete(zoom, row, column);

				// get boundary
				GdalGlobal.EnvelopBoundary boundary = MBTiles.getBoundary(zoom, column, row, MBTiles.mbtilesEPSG);

				// get wms image
				wms.setBound(boundary, MBTiles.mbtilesEPSG);
				byte[] bytes = wms.getTileMap(256, 256);

				// insert blob
				preparedStatement.setInt(1, zoom);
				preparedStatement.setInt(2, column);
				preparedStatement.setInt(3, row);
				preparedStatement.setBinaryStream(4, new ByteArrayInputStream(bytes), bytes.length);
				preparedStatement.executeUpdate();
			}
		}
		preparedStatement.execute();
	}

	// add image
	public void add(int zoom, int column, int row, String imageAdd)
			throws SQLException, InterruptedException, IOException {

		// delete old blob
		this.delete(zoom, row, column);

		// create preparedStatements
		StringBuilder prepareSql = new StringBuilder();
		prepareSql.append("insert into " + this.table);
		prepareSql.append(" (" + this.zoomTitle + "," + this.columnTitle + "," + this.rowTitle + "," + this.dataTitle
				+ ") values(?,?,?,?)");
		PreparedStatement preparedStatement = this.dbControl.createPrepareStatement(prepareSql.toString());

		// insert blob
		byte[] blobByts = FileUtils.readFileToByteArray(new File(imageAdd));
		preparedStatement.setInt(1, zoom);
		preparedStatement.setInt(2, column);
		preparedStatement.setInt(3, row);
		preparedStatement.setBinaryStream(4, new ByteArrayInputStream(blobByts), blobByts.length);
		preparedStatement.executeUpdate();
		AtFileFunction.waitFileComplete(imageAdd);
	}

	// add tile with crop
	public void add(int zoom, RasterReader raster, int epsg) throws Exception {
		// get zoom resolution
		double cellSize = MBTiles.getZoomLevelResolution(zoom);

		// convert coordination
		String temptFolder = AtFileFunction.createTemptFolder();
		String coordinateTranslateFileAdd = temptFolder + "//CoordinateTranslate.tif";
		Gdal_RasterWarp translateCoordinate = new Gdal_RasterWarp(raster);
		translateCoordinate.setCoordinate(epsg, MBTiles.mbtilesEPSG);
		translateCoordinate.reSample(cellSize, cellSize, MBTiles.mbtilesEPSG);
		translateCoordinate.save(coordinateTranslateFileAdd);

		// get properties
		RasterReader convertedRaster = new RasterReader(coordinateTranslateFileAdd);
		double[] leftTopXY = new double[] { convertedRaster.getMinX(), convertedRaster.getMaxY() };
		double[] RightBotlXY = new double[] { convertedRaster.getMaxX(), convertedRaster.getMinY() };

		int[] leftTopColumnRow = MBTiles.getColumnRow(zoom, leftTopXY[0], leftTopXY[1], MBTiles.mbtilesEPSG);
		int[] rightBotColumnRow = MBTiles.getColumnRow(zoom, RightBotlXY[0], RightBotlXY[1], MBTiles.mbtilesEPSG);
		int minRow = rightBotColumnRow[1];
		int minCol = leftTopColumnRow[0];
		int maxRow = leftTopColumnRow[1];
		int maxCol = rightBotColumnRow[0];

		// processing
		int total = (maxRow - minRow) * (maxCol - minCol);
		int current = 0;
		System.out.print(current);

		// crop raster to tiles
		String temptCropFileAdd = temptFolder + "\\temptCrop.tif";
		String temptPngFileAdd = temptFolder + "\\temptPng.tif";

		for (int row = minRow; row <= maxRow; row++) {
			for (int column = minCol; column <= maxCol; column++) {

				// run processing
				int temptCurrent = (int) ((row - minRow) * (column - minCol) / (double) total * 10); // every 10
				if (current < temptCurrent) {
					System.out.print("..." + temptCurrent * 10);
					current = temptCurrent;
				}

				// crop
				EnvelopBoundary boundary = MBTiles.getBoundary(zoom, column, row, 3857);
				Gdal_RasterTranslateFormat rasterCrop = new Gdal_RasterTranslateFormat(coordinateTranslateFileAdd);
				rasterCrop.setBoundary(boundary.getMinX(), boundary.getMaxX(), boundary.getMinY(), boundary.getMaxY());
				rasterCrop.save(temptCropFileAdd, GdalGlobal_DataFormat.DATAFORMAT_RASTER_GTiff);

				// translate to pngFile
				Gdal_RasterTranslateFormat convertPng = new Gdal_RasterTranslateFormat(temptCropFileAdd);
				convertPng.save(temptPngFileAdd, GdalGlobal_DataFormat.DATAFORMAT_RASTER_PNG);

				// add to MBTiles
				this.add(zoom, column, row, temptPngFileAdd);
			}
		}

		// clear catch
		AtFileFunction.delete(temptFolder);
		System.out.println("  Completed");
	}

	public void delete(int zoom, int row, int column) {
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("delete from " + this.table);
		deleteSql.append(" where " + this.rowTitle + " = " + row);
		deleteSql.append(" and " + this.columnTitle + " = " + column);
		deleteSql.append(" and " + this.zoomTitle + " = " + zoom);
		this.dbControl.excequteQuery(deleteSql.toString());
	}

	public InputStream getImageBlob(int zoom, int column, int row) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(table);
		sql.append(" where ");
		sql.append(zoomTitle + "=" + zoom + " and ");
		sql.append(rowTitle + "=" + row + " and ");
		sql.append(columnTitle + "=" + column);

		try {
			AtSqlResult result = new AtSqlResult(this.dbControl.createPrepareStatement(sql.toString()).executeQuery());
			return (InputStream) result.getResults().get(0).get(this.dataTitle);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void saveImage(String saveAdd, int zoom, int column, int row)
			throws IOException, SQLException, InterruptedException {
		InputStream inputStream = this.getImageBlob(zoom, column, row);

		// prepare streams
		File temptFile = new File(saveAdd);
		FileOutputStream output = new FileOutputStream(temptFile);
		int temptLen = 0;

		// write file
		while ((temptLen = inputStream.read()) != -1) {
			output.write(temptLen);
		}

		// close file
		output.close();
		inputStream.close();
		AtFileFunction.waitFileComplete(saveAdd);
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		this.dbControl.close();
	}

}
