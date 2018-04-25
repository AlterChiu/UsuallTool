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
import asciiFunction.AsciiGridChange;
import asciiFunction.AsciiMerge;
import asciiFunction.XYZToAscii;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class testAtCommon {

	public static void main(String[] args) throws OperationNotSupportedException, IOException, DocumentException {
		// TODO Auto-generated method stub
//		String fileAdd ="S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\AnChung\\buffer30m.asc";
//		String fileAdd = "S:\\Users\\alter\\Desktop\\dm2d0029.asc";
//		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
//		System.out.println(ascii.getValue(168461.012654870000000,2549258.95723700000000));
		
		
//		String fileAdd ="S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\AnChung\\buffer30m(kn).asc";
//		AsciiGridChange ascii = new AsciiGridChange(fileAdd);
//		new AtFileWriter(ascii.getChangedContent(10) , "S:\\HomeWork\\ICCCBE2018\\LevelDEM\\IOT_1m\\AnChung\\buffer30m_10m(kn).asc").textWriter("    ");;

//		String fileAdd = "S:\\HomeWork\\QpesumsAnalysis\\SHP\\Taiwan_Qpesums_grid_1.xyz";
//		XYZToAscii toAscii = new XYZToAscii(fileAdd);
//		toAscii.setCellSize(1400);
//		toAscii.saveAscii("S:\\HomeWork\\QpesumsAnalysis\\SHP\\Taiwan_Qpesums_grid_1.asc");
//		
	}

	static public String[][] getIotPosition() {
		return new String[][] {{ "AnChung5", "162367.190368440000000", "2551199.148974450000000" },
			{ "HaiTeing4", "165662.855498722000000", "2551505.057514020000000" },
			{ "HaiTeing3", "166352.509038435000000", "2549777.889128630000000" },
			{ "ChauHuangTemple", "166393.082894916000000", "2548985.283620740000000" },
			{ "LongKin", "169920.008159805000000", "2551706.181965540000000" },
			{ "AnChung", "167329.839003952000000", "2549390.511627280000000" },
			{ "DinAhn", "168461.012654870000000", "2549258.957237000000000" },
			{ "AnHo", "169782.704140448000000", "2549447.958468440000000" },
			{ "SeeDingLaio", "169520.714143787000000", "2547951.882686490000000" },
			{ "YuiNongYuiYeeSection", "172977.183568405000000", "2543479.676934840000000" } };
	}
}
