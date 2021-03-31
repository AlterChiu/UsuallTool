package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AtSqlResult {

	private Map<String, Integer> resultTypes = new HashMap<>();
	private List<Map<String, Object>> sqlResult = new ArrayList<>();
	private List<String> titles = new ArrayList<>();

	private ResultSet resultSet;

	public AtSqlResult(ResultSet resultSet) throws SQLException {
		this.resultSet = resultSet;

		// get metaData
		this.setMetaData();

		// get resultSet
		this.setResult();
	}

	private void setMetaData() throws SQLException {
		// set meta data
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int index = 1; index <= columnCount; index++) {
			this.resultTypes.put(metaData.getColumnName(index), metaData.getColumnType(index));
			this.titles.add(metaData.getColumnName(index));
		}
	}

	private void setResult() throws SQLException {
		while (this.resultSet.next()) {
			Map<String, Object> temptMap = new LinkedHashMap<>();

			for (String title : this.titles) {
				switch (this.resultTypes.get(title)) {
				case java.sql.Types.BINARY:
					temptMap.put(title, this.resultSet.getBytes(title));
					break;
				case java.sql.Types.BIT:
					temptMap.put(title, this.resultSet.getBytes(title));
					break;
				case java.sql.Types.BLOB:
					temptMap.put(title, this.resultSet.getBinaryStream(title));
					break;
				case java.sql.Types.BOOLEAN:
					temptMap.put(title, this.resultSet.getBoolean(title));
					break;
				case java.sql.Types.CHAR:
					temptMap.put(title, this.resultSet.getString(title));
					break;
				case java.sql.Types.DATE:
					temptMap.put(title, this.resultSet.getString(title));
					break;
				case java.sql.Types.DECIMAL:
					temptMap.put(title, this.resultSet.getDouble(title));
					break;
				case java.sql.Types.DOUBLE:
					temptMap.put(title, this.resultSet.getDouble(title));
					break;
				case java.sql.Types.FLOAT:
					temptMap.put(title, this.resultSet.getDouble(title));
					break;
				case java.sql.Types.INTEGER:
					temptMap.put(title, this.resultSet.getInt(title));
					break;
				case java.sql.Types.NCHAR:
					temptMap.put(title, this.resultSet.getString(title));
					break;
				case java.sql.Types.NULL:
					temptMap.put(title, null);
					break;
				case java.sql.Types.NUMERIC:
					temptMap.put(title, this.resultSet.getInt(title));
					break;
				case java.sql.Types.REAL:
					temptMap.put(title, this.resultSet.getDouble(title));
					break;
				case java.sql.Types.TIME:
					temptMap.put(title, this.resultSet.getString(title));
					break;
				case java.sql.Types.TINYINT:
					temptMap.put(title, this.resultSet.getInt(title));
					break;
				case java.sql.Types.VARBINARY:
					temptMap.put(title, this.resultSet.getString(title));
					break;
				default:
					temptMap.put(title, this.resultSet.getString(title));
					break;
				}
			}

			this.sqlResult.add(temptMap);
		}
	}

	public List<String> getTitles() {
		return this.titles;
	}

	public List<Map<String, Object>> getResults() {
		return this.sqlResult;
	}

	public Map<String, Integer> getTypes() {
		return this.resultTypes;
	}
}
