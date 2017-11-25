package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AsciiToJson {
	private String[][] asciiContent;
	private TreeMap<String, String> asciiProperty;

	public AsciiToJson(String[][] asciiFile) throws IOException {
		AsciiBasicControl ascii = new AsciiBasicControl(asciiFile).cutFirstColumn();
		this.asciiContent = ascii.getAsciiGrid();
		this.asciiProperty = ascii.getProperty();
	}

	public AsciiToJson(String fileAdd) throws IOException {
		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd).cutFirstColumn();
		this.asciiContent = ascii.getAsciiGrid();
		this.asciiProperty = ascii.getProperty();
	}

	
//	<=============================>
//	< getting the GeoJson by the setting asciiFile>
//	<==============================>
	public JsonObject getGeoJson(double base , double top) {
//		return the geoJson 
//		_____________________________________________________________________________
		JsonObject outJson = new JsonObject();
		outJson.addProperty("type", "FeatureCollection");

//		Basic setting
//		____________________________________________________________________________
		double startX = Double.parseDouble(this.asciiProperty.get("bottomX"));
		double startY = Double.parseDouble(this.asciiProperty.get("topY"));
		double cellSize = Double.parseDouble(this.asciiProperty.get("cellSize"));
		String noData = this.asciiProperty.get("noData");

		JsonArray shpArray = new JsonArray();
//		<reading the asciiContent if the value isn't equals to the noData value>
//		________________________________________________________________________________
		for (int line = 0; line < this.asciiContent.length; line++) {
			double temptY = new BigDecimal(startY - line * cellSize)
					.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
			for (int column = 0; column < this.asciiContent[0].length; column++) {
				
				
				if (!this.asciiContent[line][column].equals(noData) ) {
					double value = Double.parseDouble(this.asciiContent[line][column]) ;
					if(value>=base && value < top){
						double temptX = new BigDecimal(startX + column * cellSize)
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();

						double xP = new BigDecimal(temptX + 0.5 * cellSize)
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
						double xN = new BigDecimal(temptX - 0.5 * cellSize)
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
						double yP = new BigDecimal(temptY + 0.5 * cellSize)
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
						double yN = new BigDecimal(temptY - 0.5 * cellSize)
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						shpArray.add(getSingleGrid(xP,xN,yP,yN,value));
					}
				}
			}
		}
		outJson.add("features", shpArray);
		return outJson;
	}
	
	
	
	
//	<=============================>
//	< getting the GeoJson by the setting asciiFile>
//	<==============================>
	public JsonObject getGeoJson() {
//		return the geoJson 
//		_____________________________________________________________________________
		JsonObject outJson = new JsonObject();
		outJson.addProperty("type", "FeatureCollection");

//		Basic setting
//		____________________________________________________________________________
		double startX = Double.parseDouble(this.asciiProperty.get("bottomX"));
		double startY = Double.parseDouble(this.asciiProperty.get("topY"));
		double cellSize = Double.parseDouble(this.asciiProperty.get("cellSize"));
		String noData = this.asciiProperty.get("noData");

		JsonArray shpArray = new JsonArray();
//		<reading the asciiContent if the value isn't equals to the noData value>
//		________________________________________________________________________________
		for (int line = 0; line < this.asciiContent.length; line++) {
			double temptY = new BigDecimal(startY - line * cellSize)
					.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
			for (int column = 0; column < this.asciiContent[0].length; column++) {
				
				
				if (!this.asciiContent[line][column].equals(noData)) {
					double temptX = new BigDecimal(startX + column * cellSize)
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();

					double xP = new BigDecimal(temptX + 0.5 * cellSize)
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
					double xN = new BigDecimal(temptX - 0.5 * cellSize)
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
					double yP = new BigDecimal(temptY + 0.5 * cellSize)
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
					double yN = new BigDecimal(temptY - 0.5 * cellSize)
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
					
					shpArray.add(getSingleGrid(xP,xN,yP,yN,Double.parseDouble(this.asciiContent[line][column])));
				}
			}
		}
		outJson.add("features", shpArray);
		return outJson;
	}
	
	
	public String getGeoJsonInString(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(getGeoJson());
	}

	private JsonObject getSingleGrid(double xP, double xN, double yP, double yN, double value) {
		// <return the shpFile grid in geoJson>
		// _________________________________________________________________________________________________
		JsonObject returnJsonObject = new JsonObject();
		returnJsonObject.addProperty("type", "Feature");

		// <each grid property >
		// ________________________________________________________________________________________________
		JsonObject propertyObject = new JsonObject();
		propertyObject.addProperty("value", value);

		// <each grid coordinate>
		// ___________________________________________________________________________________________________
		ArrayList<String> temptArray = new ArrayList<String>();
		temptArray.add("[" + xN + "," + yN + "]");
		temptArray.add("[" + xN + "," + yP + "]");
		temptArray.add("[" + xP + "," + yP + "]");
		temptArray.add("[" + xP + "," + yN + "]");

		JsonArray cordinateArray = (JsonArray) new JsonParser().parse("[[[" + String.join(",", temptArray) + "]]]");

		JsonObject geoObject = new JsonObject();
		geoObject.addProperty("type", "MultiPolygon");
		geoObject.add("coordinates", cordinateArray);

		// <combin the jsonObject>
		// _______________________________________________________________________________________________________
		returnJsonObject.add("properties", propertyObject);
		returnJsonObject.add("geometry", geoObject);

		return returnJsonObject;
	}

}
