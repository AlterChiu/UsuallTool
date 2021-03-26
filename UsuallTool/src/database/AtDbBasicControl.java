package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AtDbBasicControl {
	private DbDriver driver;
	private Connection connect;

	public AtDbBasicControl(String url, DbDriver driver) throws SQLException {
		StringBuilder connectUrl = new StringBuilder();
		connectUrl.append(driver.getDriverPrefix().prefix);
		connectUrl.append(url);

		this.connect = DriverManager.getConnection(connectUrl.toString());
		this.driver = driver;
		this.driver.getDriverPrefix().loadDriver();
	}

	public AtDbBasicControl(String url, String user, String pass, DbDriver driver) throws SQLException {
		StringBuilder connectUrl = new StringBuilder();
		connectUrl.append(driver.getDriverPrefix().prefix);
		connectUrl.append(url);

		this.connect = DriverManager.getConnection(connectUrl.toString(), user, pass);
		this.driver = driver;
		this.driver.getDriverPrefix().loadDriver();
	}

	public enum DbDriver {
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
		private boolean isLoad = false;

		public DriverPrefix(String driver, String prefix) {
			this.driver = driver;
			this.prefix = prefix;
		}

		public String getDriver() {
			return this.driver;
		}

		public String getPrefix() {
			return this.prefix;
		}

		public void loadDriver() {
			this.isLoad = true;
		}

		public boolean isLoad() {
			return this.isLoad;
		}
	}

	public class SqlResult {

		private Map<String, String> metadata = new HashMap<>();
		 

	}
}
