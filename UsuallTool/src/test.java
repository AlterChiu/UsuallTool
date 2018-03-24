import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import FEWS.Rinfall.BUI.BuiTranslate;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiGridChange;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiSplit;
import asciiFunction.AsciiToJson;
import usualTool.AtFileWriter;


public class test {

	public static void main(String[] args) throws IOException, OperationNotSupportedException, ParseException {
		// TODO Auto-generated method stub

//		
//		String originalDem = "S:\\Users\\alter\\Desktop\\testRainfall.xml";
//		String target = "S:\\Users\\alter\\Desktop\\testRainfall.BUI";
//		
//		
//		BuiTranslate bui = new BuiTranslate(originalDem);
//		new AtFileWriter(bui.getBuiRainfall() , target).textWriter("");;
		
		
		String filePath = "S:\\Users\\alter\\Desktop\\test.json";
		JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(filePath));
        JsonObject  jsonObject = jsonElement.getAsJsonObject();
		 
		
		System.out.println(jsonObject.get("test").getAsString());
	
	}
}
