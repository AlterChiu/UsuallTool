import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import asciiFunction.AsciiMerge;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException, OperationNotSupportedException {
		// TODO Auto-generated method stub
		String fileAdd = "C:\\Users\\alter\\Desktop\\sobekDEM\\original\\";
		
		String U1Ascii = "97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit1(kn).asc";
		String U2Ascii = "97Zone1(20mDEM_200YmaxNodata)(mhby10)-Unit2(kn).asc";
		
		String outPutFile = "C:\\Users\\alter\\Desktop\\sobekDEM\\export\\ZoneOne_20m_total(kn).asc";
		
		
		String[][] outAscii = new AsciiMerge(fileAdd + U1Ascii , fileAdd + U2Ascii).getMergedAscii();
		new AtFileWriter(outAscii , outPutFile).textWriter("    ");
		
	}
}
