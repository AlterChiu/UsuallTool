import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import FEWS.Rinfall.BUI.BuiTranslate;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiGridChange;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiSplit;
import asciiFunction.AsciiToJson;
import usualTool.AtFileWriter;


public class test {

	public static void main(String[] args) throws IOException, OperationNotSupportedException, ParseException {
		// TODO Auto-generated method stub

		
		String originalDem = "C:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Modules\\WRA\\Taiwan\\Southern\\Tainan\\Sobek\\Zone4\\Input\\Rainfall.xml";
		String temptDemFolder = "C:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Modules\\WRA\\Taiwan\\Southern\\Tainan\\Sobek\\Zone4\\Input\\Rainfall.bui";
		
		BuiTranslate bui = new BuiTranslate(originalDem);
		new AtFileWriter(bui.getBuiRainfall() , temptDemFolder).textWriter("");;
	
	}
}
