package geo.gdal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	// <=========================================>
	// <constructor>
	// <=========================================>
	public SpatialReader(String fileAdd) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");
		this.dataSource = ogr.Open(fileAdd);
		detectAttributeTitle();
		detectAttributeTable();
		detectAttributeTitleType();
		this.close();
	}

	public SpatialReader(String fileAdd, String encode) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_" + encode, "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", encode);
		this.dataSource = ogr.Open(fileAdd);
		detectAttributeTitle();
		detectAttributeTable();
		detectAttributeTitleType();
		this.close();
	}

	public SpatialReader(DataSource dataSource) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");
		this.dataSource = dataSource;
		detectAttributeTitle();
		detectAttributeTable();
		detectAttributeTitleType();
		this.close();
	}

	public SpatialReader(DataSource dataSource, String encode) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_" + encode, "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", encode);
		this.dataSource = dataSource;
		detectAttributeTitle();
		detectAttributeTable();
		detectAttributeTitleType();
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
	private void detectAttributeTitle() {
		FeatureDefn layerDefn = this.dataSource.GetLayer(0).GetLayerDefn();
		for (int index = 0; index < layerDefn.GetFieldCount(); index++) {
			attributeTitles.add(layerDefn.GetFieldDefn(index).GetName());
		}
	}

	private void detectAttributeTitleType() {
		FeatureDefn layerDefn = this.dataSource.GetLayer(0).GetLayerDefn();
		for (int index = 0; index < layerDefn.GetFieldCount(); index++) {
			attributeTitleType.put(layerDefn.GetFieldDefn(index).GetName(),
					layerDefn.GetFieldDefn(index).GetTypeName());
		}
	}

	private void detectAttributeTable() {
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
