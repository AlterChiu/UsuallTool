package testFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import FEWS.PIXml.AtPiXmlReader;
import FEWS.Rinfall.BUI.BuiTranslate;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class testAtCommon {

	public static void main(String[] args) throws OperationNotSupportedException, IOException, DocumentException {
		// TODO Auto-generated method stub

		// String fileAdd =
		// "S:\\HomeWork\\ICCCBE2018\\DEMUncertainty\\eventRainfall\\201609270900_1H_24H.xml";
		// String saveAdd =
		// "S:\\HomeWork\\ICCCBE2018\\DEMUncertainty\\eventRainfall\\201609270900_1H_24H.BUI";
		//
		// new AtFileWriter(new BuiTranslate(fileAdd).getBuiRainfall() ,
		// saveAdd).textWriter("");

		// get 1D value
		// ==========================================
		// String fileAdd ="C:\\Sobek213\\Output\\0927\\BinHai.xml";
		//// String fileAdd = "C:\\\\Sobek213\\\\Output\\0927\\AhnSung.xml";
		//// String fileAdd = "C:\\\\\\\\Sobek213\\\\\\\\Output\\\0927\\\\\ReinAi.xml";
		// ArrayList<TimeSeriesArray> reader = new
		// AtPiXmlReader().getTimeSeriesArrays(fileAdd);
		//
		// TimeSeriesArray timeSeries = reader.get(0);
		//
		// for(int index = 144 ; index<timeSeries.size()-1;index++) {
		// System.out.println(timeSeries.getValue(index));
		// }

		// getboundary
		// ====================================
		// String fileAdd = "S:\\Users\\alter\\Desktop\\boundary.xml";
		// String saveAdd = "S:\\\\Users\\\\alter\\\\Desktop\\\\save.txt";
		// ArrayList<TimeSeriesArray> reader = new
		// AtPiXmlReader().getTimeSeriesArrays(fileAdd);
		// TimeTranslate tt = new TimeTranslate();
		//
		//
		// TimeSeriesArray timeSeries = reader.get(0);
		// ArrayList<String> temptOut = new ArrayList<String>();
		// for(int index = 0 ; index<timeSeries.size();index++) {
		// String tempt = "";
		// tempt = tempt + "'";
		// tempt = tempt + tt.milliToDate(timeSeries.getTime(index) ,
		// "yyyy/MM/dd;HH;mm;ss");
		// tempt = tempt + "' ";
		// tempt = tempt + timeSeries.getValue(index) + " <";
		//
		// temptOut.add(tempt);
		// }
		// new AtFileWriter(temptOut.parallelStream().toArray(String[]::new) ,
		// saveAdd).textWriter("");

		// getBUI
		// =================================================
		// String fileAdd =
		// "S:\\HomeWork\\ICCCBE2018\\DEMUncertainty\\eventRainfall\\201609270900_1H_24H.xml";
		// String saveAdd =
		// "S:\\HomeWork\\\\ICCCBE2018\\\\DEMUncertainty\\\\eventRainfall\\\\201609270900_1H_48H.BUI";
		//
		// new AtFileWriter(new BuiTranslate(fileAdd).getBuiRainfall_Fill("0.0" , 24) ,
		// saveAdd).textWriter("");

		String fileAdd = "S:\\Users\\alter\\Desktop\\Boundary.xml";

		Map<String, String> nameSpace = new HashMap<String, String>();
		nameSpace.put("np", "");
		//
		SAXReader reader = new SAXReader();
		reader.getDocumentFactory().setXPathNamespaceURIs(nameSpace);

		Document document = reader.read(new File(fileAdd));

		Element root = document.getRootElement();

		System.out.println(root.getNamespaceURI());

	}

}
