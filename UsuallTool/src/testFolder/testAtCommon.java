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
		String fileAdd = "S:\\HomeWork\\測量實習\\106-2\\三角三邊\\original\\";
		for(String name : new File(fileAdd).list()) {
			System.out.println(name);
		}
		

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
