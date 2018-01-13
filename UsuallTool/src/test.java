import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import FEWS.Rinfall.BUI.BuiTranslate;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import usualTool.AtFileWriter;


public class test {

	public static void main(String[] args) throws IOException, OperationNotSupportedException, ParseException {
		// TODO Auto-generated method stub

//		String fileAdd = "C:\\mapReduce\\asciiDifference\\";
//		
//		String original = fileAdd + "20171209_BUI\\dm1d0000.asc";
//		String target = fileAdd + "20171209_XML\\dm1d0002.asc";
//		
//		AsciiDifference dif = new AsciiDifference(original,target);
//		
//		System.out.println("combin\t:\t" + dif.getCombine());
//		System.out.println("Intersect\t:\t" + dif.getIntersect());
//		System.out.println("Persentage\t:\t" + dif.getPersentage());
//		System.out.println("difference\t:\t" + dif.getDifferenceTotal());
//		
	String fileAdd = "C:\\HomeWork\\測量實習\\乙班程式\\導線\\";
	ArrayList<String> fileList = new ArrayList<String>(Arrays.asList(new File(fileAdd).list()));
	
	fileList.forEach(e -> System.out.println(e.substring(0, 9)));
	 
		
		
		
//		
//		String tempt = "0.64 2.13 2.89 2.63 1.29 2.71 1.67 2.11 1.91 2.95 1.81 1.47 2.31 2.72 2.84 1.86 2.21 2.15 2.99 2.02 1.92 2.4 2.79 2.19 2.06 1.67 1.58 1.52 2.65 2.45 2.53 2.93 1.65 1.68 2.21 1.42 2.61 1.83 1.88 1.83 2.22 2.24 2.96 0.93 1.97 2.19 2.97 2.94 3.02 1.99 2.63 1.53 2.56 0.83 0.89 2.46 2.17 2.63 2.95 1.52 2.83 3.11 0.66 1.94 1.34 1.44 0.65 2.2 2.06 2.34 1.97 1.76 1.41 1.95 2.16 1.42 2.25 1.97 2.26 1.22 2.03 2.0 1.99 2.21 1.71 1.23 0.95 1.22 1.57 2.15 1.29 1.25 1.16 1.96 1.14 1.93 1.17 0.65 1.33 1.15 2.25 0.84 1.78 2.58 0.87 0.67 1.15 1.09 2.36 0.62 2.55 1.04 2.12 1.01 0.62 1.3 1.98 1.36 1.31 1.54 0.42 2.0 1.22 2.02 1.31 1.96 1.0 1.75 0.72 2.09 0.46 0.55 2.0 1.3 1.85 0.7 2.0 0.5 1.64 0.72 1.35 1.38 1.48 1.91 1.34 0.66 1.63 2.0 0.43 2.02 0.48 1.51 1.34 0.74 1.68 1.95 1.05 2.03 1.53 2.1 1.8 0.89 1.27 0.85 1.79 0.5 0.39 1.57 0.61 1.49 1.92 1.22 0.55 1.6 2.13 1.57 1.37 1.08 1.7 0.9 0.33 0.47 0.48 0.61 1.63 0.85 1.01 1.01 0.57 1.71 2.03 2.41 1.33 0.79 1.03 1.71 0.78 2.91 0.58 1.49 1.62 0.45 1.34 0.56 2.87 0.57 0.8 0.56 1.75 0.52 1.55 1.56 0.49 0.48 1.56 1.16 0.96 1.35 0.82 0.54 1.04 1.09 1.05 0.62 1.16 1.01 1.08 1.36 0.87 0.88 0.33 1.04 1.51 0.83 1.37 1.52 0.88 0.96 0.9 0.52 0.34 0.42 1.37 1.18 1.27 0.46 1.08 1.44 1.12 1.27 1.23 0.39 1.3 1.36 0.3 1.18 0.15 1.34 1.21 0.28 1.43 0.56 1.15 1.1 1.25 1.21 1.24 1.14 1.21 0.9 1.29 1.21 1.25 1.15 0.1 1.33 0.44 1.12 1.5 1.65 1.8 1.12 1.0 1.08 1.19 1.18 1.0 2.21 2.02 1.51 2.08 1.45 1.08 0.68 1.19 2.17 1.68 2.27 2.0 1.98 1.38 2.33 0.99 2.05 1.16 1.67 1.58 1.29 0.98 0.8 1.47 1.2 1.09 1.15 0.92 0.92 1.38 1.42 1.88 1.51 1.8 1.93 1.61 1.46 1.0 1.09 2.08 2.11 2.08 1.09 1.03 0.46 1.19 1.24 0.59 0.45 1.36 1.55 1.5 1.53 1.54 1.51 1.55 1.67 1.99 1.92 1.59 1.87 1.69 1.83 1.63 1.53 1.36 1.64 0.52 1.69 1.46 0.62 0.58 0.69 0.63 0.67 1.21 0.6 1.38 0.92 0.93 0.99 1.2 2.61 1.08 1.16 1.14 1.0 2.92 1.73 1.0 1.1 1.19 1.03 1.21 1.29 2.8 2.75 2.75 2.5 1.71 1.0 1.14 2.69 1.06 3.0 2.98 0.92 0.86 1.08 0.81 1.46 0.96 0.75 0.66 0.66 0.83 0.63 0.63 1.38 1.11 0.75 1.89 1.96 0.72 0.81 0.75 0.82 0.79 0.67 0.69 0.67 1.41 1.87 1.43 0.75 0.82 0.68 0.68 1.39 0.87 0.86 0.71 0.77 1.65 2.54 1.05 0.7 0.95 0.95 0.67 0.67 1.41 1.8 0.79 2.33 1.38 0.9 0.33 1.29 0.46 0.38 0.7 0.42 0.49 0.46 0.48 1.22 1.27 0.48 1.84 1.62 1.81 1.08 0.58 0.5 1.93 1.37 1.62 0.82 0.45 1.42 1.06 0.99 0.92 0.92 0.47 0.81 0.7 0.69 0.83 0.84 1.09 0.96 0.61 0.65 1.0 0.9 0.86 2.05 0.82 1.51 2.13 0.86 0.91 2.57 1.48 1.03 1.57 0.86 0.86 0.47 1.05 0.88 1.03 2.15 0.74 0.97 1.27 0.88 0.58 0.64 1.91 2.44 1.47 1.85 1.97 0.64 0.84 1.92 0.64 1.88 1.92 0.94 2.33 0.87 2.29 0.81 1.81 1.0 1.17 2.3 1.44 1.39 2.49 2.28 1.56 1.58 1.0 1.26 1.26 1.85 2.84 2.65 1.12 1.63 2.33 1.26 2.73 2.88 2.92 1.63 2.08 1.02 1.63 1.05 1.29";
//		System.out.println(tempt.trim().split(" +").length);
		
		
		// String fileAdd = "C:\\Users\\alter\\Desktop\\sobekDEM\\original\\";
		//
		// String U1Ascii =
		// "97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc";
		// String U2Ascii =
		// "97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc";
		//
		// String outPutFile =
		// "C:\\Users\\alter\\Desktop\\sobekDEM\\export\\ZoneOne_20m_total(kn).asc";
		//
		//
		// String[][] outAscii = new AsciiMerge(fileAdd + U1Ascii , fileAdd +
		// U2Ascii).getMergedAscii();
		// new AtFileWriter(outAscii , outPutFile).textWriter(" ");
		//
	}
}
