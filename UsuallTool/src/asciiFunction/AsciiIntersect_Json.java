package asciiFunction;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;

public class AsciiIntersect_Json {
	private AsciiBasicControl ascii;
	private JsonObject geoJson;
	private Map<String, String> property;
	private List<AsciiBasicControl> asciiFileList = new ArrayList<AsciiBasicControl>();;

	// <===============================>
	// <this is the constructor and the geoJson setting>
	// <===============================>
	public AsciiIntersect_Json(JsonObject geojson) throws IOException {
		this.geoJson = geojson;
	}

	public AsciiIntersect_Json(String jsonAdd) throws IOException {
		this.geoJson = new AtFileReader(jsonAdd).getJsonObject();
	}

	// <===================================================== >
	// <get the meanValue and mount of grid that inside the each features of geoJson
	// >
	// <===================================================== >
	public JsonObject getIntersectGeoJson(AsciiBasicControl ascii) {
		// setting ascii
		this.ascii = ascii;
		this.property = ascii.getProperty();

		// clone the geoJson
		JsonObject outJson = new Gson().fromJson(this.geoJson, JsonObject.class);
		JsonArray features = outJson.get("features").getAsJsonArray();

		// get each features
		for (JsonElement feature : features) {
			JsonObject featureProperty = feature.getAsJsonObject().get("properties").getAsJsonObject();

			JsonArray coordinateList = feature.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates")
					.getAsJsonArray().get(0).getAsJsonArray().get(0).getAsJsonArray();

			Path2D temptPath = getJsonPolygon(coordinateList);
			ArrayList<Double> intersectValue = getIntersectValue(temptPath);
			try {
				featureProperty.addProperty("averageValue", new AtCommonMath(intersectValue).getMean());
			} catch (Exception e) {
				featureProperty.addProperty("averageValue", 0.0);
			}
			try {
				featureProperty.addProperty("mountOfGrid", intersectValue.size());
			} catch (Exception e) {
				featureProperty.addProperty("mountOfGrid", (int) 0);
			}

		}
		return outJson;
	}

	public List<AsciiBasicControl> getIntersectAscii(AsciiBasicControl ascii) throws IOException {
		// setting ascii
		this.ascii = ascii;
		this.property = ascii.getProperty();
		List<AsciiBasicControl> outAsciiList = new ArrayList<AsciiBasicControl>();

		// get each features
		for (JsonElement feature : this.geoJson.get("features").getAsJsonArray()) {

			// get the each polygons in json file
			JsonArray coordinateList = feature.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates")
					.getAsJsonArray().get(0).getAsJsonArray().get(0).getAsJsonArray();

			// make the polygons to path object
			Path2D temptPath = getJsonPolygon(coordinateList);
			Rectangle2D temptBound = temptPath.getBounds2D();
			double maxY = temptBound.getMaxY();
			double maxX = temptBound.getMaxX();
			double minX = temptBound.getMinX();
			double minY = temptBound.getMinY();

			outAsciiList.add(ascii.getClipAsciiFile(minX, minY, maxX, maxY));

		}
		return outAsciiList;
	}

	// <====================================>
	// <Get the series Ascii file clip by the GeoJson>
	// <====================================>
	public List<List<AsciiBasicControl>> getSeriesintersectAscii(List<AsciiBasicControl> asciiList) throws IOException {
		List<List<AsciiBasicControl>> outList = new ArrayList<List<AsciiBasicControl>>();
		for (JsonElement feature : this.geoJson.get("features").getAsJsonArray()) {

			// get the each polygons in json file
			JsonArray coordinateList = feature.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates")
					.getAsJsonArray().get(0).getAsJsonArray().get(0).getAsJsonArray();

			// make the polygons to path object
			Path2D temptPath = getJsonPolygon(coordinateList);
			Rectangle2D temptBound = temptPath.getBounds2D();
			double maxY = temptBound.getMaxY();
			double maxX = temptBound.getMaxX();
			double minX = temptBound.getMinX();
			double minY = temptBound.getMinY();

			// get each ascii
			List<AsciiBasicControl> featureAscii = new ArrayList<AsciiBasicControl>();
			for (AsciiBasicControl temptAscii : asciiList) {

				// setting the ascii
				this.ascii = temptAscii;
				this.property = temptAscii.getProperty();
				featureAscii.add(temptAscii.getClipAsciiFile(minX, minY, maxX, maxY));
			}
			outList.add(featureAscii);
		}
		return outList;
	}

