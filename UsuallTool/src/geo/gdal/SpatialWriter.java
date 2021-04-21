package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;

public class SpatialWriter {
	protected List<SpatialFeature> featureList = new ArrayList<>();
	protected Map<String, String> fieldType = new LinkedHashMap<>();

	// projection
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;
	private SpatialReference outputSpatitalSystem = new SpatialReference();

	// geometry
	private static String geoType_Polygon = "\"polygon\"";
	private static String geoType_Point = "\"point\"";
	private static String outGeoType = geoType_Polygon;

	private String layerName = "temptFile";

	public static String SAVINGTYPE_GeoJson = "Geojson";
	public static String SAVINGTYPE_SHP = "Esri Shapefile";
	public static String SAVINGTYPE_CSV = "CSV";
	public static String SAVINGTYPE_TopoJson = "TopoJSON";
	public static String SAVINGTYPE_CAD = "CAD";
	public static String SAVINGTYPE_DWG = "DWG";
	public static String SAVINGTYPE_KML = "KML";

	// <=========================================>
	// <constructor>
	// <=========================================>
	public SpatialWriter() {
		gdal.AllRegister();
		gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
	}

	public SpatialWriter setGeoList(List<Geometry> geoList) {

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
		this.initialFeatures(geoList.parallelStream().map(geo -> new SpatialFeature(geo)).collect(Collectors.toList()));
		return this;
	}

	public SpatialWriter setGeoList(Geometry geometry) {
		List<Geometry> geometryList = new ArrayList<>();
		geometryList.add(geometry);

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
		this.initialFeatures(
				geometryList.parallelStream().map(geo -> new SpatialFeature(geo)).collect(Collectors.toList()));
		return this;
	}

	public SpatialWriter setFeatureList(List<SpatialFeature> featureList) {
		this.featureList = featureList;
		return this;
	}

	private void initialFeatures(List<SpatialFeature> featureList) {
		this.featureList = featureList;
	}

	// <=========================================>

	public int getSize() {
		return this.featureList.size();
	}

	/*
	 * 
	 * 
	 */
	// <==========================================>
	// <output setting>
	// <==========================================>

	public SpatialWriter setLayerName(String name) {
		this.layerName = name;
		return this;
	}

	public SpatialWriter setCoordinateSystem(int system) {
		outputSpatitalSystem.ImportFromEPSG(system);
		return this;
	}

	public SpatialWriter setFieldType(Map<String, String> type) {
		this.fieldType = type;
		return this;
	}

	public SpatialWriter addFieldType(String titleName, String type) {
		this.fieldType.put(titleName, type);
		return this;
	}

	public SpatialWriter addFeature(Geometry geometry, Map<String, Object> feature) {
		this.featureList.add(new SpatialFeature(feature, geometry));
		return this;
	}

	public SpatialWriter reNameFeild(String oldFieldName, String newFeildName) {
		this.featureList.forEach(feature -> {
			feature.renameField(oldFieldName, newFeildName);
		});
		return this;
	}

	// <==========================================>

