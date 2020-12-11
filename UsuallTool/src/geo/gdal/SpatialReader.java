
package geo.gdal;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

public class SpatialReader {
	private DataSource dataSource;
	private List<String> attributeTitles = new ArrayList<String>();
	private List<Geometry> geometryList = new ArrayList<Geometry>();
	private List<Map<String, Object>> featureTable = new ArrayList<>();
	private Map<String, String> attributeTitleType = new LinkedHashMap<>();
	private int EPSG = 4326;
	private String encoding = "UTF-8";

	// <=========================================>
	// <constructor>
	// <=========================================>
	public SpatialReader(String fileAdd) throws UnsupportedEncodingException {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", this.encoding);
		this.dataSource = ogr.Open(fileAdd);
		detectAttributeTitle();
		detectAttributeTable();
		this.close();
	}

	public SpatialReader(String fileAdd, String encode) throws UnsupportedEncodingException {
		this.encoding = encode;

		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", this.encoding);
		this.dataSource = ogr.Open(fileAdd);
		detectAttributeTitle();
//		detectAttributeTable();
		this.close();
	}

	public SpatialReader(DataSource dataSource) throws UnsupportedEncodingException {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", this.encoding);
		this.dataSource = dataSource;
		detectAttributeTitle();
		detectAttributeTable();
		this.close();
	}

	public SpatialReader(DataSource dataSource, String encode) throws UnsupportedEncodingException {
		this.encoding = encode;
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF-8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", this.encoding);
		this.dataSource = dataSource;
		detectAttributeTitle();
		detectAttributeTable();
	}

	// <===========================================>

	/*
	 * return function
	 * 
	 */
	// <============================================>
	public List<String> getAttrubuteTitle() {
		return this.attributeTitles;
	}

	public List<Map<String, Object>> getAttributeTable() {
		return this.featureTable;
	}

	public List<Geometry> getGeometryList() {
		return this.geometryList;
	}

	public DataSource getSpatitalDataSource() {
		return this.dataSource;
	}

	public Map<String, String> getAttributeTitleType() {
		return this.attributeTitleType;
	}

	public Map<String, List<Geometry>> getGeoListMap(String columnID) {
		if (!this.attributeTitleType.containsKey(columnID)) {
			new Exception("No such column ID in this spatialFile");
			return null;
		}

		Map<String, List<Geometry>> outMap = new LinkedHashMap<>();
		for (int index = 0; index < this.geometryList.size(); index++) {
			String attrValue = String.valueOf(this.featureTable.get(index).get(columnID));

			List<Geometry> temptGeoList = Optional.ofNullable(outMap.get(attrValue)).orElse(new ArrayList<>());
			temptGeoList.add(this.geometryList.get(index));
			outMap.put(attrValue, temptGeoList);
		}

		return outMap;
	}

	public void reNameFeild(String oldFieldName, String newFeildName) {

		// change type
		if (this.attributeTitleType.containsKey(oldFieldName)) {
			this.attributeTitleType.put(newFeildName, this.attributeTitleType.get(oldFieldName));
		} else {
			this.attributeTitleType.remove(oldFieldName);
		}

		// change attrTables
		this.featureTable.forEach(feature -> {
			feature.put(newFeildName, feature.get(oldFieldName));
			feature.remove(oldFieldName);
		});
	}

	public int getEPSG() {
		return this.EPSG;
	}
	// <============================================>

	/*
	 * translate function
	 */
	public void saveAsGeoJson(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsGeoJson(saveAdd);
	}

	public void saveAsShp(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsShp(saveAdd);
	}

	public void saveAsCsv(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsCsv(saveAdd);
	}

	public void saveAsTopoJson(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsTopoJson(saveAdd);
	}

	public void saveAsCAD(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsCAD(saveAdd);
	}

	public void saveAsDWG(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsDWG(saveAdd);
	}

	public void saveAsKML(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsKML(saveAdd);
	}

	private SpatialWriter getSpatialWriter(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = new SpatialWriter();
		spWriter.setFieldType(this.attributeTitleType);

		List<Geometry> temptGeoList = new ArrayList<>();
		this.geometryList.forEach(geo -> {
			temptGeoList.add(GdalGlobal.GeometryTranslator(geo, importCoordinate, outputCoordinate));
		});
		spWriter.setGeoList(temptGeoList);

		List<Map<String, Object>> temptList = new ArrayList<>();
		this.featureTable.forEach(feature -> {
			Map<String, Object> temptMap = new TreeMap<>();
			for (String key : feature.keySet()) {
				temptMap.put(key, feature.get(key));
			}
			temptList.add(temptMap);
		});
		spWriter.setAttribute(temptList);
		spWriter.setCoordinateSystem(outputCoordinate);
		return spWriter;
	}

	/*
	 * 
	 * function for private
	 */
	// <===========================================>
	// <get the name of attribute titles>
	private void detectAttributeTitle() throws UnsupportedEncodingException {
		FeatureDefn layerDefn = this.dataSource.GetLayer(0).GetLayerDefn();
		for (int index = 0; index < layerDefn.GetFieldCount(); index++) {
			String titleName = new String(layerDefn.GetFieldDefn(index).GetName().getBytes("BIG5"), "UTF-8");


			// get title name
			attributeTitles.add(titleName);

			// get title style
			attributeTitleType.put(titleName, layerDefn.GetFieldDefn(index).GetTypeName().toUpperCase());
		}
		System.out.println(new String(layerDefn.GetFieldDefn(1).GetName().getBytes("UTF-8"), "UTF-8"));

	}

	private void detectAttributeTable() throws UnsupportedEncodingException {
		Layer layer = this.dataSource.GetLayer(0);
		try {
			this.EPSG = layer.GetSpatialRef().AutoIdentifyEPSG();
		} catch (Exception e) {
			new Exception("*WARN* error while detect EPSG ");
		}

		for (int index = 0; index < layer.GetFeatureCount(); index++) {
			Feature feature = layer.GetFeature(index);

			// get the geometry of each feature
			this.geometryList.add(feature.GetGeometryRef());

			// get the attribute table
			Map<String, Object> temptMap = new TreeMap<String, Object>();
			for (String key : this.attributeTitles) {
				key = new String(key.getBytes(), this.encoding);

				String type = this.attributeTitleType.get(key);
				try {
					if (type.contains("STRING") || type.contains("CHAR") || type.contains("STR")
							|| type.contains("CHARATER")) {
						temptMap.put(key, feature.GetFieldAsString(key));

					} else if (type.contains("DOUBLE") || type.contains("FLOAT") || type.contains("REAL")) {
						temptMap.put(key, feature.GetFieldAsDouble(key));

					} else if (type.contains("INT") || type.contains("INTEGER")) {
						temptMap.put(key, feature.GetFieldAsInteger(key));

					} else if (type.contains("DATE") || type.contains("TIME")) {
						temptMap.put(key, feature.GetFieldAsDouble(key));
					} else {
						temptMap.put(key, null);
					}
				} catch (Exception e) {
					temptMap.put(key, null);
				}
			}
			this.featureTable.add(temptMap);
		}
	}

	// <==============================================>
	private void close() {
		this.dataSource.delete();
	}
}