	// <=============================================>
	// <Get the series Json>
	// <=============================================>
	public JsonObject getSeriesintersectJsonObject() {
		JsonObject outJson = new Gson().fromJson(this.geoJson, JsonObject.class);
		for (int order = 0; order < asciiFileList.size(); order++) {
			this.ascii = asciiFileList.get(order);
			this.property = this.ascii.getProperty();

			outJson = getIntersectGeoJson(order, outJson);
		}
		return outJson;
	}

	// <for series function>
	// <_________________________________________________>
	private JsonObject getIntersectGeoJson(int order, JsonObject jsonObject) {
		JsonArray features = jsonObject.get("features").getAsJsonArray();

		for (JsonElement feature : features) {
			JsonObject featureProperty = feature.getAsJsonObject().get("properties").getAsJsonObject();

			JsonArray coordinateList = feature.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates")
					.getAsJsonArray().get(0).getAsJsonArray().get(0).getAsJsonArray();

			Path2D temptPath = getJsonPolygon(coordinateList);
			ArrayList<Double> intersectValue = getIntersectValue(temptPath);
			AtCommonMath commonMath = new AtCommonMath(intersectValue);
			try {
				featureProperty.addProperty("averageValue_" + order, commonMath.getMean());
			} catch (Exception e) {
				featureProperty.addProperty("averageValue_" + order, 0.0);
			}
			try {
				featureProperty.addProperty("maxValue_" + order, commonMath.getMax());
			} catch (Exception e) {
				featureProperty.addProperty("maxValue_" + order, 0.0);
			}
			try {
				featureProperty.addProperty("minValue_" + order, commonMath.getMin());
			} catch (Exception e) {
				featureProperty.addProperty("minValue_" + order, 0.0);
			}
			try {
				featureProperty.addProperty("mountOfGrid_" + order, intersectValue.size());
			} catch (Exception e) {
				featureProperty.addProperty("mountOfGrid_" + order, (int) 0);
			}

		}
		return jsonObject;
	}

	// <=====================>
	// <PRIVATE CACULATE FUNCTION>
	// <=====================>

	// <drawing the 2d polygon>
	// <_______________________________________________________________________________________>
	private Path2D getJsonPolygon(JsonArray coordinateList) {
		Path2D temptPath = new Path2D.Double();
		// get the startPoint
		JsonArray firstCoordinate = coordinateList.get(0).getAsJsonArray();
		temptPath.moveTo(firstCoordinate.get(0).getAsDouble(), firstCoordinate.get(1).getAsDouble());

		// make polygon
		for (int order = 1; order < coordinateList.size(); order++) {
			JsonArray temptCoordinate = coordinateList.get(order).getAsJsonArray();
			temptPath.lineTo(temptCoordinate.get(0).getAsDouble(), temptCoordinate.get(1).getAsDouble());
		}
		return temptPath;
	}

	// <=================================>
	// <get the value inside the each features of geoJson >
	// <=================================>
	private ArrayList<Double> getIntersectValue(Path2D polygon) {
		// <setting the path (polygong of feature)>
		// <and get the bound (rectangle) of the feature>
		// <_____________________________________________________________________>
		Rectangle2D temptBound = polygon.getBounds2D();
		double maxY = temptBound.getMaxY();
		double maxX = temptBound.getMaxX();
		double minX = temptBound.getMinX();
		double minY = temptBound.getMinY();

		// getting the boundary of the asciiGrid by its position
		// to make sure the coordinate is fitting to the asciiGrid
		// <___________________________________________________________________________________________________>
		String[][] content = this.ascii.getAsciiGrid();
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		double cellSize = Double.parseDouble(this.property.get("cellSize"));

		String cellSizeString = this.property.get("noData");

		int[] startPosition = this.ascii.getPosition(minX, maxY); // leftTop
																	// <column ,
																	// row >
		int[] endPosition = this.ascii.getPosition(maxX, minY);// rightBottom
																// <column , row
																// >

		// <get the value and mount of asciiGrid that inside in the pokygon>
		// <______________________________________________________________________>
		ArrayList<Double> temptArray = new ArrayList<Double>();
		for (int line = startPosition[1]; line < endPosition[1]; line++) {

			// <setting the coordinate of y>
			double temptCoordinateY = startY - line * cellSize;
			for (int column = startPosition[0]; column < endPosition[0]; column++) {

				// <setting the coordinate of X>
				double temptCoordinateX = startX + column * cellSize;

				// point inside the polygon
				if (polygon.contains(temptCoordinateX, temptCoordinateY)) {
					try {
						String temptValue = content[line][column];

						// value don't equals to the noDate value
						if (!temptValue.equals(cellSizeString)
								&& Double.parseDouble(temptValue) >= globalAscii.floodedDepth) {
							temptArray.add(Double.parseDouble(temptValue));
						}
					} catch (Exception e) {
					}
				}
			}
		}
		return temptArray;
	}

}
