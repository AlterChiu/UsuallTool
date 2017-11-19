import java.io.IOException;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String[][] U1Zcontent = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc").getStr(1,0);
//		String[][] U2Zcontent = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc").getStr(1,0);
		
		String U1Zcontent = "C:\\Users\\alter\\Desktop\\sobekDEM\\original\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc";
		String U2Zcontent = "C:\\Users\\alter\\Desktop\\sobekDEM\\original\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc";
		
		String[][] ascii1  = new AsciiBasicControl(U1Zcontent).cutFirstColumn().getAsciiFile();
		String[][] ascii2 = new AsciiBasicControl(U2Zcontent).cutFirstColumn().getAsciiFile();
		
		
		String[][] temptout = new AsciiMerge(ascii1,ascii2).getMergedAscii();
		new AtFileWriter(temptout , "C:\\Users\\alter\\Desktop\\sobekDEM\\export\\97Zone1(20mDEM_total)(kn).asc").textWriter("    ");
		
		
		
	}

}
