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
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiBuffer;
import asciiFunction.AsciiMerge;
import asciiFunction.XYZToAscii;
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
//		String fileAdd = "S:\\HomeWork\\ICCCBE2018\\DEMUncertainty\\eventRainfall\\201606110000_1H_24H.xml";
//		String saveAdd = "S:\\HomeWork\\\\ICCCBE2018\\\\DEMUncertainty\\\\eventRainfall\\\\201606110000_1H_48H.BUI";
//
////		new AtFileWriter(new BuiTranslate(fileAdd).getBuiRainfall(), saveAdd).textWriter("");
//		new AtFileWriter(new BuiTranslate(fileAdd).getBuiRainfall_Fill("0.0", 24), saveAdd).textWriter("");

		// String fileAdd = "S:\\Users\\alter\\Desktop\\Boundary.xml";
		//
		// Map<String, String> nameSpace = new HashMap<String, String>();
		// nameSpace.put("np", "");
		// //
		// SAXReader reader = new SAXReader();
		// reader.getDocumentFactory().setXPathNamespaceURIs(nameSpace);
		//
		// Document document = reader.read(new File(fileAdd));
		//
		// Element root = document.getRootElement();
		//
		// System.out.println(root.getNamespaceURI());
//		String fileName[] = new String[] {"94193076" , "94193077" , "94193079" , "94193088" , "94193089"};
//		
//		String fileAdd = "S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\";
//		String saveAdd = "S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\merge.asc";
//		String[][] temptAscii  = new AsciiBasicControl(fileAdd + fileName[0] + ".asc").getAsciiFile();
//		
//		for(int index = 1 ; index<fileName.length;index++) {
//			temptAscii = new AsciiMerge(temptAscii , fileAdd + fileName[index] + ".asc").getMergedAscii();
//		}
//		
//		new AtFileWriter(temptAscii , saveAdd).textWriter("    ");
		String[][] position = getIotPosition();
		AsciiBuffer buffer =  new AsciiBuffer("S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\merge.asc");
		for(String[] sensor : position) {
			buffer.setPoint(Double.parseDouble(sensor[1]), Double.parseDouble(sensor[1]));
		}
		new AtFileWriter(buffer.getSelecBufferAscii(30) , "S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\merge_selected.asc").textWriter("    ");

	}

	static public String[][] getIotPosition() {
		return new String[][] { { "安中五站", "162367.190368440000000", "2551199.148974450000000" },
				{ "海佃四站", "165662.855498722000000", "2551505.057514020000000" },
				{ "海佃三段站", "166352.509038435000000", "2549777.889128630000000" },
				{ "朝皇宮站", "166393.082894916000000", "2548985.283620740000000" },
				{ "龍金站", "169920.008159805000000", "2551706.181965540000000" },
				{ "安中站", "167329.839003952000000", "2549390.511627280000000" },
				{ "頂安站", "168461.012654870000000", "2549258.957237000000000" },
				{ "安和站", "169782.704140448000000", "2549447.958468440000000" },
				{ "溪頂寮站", "169520.714143787000000", "2547951.882686490000000" } };
	}
}
