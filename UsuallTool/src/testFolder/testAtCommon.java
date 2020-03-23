package testFolder;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.io.File;
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
import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import geo.common.CoordinateTranslate;
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
		double[][] coordinates = new double[][] { { 24.65404, 121.79239 }, { 24.7196, 121.815 }, { 24.7705, 121.813 },
				{ 24.7286, 121.818 }, { 24.7304, 121.795 }, { 24.7258, 121.8 }, { 24.7351, 121.812 },
				{ 24.7228, 121.758 }, { 24.7397, 121.777 }, { 24.7297, 121.755 }, { 24.7359, 121.778 },
				{ 24.7371, 121.774 }, { 24.7325, 121.774 }, { 24.7669, 121.748 }, { 24.7569, 121.73 },
				{ 24.7583, 121.77 }, { 24.7483, 121.772 }, { 24.7234, 121.769 }, { 24.75, 121.721 },
				{ 24.6833, 121.816 }, { 24.6737, 121.824 }, { 24.6929, 121.814 }, { 24.7024, 121.822 },
				{ 24.658, 121.816 }, { 24.68, 121.818 }, { 24.7025, 121.81 }, { 24.6959, 121.817 },
				{ 24.6993, 121.823 }, { 24.701, 121.804 }, { 24.6761, 121.812 }, { 24.6831, 121.828 },
				{ 24.7014, 121.802 }, { 24.7084, 121.779 }, { 24.6705, 121.784 }, { 24.6682, 121.805 },
				{ 24.6763, 121.8 }, { 24.6899, 121.788 }, { 24.6905, 121.781 }, { 24.6849, 121.791 },
				{ 24.6961, 121.784 }, { 24.7069, 121.822 }, { 24.6606, 121.811 }, { 24.6586, 121.806 },
				{ 24.6527, 121.795 }, { 24.649, 121.804 }, { 24.6667, 121.771 }, { 24.6697, 121.795 },
				{ 24.6417, 121.83 }, { 24.6707, 121.71 }, { 24.6825, 121.726 }, { 24.6695, 121.726 },
				{ 24.72992, 121.78637 }, { 24.70712, 121.82049 }, { 24.65858, 121.79933 }, { 24.727908, 121.795562 },
				{ 24.74639, 121.78589 }, { 24.72707, 121.802 }, { 24.73595, 121.77258 }, { 24.66677, 121.80572 },
				{ 24.65748, 121.80188 }, { 24.65876, 121.81208 } };

		AsciiBasicControl ascii = new AsciiBasicControl("D:\\merge.asc");

		for (double[] coordinate : coordinates) {
			double coordinate97[] = CoordinateTranslate.Wgs84ToTwd97(coordinate[1], coordinate[0]);
			System.out.println(ascii.getValue(coordinate97[0], coordinate97[1]));

		}
	}
}
