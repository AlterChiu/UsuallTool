package testFolder;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import FEWS.Rinfall.BUI.BuiTranslate;
import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import geo.gdal.CsvToSpatialFile;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialFileTranslater;
import geo.gdal.Interpolation.AtInterpolation;
import geo.gdal.Interpolation.InterPolationKriging;
import ucar.nc2.NetcdfFile;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.FileFunction;
import usualTool.TimeTranslate;

public class test {

	public static void main(String[] args)
			throws IOException, InterruptedException, ParseException, OperationNotSupportedException {
		// TODO Auto-generated method stud

		BuiTranslate bui = new BuiTranslate(
				"E:\\LittleProject\\IotSensorComparision\\20190702 - 桃園\\modelRainfall.xml");
		new AtFileWriter(bui.getBuiRainfall(),
				"E:\\LittleProject\\IotSensorComparision\\20190702 - 桃園\\modelRainfall.BUIl").textWriter(" ");
	}

}
