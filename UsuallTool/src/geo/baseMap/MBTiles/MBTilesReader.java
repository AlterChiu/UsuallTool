package geo.baseMap.MBTiles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.AtDbBasicControl;
import database.AtSqlResult;
import geo.baseMap.wms.WMSBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal.EnvelopBoundary;
import usualTool.AtFileFunction;

public class MBTilesReader {
	private AtDbBasicControl dbControl;
	private final String table = "tiles";
	private final String columnTitle = "tile_column";
	private final String rowTitle = "tile_row";
	private final String dataTitle = "tile_data";
	private final String zoomTitle = "zoom_level";

	public MBTilesReader(String fileAdd) throws ClassNotFoundException, SQLException {
		this.dbControl = new AtDbBasicControl(fileAdd, AtDbBasicControl.DbDriver.SQLITE);
	}

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
				StringBuilder deleteSql = new StringBuilder();
				deleteSql.append("delete from " + this.table);
				deleteSql.append(" where " + this.rowTitle + " = " + row);
				deleteSql.append(" and " + this.columnTitle + " = " + column);
				deleteSql.append(" and " + this.zoomTitle + " = " + zoom);
				this.dbControl.excequteQuery(deleteSql.toString());
				// get boundary
				GdalGlobal.EnvelopBoundary boundary = MBTiles.getBoundary(zoom, column, row, MBTiles.mbtilesEPSG);

				// get wms image
				wms.setBound(boundary, MBTiles.mbtilesEPSG);
				byte[] bytes = wms.getTileMap(256, 256);
				wms.saveAsPng(256, 256, "C:\\Users\\2101017\\Downloads\\test\\"
						+ AtFileFunction.getTempFileName("C:\\Users\\2101017\\Downloads\\test\\", ".png"));

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

	public void replace(int zoom, int column, int row, String imageAdd) throws SQLException, FileNotFoundException {

		// delete old blob
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("delete from " + this.table);
		deleteSql.append(" where " + this.rowTitle + " = " + row);
		deleteSql.append(" and " + this.columnTitle + " = " + column);
		deleteSql.append(" and " + this.zoomTitle + " = " + zoom);
		this.dbControl.excequteQuery(deleteSql.toString());

		// create preparedStatements
		StringBuilder prepareSql = new StringBuilder();
		prepareSql.append("insert into " + this.table);
		prepareSql.append(" (" + this.zoomTitle + "," + this.columnTitle + "," + this.rowTitle + "," + this.dataTitle
				+ ") values(?,?,?,?)");
		PreparedStatement preparedStatement = this.dbControl.createPrepareStatement(prepareSql.toString());

		// insert blob
		preparedStatement.setInt(1, zoom);
		preparedStatement.setInt(2, column);
		preparedStatement.setInt(3, row);
		preparedStatement.setBinaryStream(4, new FileInputStream(new File(imageAdd)));
		preparedStatement.executeUpdate();
	}

	public Blob getImageBlob(int zoom, int column, int row) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(table + " ");
		sql.append("where ");
		sql.append(zoomTitle + "=" + zoom + "and ");
		sql.append(rowTitle + "=" + row + "and ");
		sql.append(columnTitle + "=" + column);

		try {
			AtSqlResult result = new AtSqlResult(this.dbControl.createPrepareStatement(sql.toString()).executeQuery());
			return (Blob) result.getResults().get(0).get(this.dataTitle);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void saveImage(String saveAdd, int zoom, int column, int row)
			throws IOException, SQLException, InterruptedException {
		Blob blob = this.getImageBlob(zoom, column, row);
		InputStream input = blob.getBinaryStream();

		// prepare streams
		File temptFile = new File(saveAdd);
		FileOutputStream output = new FileOutputStream(temptFile);
		int temptLen = 0;

		// write file
		while ((temptLen = input.read()) != -1) {
			output.write(temptLen);
		}

		// close file
		output.close();
		input.close();
		AtFileFunction.waitFileComplete(saveAdd);
	}

}
