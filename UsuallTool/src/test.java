import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiSplit;
import asciiFunction.AsciiToJson;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String[][] U1Zcontent = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc").getStr(1,0);
//		String[][] U2Zcontent = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc").getStr(1,0);
		
//
//		String fileAdd = "C:\\Users\\alter\\Desktop\\海棠易致災分析\\07310700_Output\\UNITALL0002.asc";
//		AsciiToJson geoJson = new AsciiToJson(fileAdd);
//		String tempt = new Gson().toJson(geoJson.getGeoJson(0.05,99));
//		new AtFileWriter(tempt ,  "C:\\Users\\alter\\Desktop\\海棠易致災分析\\07310700_Output\\out_5cm.geoJson").textWriter("");
		
		String save[] = new String[]{ "C:\\Users\\alter\\Desktop\\海棠易致災分析\\ASCII\\01.asc" , "C:\\Users\\alter\\Desktop\\海棠易致災分析\\ASCII\\02.asc"};
		String fileAdd = "C:\\Users\\alter\\Desktop\\海棠易致災分析\\ASCII\\0700.asc";
		AsciiSplit asciiSplit = new AsciiSplit(fileAdd);
		
		ArrayList<String[][]> asciiSplitOut = asciiSplit.getSplitAsciiByEqualCut(2);
		for(int i=0;i<asciiSplitOut.size();i++){
			new AtFileWriter(asciiSplitOut.get(i) , save[i]).textWriter("    ");
		}
		
	}

}
