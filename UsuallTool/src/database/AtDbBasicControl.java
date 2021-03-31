package database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AtDbBasicControl implements Closeable {
	private DbDriver driver;
	private Connection connect;

	public AtDbBasicControl(String url, DbDriver driver) throws SQLException, ClassNotFoundException {
		StringBuilder connectUrl = new StringBuilder();
		connectUrl.append(driver.getDriverPrefix().prefix);
		connectUrl.append(url);

		this.driver = driver;
		this.driver.getDriverPrefix().loadDriver();
		this.connect = DriverManager.getConnection(connectUrl.toString());
	}

	public AtDbBasicControl(String url, String user, String pass, DbDriver driver)
			throws SQLException, ClassNotFoundException {
		StringBuilder connectUrl = new StringBuilder();
		connectUrl.append(driver.getDriverPrefix().prefix);
		connectUrl.append(url);

		this.driver = driver;
		this.driver.getDriverPrefix().loadDriver();
		this.connect = DriverManager.getConnection(connectUrl.toString(), user, pass);
	}

	public Connection getConnection() {
		return this.connect;
	}

	public void excequteQuery(String query) {
		try {
			this.connect.prepareStatement(query).execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getTables() throws SQLException {
		List<String> outList = new ArrayList<>();
		ResultSet tables = this.connect.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
		while (tables.next()) {
			outList.add(tables.getString("TABLE_NAME"));
		}

		return outList;
	}

	public Map<String, String> getTableColumns(String table) throws SQLException {
		Map<String, String> outMap = new LinkedHashMap<>();
		ResultSet result = this.connect.getMetaData().getColumns(null, null, table, null);

		while (result.next()) {
			outMap.put(result.getString("COLUMN_NAME"), result.getString("TYPE_NAME"));
		}
		return outMap;
	}

	public PreparedStatement createPrepareStatement(String query) throws SQLException {
		return this.connect.prepareStatement(query);
	}

	public static enum DbDriver {
		MYSQL, MSSQL, SQLITE, POSTGRES;

		public DriverPrefix getDriverPrefix() {
			switch (this) {
			case MYSQL:
				return new DriverPrefix("com.mysql.jdbc.Driver", "jdbc:mysql://");
			case MSSQL:
				return new DriverPrefix("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://");
			case SQLITE:
				return new DriverPrefix("org.sqlite.JDBC", "jdbc:sqlite://");
			case POSTGRES:
				return new DriverPrefix("org.postgresql.Driver", "jdbc:postgresql://");
			}
			return null;
		}
	}

	private static class DriverPrefix {
		private String driver;
		private String prefix;

		public DriverPrefix(String driver, String prefix) {
			this.driver = driver;
			this.prefix = prefix;
		}

		public void loadDriver() throws ClassNotFoundException {
			Class.forName(this.driver);
		}

	}

	public static String TypeConvert(int typeName) {
		switch (typeName) {
		case java.sql.Types.BINARY:
			return "BINARY";
		case java.sql.Types.BIT:
			return "BIT";
		case java.sql.Types.BLOB:
			return "BLOB";
		case java.sql.Types.BOOLEAN:
			return "Boolean";
		case java.sql.Types.CHAR:
			return "CHAR";
		case java.sql.Types.DATE:
			return "DATE";
		case java.sql.Types.DECIMAL:
			return "DECIMAL";
		case java.sql.Types.DOUBLE:
			return "DOUBLE";
		case java.sql.Types.FLOAT:
			return "FLOAT";
		case java.sql.Types.INTEGER:
			return "INTEGER";
		case java.sql.Types.NCHAR:
			return "NCHAR";
		case java.sql.Types.NULL:
			return "NULL";
		case java.sql.Types.NUMERIC:
			return "NUMERIC";
		case java.sql.Types.REAL:
			return "REAL";
		case java.sql.Types.TIME:
			return "TIME";
		case java.sql.Types.TINYINT:
			return "TINYINT";
		case java.sql.Types.VARBINARY:
			return "VARBINARY";
		default:
			return "OTHER";
		}
	}

	@Override
	public void close() throws IOException {
		try {
			this.connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
