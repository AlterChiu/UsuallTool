import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiIntersect;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiSplit;
import asciiFunction.AsciiToJson;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileAdd = "C:\\Users\\alter\\Desktop\\海棠易致災分析\\test\\";
		String asciiName[] = new String[]{"0700.asc" ,"0800.asc" , "0900.asc" , "1000.asc" , "1100.asc"};
		ArrayList<String[][]> asciiContent = new ArrayList<String[][]>();
		
		for(String name : asciiName){
			asciiContent.add(new AsciiBasicControl(fileAdd + name).cutFirstColumn().getAsciiFile());
		}
		
		
		AsciiIntersect intersect = new AsciiIntersect(asciiContent , fileAdd + "vallage.geojson" );
		
		new AtFileWriter(intersect.getSeriesJsonObject() , fileAdd + "out.geoJson").textWriter("");
		
		
	}

}
