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
		// String[][] U1Zcontent = new
		// AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc").getStr(1,0);
		// String[][] U2Zcontent = new
		// AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc").getStr(1,0);

		//
		// String fileAdd =
		// "C:\\Users\\alter\\Desktop\\海棠易致災分析\\07310700_Output\\UNITALL0002.asc";
		// AsciiToJson geoJson = new AsciiToJson(fileAdd);
		// String tempt = new Gson().toJson(geoJson.getGeoJson(0.05,99));
		// new AtFileWriter(tempt ,
		// "C:\\Users\\alter\\Desktop\\海棠易致災分析\\07310700_Output\\out_5cm.geoJson").textWriter("");

		String readFileName[] = new String[] { "0800", "0900", "1000", "1100" };

		String fileAdd = "C:\\Users\\alter\\Desktop\\海棠易致災分析\\ASCII\\";

//		String outGrid[][] = new String[1275][1371];
//		for (int line = 0; line < outGrid.length; line++) {
//			for (int column = 0; column < outGrid[0].length; column++) {
//				outGrid[line][column] = "-999";
//			}
//		}
//		for (int i = 1; i < readFileName.length; i++) {
//			String[][] firstContent = new AsciiBasicControl(fileAdd + readFileName[i - 1] + ".asc").cutFirstColumn()
//					.getAsciiGrid();
//			String[][] secondContent = new AsciiBasicControl(fileAdd + readFileName[i] + ".asc").cutFirstColumn()
//					.getAsciiGrid();
//
//			for (int line = 0; line < firstContent.length; line++) {
//				for (int column = 0; column < firstContent[0].length; column++) {
//					if (Double.parseDouble(firstContent[line][column]) >= 0.05
//							&& Double.parseDouble(secondContent[line][column]) >= 0.05) {
//						outGrid[line][column] = "100";
//					}
//				}
//			}
//		}
//		new Gson().toJson(new AsciiToJson(outGrid).getGeoJson())
//		new AtFileWriter(new Gson().toJson(new AsciiToJson(new AtFileReader(fileAdd + "continuous.asc").getStr()).getGeoJson()), fileAdd + "continuous.geoJson").textWriter("    ");
		
		String[][] basic = new AsciiBasicControl(fileAdd + "0700.asc").cutFirstColumn().getAsciiFile(0.05, 999);
		for(String name :readFileName){
			String target[][] = new AsciiBasicControl(fileAdd + name + ".asc").cutFirstColumn().getAsciiFile(0.05,999);
			basic = new AsciiMerge(basic , target).getMergedAscii();
		}
		new AtFileWriter(new Gson().toJson(new AsciiToJson(basic).getGeoJson()) ,  fileAdd + "total.geoJson").textWriter("    ");
		
	}

}
