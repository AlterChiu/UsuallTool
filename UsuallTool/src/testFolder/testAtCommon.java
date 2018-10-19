package testFolder;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogrConstants;

import com.google.gson.JsonObject;

import Drawing.Excel.ChartImplemetns;
import Drawing.JFreeChart.ChartImplement;
import FEWS.PIXml.AtPiXmlReader;
import FEWS.Rinfall.BUI.BuiTranslate;
import Netcdf.NetCDFReader;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiToPath;
import asciiFunction.XYZToAscii;
import geo.gdal.DBFToGeoJson;
import geo.path.IntersectLine;
import geo.path.PathToGeoJson;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtKmeans;
import usualTool.ExcelBasicControl;
import usualTool.FileFunction;
import usualTool.RandomMaker;
import usualTool.TimeTranslate;
import usualTool.MathEqualtion.AtDeterminant;

public class testAtCommon {

	public static void main(String[] args) throws IOException, InvalidRangeException, OperationNotSupportedException,
			ParseException, EncryptedDocumentException, InvalidFormatException {
		// TODO Auto-generated method

		AsciiToPath asciiPath = new AsciiToPath(
				"S:\\Users\\alter\\Downloads\\94184010_HyDEMk1_breakline\\94184010_HyDEMk1_breakline_clip.asc");
		IntersectLine pathIntersect = new IntersectLine(asciiPath.getAsciiPath());
		pathIntersect.getInterceptPoints(0, 1, -2542082).forEach(e -> System.out.println(e[0] + "\t" + e[1]));

	}
}