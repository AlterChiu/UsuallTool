package asciiFunction;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import usualTool.AtCommonMath;


public class AsciiIntersect {
	private String[][] asciiFile;
	private AsciiBasicControl ascii;
	private JsonObject geoJson;
	private TreeMap<String, String> property;
	private String[][] asciiGrid;
	private JsonObject seriesJsonObject;

	// <===============================>
	// <this is the constructor and the geoJson setting>
	// <===============================>
	public AsciiIntersect(String[][] asciiFile) throws IOException {
		this.asciiFile = asciiFile;
		this.ascii = new AsciiBasicControl(this.asciiFile);
		this.property = this.ascii.getProperty();
		this.asciiGrid = this.ascii.getAsciiGrid();
	}

	public AsciiIntersect(String fileAdd) throws IOException {
		this.asciiFile = new AsciiBasicControl(fileAdd).getAsciiFile();
		this.ascii = new AsciiBasicControl(this.asciiFile);
		this.property = this.ascii.getProperty();
		this.asciiGrid = this.ascii.getAsciiGrid();
	}

	public AsciiIntersect settingGeoJson(JsonObject geoJson) {
		this.geoJson = geoJson;
		return this;
	}

	public AsciiIntersect settingGeoJson(String fileAdd)
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.geoJson = new JsonParser().parse(new FileReader(fileAdd)).getAsJsonObject();
		return this;
	}
	
	
	
//	<===================== >
//	<Series Ascii intersect to geoJson>
//	<===================== >
	public AsciiIntersect(ArrayList<String[][]> asciiFile, JsonObject json) throws IOException {
		this.seriesJsonObject = json;

		for (int order = 0; order < asciiFile.size(); order++) {
			this.asciiFile = asciiFile.get(order);
			this.ascii = new AsciiBasicControl(this.asciiFile);
			this.property = this.ascii.getProperty();
			this.asciiGrid = this.ascii.getAsciiGrid();

			this.seriesJsonObject = getIntersectGeoJson(order, this.seriesJsonObject);
		}
	}

	public AsciiIntersect(ArrayList<String[][]> asciiFile, String jsonFileAdd)
			throws JsonIOException, JsonSyntaxException, IOException {
		this.seriesJsonObject = this.geoJson = new JsonParser().parse(new FileReader(jsonFileAdd)).getAsJsonObject();

		for (int order = 0; order < asciiFile.size(); order++) {
			this.asciiFile = asciiFile.get(order);
			this.ascii = new AsciiBasicControl(this.asciiFile);
			this.property = this.ascii.getProperty();
			this.asciiGrid = this.ascii.getAsciiGrid();

			this.seriesJsonObject = getIntersectGeoJson(order, this.seriesJsonObject);
		}
	}


	// <===================================================== >
	// <get the meanValue and mount of grid that inside the each features of  geoJson >
	// <===================================================== >
	public JsonObject getIntersectGeoJson() {
		JsonArray features = this.geoJson.get("features").getAsJsonArray();

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
		return this.geoJson;
	}
	

	
	
	
	
//	<for series function>
//	<_________________________________________________>
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

	
	public JsonObject getSeriesJsonObject(){
		return this.seriesJsonObject;
	}
	
	
	
	
	
	
	
	
	
//	<=====================>
//	<PRIVATE CACULATE FUNCTION>
//	<=====================>
	
	

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
						String temptValue = this.asciiGrid[line][column];

						// value don't equals to the noDate value
						if (!temptValue.equals(cellSizeString) && Double.parseDouble(temptValue)>=globalAscii.floodedDepth) {
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
