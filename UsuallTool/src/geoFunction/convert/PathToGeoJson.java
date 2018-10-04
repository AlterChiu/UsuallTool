package geoFunction.convert;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import usualTool.AtFileWriter;

public class PathToGeoJson {
	private List<Path2D> pathList;
	private List<Map<String, String>> attribute = new ArrayList<Map<String, String>>();

	private String selectedCoordinateSystem;
	public final String WGS84 = "urn:ogc:def:crs:EPSG::3826";
	public final String TWD97 = "urn:ogc:def:crs:OGC:1.3:CRS84";
	private String jsonName = "temptGeoJson";

	private JsonObject outJson;

	public PathToGeoJson(List<Path2D> pathList) {
		this.pathList = pathList;
		this.selectedCoordinateSystem = this.WGS84;
	}

	public PathToGeoJson(Path2D path) {
		this.selectedCoordinateSystem = this.WGS84;
		this.pathList = new ArrayList<Path2D>();
		this.pathList.add(path);
	}

	public void setAttributeTitle(List<Map<String, String>> attribute) {
		this.attribute.clear();
		this.attribute = attribute;
	}

	public void setJsonName(String name) {
		this.jsonName = name;
	}

	public void setCoordinateSystem(String system) {
		this.selectedCoordinateSystem = system;
	}

	public void saveGeoJson(String saveAdd) throws IOException {
		clearJson();
		this.outJson = this.startJson();
		JsonArray featureArray = new JsonArray();

		for (int index = 0; index < this.pathList.size(); index++) {
			// set title
			JsonObject featureObject = new JsonObject();
			featureObject.addProperty("type", "Feature");

			// set attribute
			JsonObject propertiesObject = new JsonObject();
			try {
				Map<String, String> temptMap = this.attribute.get(index);
				for (String key : temptMap.keySet()) {
					propertiesObject.addProperty(key, temptMap.get(key));
				}
			} catch (Exception e) {
			}

			// set geo
			JsonObject geometryObject = new JsonObject();
			geometryObject.addProperty("type", "Polygon");

			// set path
			JsonArray geoArray1 = new JsonArray();
			JsonArray geoArray2 = new JsonArray();

			final float[] coords = new float[2];
			PathIterator it = this.pathList.get(index).getPathIterator(null);
			for (; !it.isDone(); it.next()) {
				JsonArray temptArray = new JsonArray();
				int type = it.currentSegment(coords);

				temptArray.add(new JsonParser().parse(coords[0] + ""));
				temptArray.add(new JsonParser().parse(coords[1] + ""));
				geoArray2.add(temptArray);
			}

			// re package
			geoArray1.add(geoArray2);
			geometryObject.add("coordinates", geoArray1);
			featureObject.add("properties", propertiesObject);
			featureObject.add("geometry", geometryObject);
			featureArray.add(featureObject);
		}

		this.outJson.add("features", featureArray);
		new AtFileWriter(this.outJson, saveAdd).textWriter("");
	}

	private void clearJson() {
		try {
			this.outJson.entrySet().forEach(enty -> this.outJson.remove(enty.getKey()));
		} catch (Exception e) {
		}
	}

	private JsonObject startJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "FeatureCollection");
		json.addProperty("name", this.jsonName);

		JsonObject coordinateSystem = new JsonObject();
		coordinateSystem.addProperty("name", this.selectedCoordinateSystem);

		JsonObject crs = new JsonObject();
		crs.addProperty("type", "name");
		crs.add("properties", coordinateSystem);

		json.add("crs", crs);

		return json;
	}

}
