package geo.gdal;

import java.util.ArrayList;
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
	private List<Map<String, String>> featureTable = new ArrayList<>();
	private Map<String, String> attributeTitleType = new TreeMap<>();

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
	}

	public SpatialReader(String fileAdd, String encode) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_" + encode, "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", encode);
		this.dataSource = ogr.Open(fileAdd);
		detectAttributeTitle();
		detectAttributeTable();
		detectAttributeTitleType();
	}

	public SpatialReader(DataSource dataSource) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");
		this.dataSource = dataSource;
		detectAttributeTitle();
		detectAttributeTable();
		detectAttributeTitleType();
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

	public List<Map<String, String>> getAttributeTable() {
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
	// <============================================>

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
		for (int index = 0; index < layer.GetFeatureCount(); index++) {
			Feature feature = layer.GetFeature(index);

			// get the geometry of each feature
			this.geometryList.add(feature.GetGeometryRef());

			// get the attribute table
			Map<String, String> temptMap = new TreeMap<String, String>();
			for (String key : this.attributeTitles) {
				try {
					String value = feature.GetFieldAsString(key);
					temptMap.put(key, value);
				} catch (Exception e) {
					temptMap.put(key, "");
				}
			}
			this.featureTable.add(temptMap);
		}
	}
	// <==============================================>

}
