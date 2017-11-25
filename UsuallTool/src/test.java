import java.io.IOException;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiToJson;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String[][] U1Zcontent = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc").getStr(1,0);
//		String[][] U2Zcontent = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc").getStr(1,0);
		

		String fileAdd = "C:\\Users\\alter\\Desktop\\海棠易致災分析\\07310700_Output\\UNITALL0002.asc";
		AsciiToJson geoJson = new AsciiToJson(fileAdd);
		new AtFileWriter(geoJson.getGeoJsonInString() ,  "C:\\Users\\alter\\Desktop\\海棠易致災分析\\07310700_Output\\out.geoJson")
		
		
		
	}

}
