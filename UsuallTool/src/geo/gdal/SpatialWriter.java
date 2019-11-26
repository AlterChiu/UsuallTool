package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	protected List<Geometry> geometryList = new ArrayList<Geometry>();
	protected List<Map<String, Object>> attribute = new ArrayList<Map<String, Object>>();
	protected Map<String, String> fieldType = new TreeMap<String, String>();

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
	}

	public SpatialWriter setPathList(List<Path2D> pathList) {
		// translate path to geometry
		pathList.forEach(e -> {
			this.geometryList.add(getGeometry(e));
		});

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
		return this;
	}

	public SpatialWriter setPathList(Path2D path) {
		// translate path to geometry
		this.geometryList.add(getGeometry(path));

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
		return this;
	}

	public SpatialWriter setGeoList(List<Geometry> getList) {
		// translate path to geometry
		this.geometryList = getList;

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
		return this;
	}
	// <=========================================>

	/*
	 * 
	 * 
	 */
	// <==========================================>
	// <output setting>
	// <==========================================>
	public SpatialWriter setAttribute(List<Map<String, Object>> attributeTable) {
		this.attribute.clear();
		this.attribute = attributeTable;
		return this;
	}

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

	public SpatialWriter addFeature(Geometry geometry, Map<String, Object> feature) {
		this.geometryList.add(geometry);
		this.attribute.add(feature);
		return this;
	}

	public SpatialWriter addFeature(Path2D path, Map<String, Object> feature) {
		this.geometryList.add(getGeometry(path));
		this.attribute.add(feature);
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

	public void saceAs(String saveAdd, String saceTyping) {
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
			if (type.equals("STRING") || type.equals("CHAR") || type.equals("STR") || type.equals("CHARATER")) {
				field.SetType(ogr.OFTString);
				field.SetName(name);
				field.SetWidth(20);
				outLayer.CreateField(field);

			} else if (type.equals("DOUBLE") || type.equals("FLOAT") || type.equals("REAL")) {
				field.SetType(ogr.OFTReal);
				field.SetName(name);
				field.SetWidth(20);
				field.SetPrecision(5);
				outLayer.CreateField(field);

			} else if (type.equals("INT") || type.equals("INTEGER") || type.equals("INTEGER64")) {
				field.SetType(ogr.OFTInteger);
				field.SetName(name);
				field.SetWidth(20);
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
		for (int index = 0; index < this.geometryList.size(); index++) {
			Feature feature = new Feature(outLayer.GetLayerDefn());

			// attribute value
			try {
				for (String attributeKey : attribute.get(index).keySet()) {

					String type = this.fieldType.get(attributeKey).toUpperCase();
					if (type.equals("STRING") || type.equals("CHAR") || type.equals("STR") || type.equals("CHARATER")) {
						feature.SetField(attributeKey, (String) attribute.get(index).get(attributeKey));

					} else if (type.equals("DOUBLE") || type.equals("FLOAT") || type.equals("REAL")) {
						feature.SetField(attributeKey, (Double) attribute.get(index).get(attributeKey));

					} else if (type.equals("INT") || type.equals("INTEGER")) {
						feature.SetField(attributeKey, (Integer) attribute.get(index).get(attributeKey));

					} else if (type.equals("DATE") || type.equals("TIME")) {
						feature.SetField(attributeKey, (Double) attribute.get(index).get(attributeKey));
					}

				}
			} catch (Exception e) {
			}

			// geometry
			feature.SetGeometry(this.geometryList.get(index));
			outLayer.CreateFeature(feature);
			feature.delete();
		}
		dataSourceDriver.delete();
		outDataSource.delete();
	}

	protected static void pointGeometry() {
		outGeoType = geoType_Point;
	}
}
