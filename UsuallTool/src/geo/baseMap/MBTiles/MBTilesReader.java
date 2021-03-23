package geo.baseMap.MBTiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MBTilesReader {
	private Connection connection;
	private final String columnTitle = "tile_column";
	private final String rowTitle = "tile_row";
	private final String dataTitle = "tile_data";
	private final String zoomTitle = "zoom_level";

	public MBTilesReader(String fileAdd) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + fileAdd);
	}

}
