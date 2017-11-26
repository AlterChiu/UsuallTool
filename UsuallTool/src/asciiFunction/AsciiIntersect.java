package asciiFunction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import usualTool.AtFileReader;

public class AsciiIntersect {
	private String[][] asciiFile;
	private JsonObject geoJson ;
	
	
//	<===============================>
//	<this is the constructor and the geoJson setting>
//	<===============================>
	public AsciiIntersect(String[][] asciiFile){
		this.asciiFile = asciiFile;
	}
	
	public AsciiIntersect(String fileAdd) throws IOException{
		this.asciiFile = new AsciiBasicControl(fileAdd).cutFirstColumn().getAsciiFile();
	}
	
	public AsciiIntersect settingGeoJson(JsonObject geoJson){
		this.geoJson = geoJson;
		return this;
	}
	public AsciiIntersect settingGeoJson(String fileAdd) throws JsonIOException, JsonSyntaxException, FileNotFoundException{
		this.geoJson = new JsonParser().parse(new FileReader(fileAdd)).getAsJsonObject();
		return this;
	}

}
