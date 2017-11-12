import java.io.IOException;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[][] U1content = new AsciiBasicControl("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1.asc").cutFirstColumn().getAsciiFile();
		String[][] U2content = new AsciiBasicControl("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2.asc").cutFirstColumn().getAsciiFile();
		
		
		
		String tempt[][] = new AtFileReader("C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2.asc").getStr();
		System.out.println(tempt[6].length);
		
		String[][] temptout = new AsciiMerge(U1content,U2content).getMergedAscii();
		new AtFileWriter(temptout , "C:\\Users\\alter\\Desktop\\山峰可可\\97Zone1").textWriter("    ");
		
		
		
	}

}
