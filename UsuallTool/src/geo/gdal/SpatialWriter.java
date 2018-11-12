package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.File;
import java.sql.Date;
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
	private List<Geometry> geometryList = new ArrayList<Geometry>();
	private List<Map<String, Object>> attribute = new ArrayList<Map<String, Object>>();
	private Map<String, String> fieldType = new TreeMap<String, String>();

	// projection
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;
	private SpatialReference outputSpatitalSystem = new SpatialReference();

	private String layerName = "temptFile";

	// <=========================================>
	// <constructor>
	// <=========================================>
	public SpatialWriter(List<Path2D> pathList) {
		gdal.AllRegister();
		// translate path to geometry
		pathList.forEach(e -> {
			this.geometryList.add(getGeometry(e));
		});

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
	}

	public SpatialWriter(Path2D path) {
		gdal.AllRegister();
		// translate path to geometry
		this.geometryList.add(getGeometry(path));

		// setting coordinate system
		outputSpatitalSystem.ImportFromEPSG(WGS84);
	}
	// <=========================================>

	/*
	 * 
	 * 
	 */
	// <==========================================>
	// <output setting>
	// <==========================================>
	public SpatialWriter setAttributeTitle(List<Map<String, Object>> attribute) {
		this.attribute.clear();
		this.attribute = attribute;
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

	public SpatialWriter setField(Map<String, String> type) {
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
	// <===========================================>

	/*
	 * 
	 * 
	 */
	// <===========================================>
	// <private function>
	// <===========================================>
	private Geometry getGeometry(Path2D path) {
		PathIterator temptPathIteratore = path.getPathIterator(null);
		float coordinate[] = new float[2];

		// start coordinate
		temptPathIteratore.currentSegment(coordinate);
		double startX = coordinate[0];
		double startY = coordinate[1];

		// output geometry ,start point
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"Polygon\" , \"coordinates\" : [[");
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

			} else if (type.equals("INT") || type.equals("INTEGER")) {
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
		}

	}
}
