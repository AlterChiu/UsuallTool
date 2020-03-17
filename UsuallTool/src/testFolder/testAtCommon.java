package testFolder;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.sl.draw.geom.Path;
import org.gdal.ogr.Geometry;

import FEWS.PIXml.AtPiXmlReader;
import FEWS.Rinfall.BUI.BuiTranslate;
import asciiFunction.XYZToAscii;
import geo.gdal.GdalGlobal;
import geo.gdal.IrregularReachBasicControl;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import netCDF.NetcdfBasicControl;
import nl.wldelft.fews.pi.PiTimeSeriesParser;
import nl.wldelft.fews.pi.PiTimeSeriesReader;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import nl.wldelft.util.timeseries.TimeSeriesArrays;
import usualTool.AtCommonMath;
import usualTool.AtDateClass;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class testAtCommon {
	public static String fileAdd = "E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\各項目文件\\二維模型申請圖幅\\";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		NetcdfBasicControl nc = new NetcdfBasicControl(
//				"E:\\LittleProject\\報告書\\109 - SMM\\測試\\驗證系統\\Verification\\Simulation\\2D\\FloodResult\\Event_00001_Flood.nc");
//
//		nc.getNetFile().getVariables().forEach(v -> System.out.println(v));

		String startTime = "1970-01-01 08";
		String timeFormat = "yyyy-MM-dd HH";

		AtDateClass temptDate = new AtDateClass(startTime, timeFormat);
		temptDate.addMinutes(1000000000);
		System.out.println(temptDate.getDateLong());
		System.out.println(temptDate.getDateString(timeFormat));

	}
}