	/*
	 * 
	 */
	// <===========================================>
	// <output function>
	// <===========================================>
	public void saveAsGeoJson(String saveAdd) {
		Driver dr = ogr.GetDriverByName("Geojson");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAsShp(String saveAdd) {
		Driver dr = ogr.GetDriverByName("Esri Shapefile");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAsCsv(String saveAdd) {
		Driver dr = ogr.GetDriverByName("CSV");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAsTopoJson(String saveAdd) {
		Driver dr = ogr.GetDriverByName("TopoJSON");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAsCAD(String saveAdd) {
		Driver dr = ogr.GetDriverByName("CAD");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAsDWG(String saveAdd) {
		Driver dr = ogr.GetDriverByName("DWG");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAsKML(String saveAdd) {
		Driver dr = ogr.GetDriverByName("KML");
		createSpatialFile(saveAdd, dr);
	}

	public void saveAs(String saveAdd, String saceTyping) {
		Driver dr = ogr.GetDriverByName(saceTyping);
		createSpatialFile(saveAdd, dr);
	}

	// <===========================================>

	/*
	 * 
	 * 
	 */
	// <===========================================>
	// <private function>
	// <===========================================>
	protected Geometry getGeometry(Path2D path) {
		PathIterator temptPathIteratore = path.getPathIterator(null);
		float coordinate[] = new float[2];

		// start coordinate
		temptPathIteratore.currentSegment(coordinate);
		double startX = coordinate[0];
		double startY = coordinate[1];

		// output geometry ,start point
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" :" + outGeoType + " , \"coordinates\" : [[");
		sb.append("[" + startX + "," + startY + "],");
		temptPathIteratore.next();

		for (; !temptPathIteratore.isDone(); temptPathIteratore.next()) {
			temptPathIteratore.currentSegment(coordinate);
			sb.append("[" + coordinate[0] + "," + coordinate[1] + "],");
		}
		sb.append("[" + startX + "," + startY + "] ]] }");

		return Geometry.CreateFromJson(sb.toString());
	}

	/*
	 * 
	 * 
	 */
	private void createSpatialFile(String saveAdd, Driver dataSourceDriver) {
		// create output layer
		if (new File(saveAdd).exists()) {
			dataSourceDriver.DeleteDataSource(saveAdd);
		}
		DataSource outDataSource = dataSourceDriver.CreateDataSource(saveAdd);
		Layer outLayer = outDataSource.CreateLayer(this.layerName, this.outputSpatitalSystem);

		// create field
		List<String> fieldName = new ArrayList<String>(this.fieldType.keySet());
		for (String name : fieldName) {
			FieldDefn field = new FieldDefn();

			String type = this.fieldType.get(name).toUpperCase();
			if (type.contains("STRING") || type.contains("CHAR") || type.contains("STR") || type.contains("CHARATER")) {
				field.SetType(ogr.OFTString);
				field.SetName(name);
				outLayer.CreateField(field);

			} else if (type.contains("DOUBLE") || type.contains("FLOAT") || type.contains("REAL")) {
				field.SetType(ogr.OFTReal);
				field.SetName(name);
				outLayer.CreateField(field);

			} else if (type.contains("INT") || type.contains("INTEGER") || type.contains("INTEGER64")) {
				field.SetType(ogr.OFTInteger);
				field.SetName(name);
				outLayer.CreateField(field);

			} else if (type.equals("DATE") || type.equals("TIME")) {
				field.SetType(ogr.OFTDateTime);
				field.SetName(name);
				outLayer.CreateField(field);

			} else if (type.equals("NULL")) {
				System.out.println("null title " + name);
			} else {
				System.out.println("error type " + type);
			}
		}

		// add feature
		this.featureList.forEach(temptFeature -> {
			Feature feature = new Feature(outLayer.GetLayerDefn());

			// attribute value
			if (this.fieldType.keySet().size() > 0) {
				this.fieldType.keySet().forEach(attributeKey -> {
					try {
						String type = this.fieldType.get(attributeKey).toUpperCase();
						if (type.contains("STRING") || type.contains("CHAR") || type.contains("STR")
								|| type.contains("CHARATER")) {
							feature.SetField(attributeKey, (String) temptFeature.getProperty(attributeKey));

						} else if (type.contains("DOUBLE") || type.contains("FLOAT") || type.contains("REAL")) {
							feature.SetField(attributeKey, (Double) temptFeature.getProperty(attributeKey));

						} else if (type.contains("INT") || type.contains("INTEGER")) {
							feature.SetField(attributeKey, (Integer) temptFeature.getProperty(attributeKey));

						} else if (type.contains("DATE") || type.contains("TIME")) {
							feature.SetField(attributeKey, (Double) temptFeature.getProperty(attributeKey));
						}
					} catch (Exception e) {
						feature.SetFieldNull(attributeKey);
					}
				});
			}

			// geometry
			feature.SetGeometry(temptFeature.getGeometry());
			outLayer.CreateFeature(feature);
			feature.delete();
		});

		dataSourceDriver.delete();
		outDataSource.delete();
	}

	protected static void pointGeometry() {
		outGeoType = geoType_Point;
	}
}
