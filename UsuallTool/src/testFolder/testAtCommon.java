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
		String fileAdd = "S:\\Users\\alter\\Downloads\\94184010_HyDEMk1_breakline\\94184010_HyDEMk1_breakline.asc";
		String zone4Add = "C:\\FEWS\\FEWS_Taiwan_2018\\FEWS_Taiwan_2018\\Taiwan\\Modules\\WRA\\Taiwan\\Southern\\Tainan\\Sobek\\Zone4\\TANZ4U01.lit\\DEM\\zone4(New20m_DEM).asc";
		String knFile = "C:\\FEWS\\FEWS_Taiwan_2018\\FEWS_Taiwan_2018\\Taiwan\\Modules\\WRA\\Taiwan\\Southern\\Tainan\\Sobek\\Zone4\\TANZ4U01.lit\\DEM\\zone4(New20m_kn).asc";
		AsciiBasicControl knAscii = new AsciiBasicControl(knFile);

		// AsciiBasicControl zone4Ascii = new AsciiBasicControl(zone4Add);
		// double cellSize =
		// Double.parseDouble(zone4Ascii.getProperty().get("cellSize"));
		// double topLeftCorner[] = zone4Ascii.getClosestCoordinate(173016, 2542278);
		// double bottomRightCorner[] = zone4Ascii.getClosestCoordinate(173718,
		// 2541943);
		//
		// AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
		// new AtFileWriter(
		// ascii.getClipAsciiFile(topLeftCorner[0] - 0.5 * cellSize - 1,
		// bottomRightCorner[1] - 0.5 * cellSize - 1,
		// bottomRightCorner[0] + 0.5 * cellSize + 1, topLeftCorner[1] + 0.5 * cellSize
		// + 1),
		// "S:\\Users\\alter\\Downloads\\94184010_HyDEMk1_breakline\\94184010_HyDEMk1_breakline.asc")
		// .textWriter(" ");

		// AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
		// for (int row = 0; row < Integer.parseInt(ascii.getProperty().get("row"));
		// row++) {
		// for (int column = 0; column <
		// Integer.parseInt(ascii.getProperty().get("column")); column++) {
		// Double tmeptValue = Double.parseDouble(ascii.getValue(column, row));
		//
		// ascii.setValue(column, row,
		// new BigDecimal(tmeptValue - 20.2).setScale(3,
		// BigDecimal.ROUND_HALF_UP).toString());
		// }
		// }
		// new AtFileWriter(ascii.getAsciiFile(),
		// "S:\\Users\\alter\\Downloads\\94184010_HyDEMk1_breakline\\94184010_HyDEMk1_breakline_clip_rightHeight.asc")
		// .textWriter(" ");

		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
		for (int row = 0; row < Integer.parseInt(ascii.getProperty().get("row")); row++) {
			for (int column = 0; column < Integer.parseInt(ascii.getProperty().get("column")); column++) {
				double[] coordinate = ascii.getCoordinate(column, row);

				ascii.setValue(column, row, knAscii.getValue(coordinate[0], coordinate[1]));
			}
		}
		new AtFileWriter(ascii.getAsciiFile(),
				"S:\\Users\\alter\\Downloads\\94184010_HyDEMk1_breakline\\94184010_HyDEMk1_breakline_clip_rightHeight(kn).asc")
						.textWriter(" ");

		// String fileAdd = "S:\\Users\\alter\\Downloads\\subcatchment_all_xy.csv";
		// String content[][] = new AtFileReader(fileAdd).getCsv(1, 0);
		// Map<String, List<Double[]>> outMap = new HashMap<String, List<Double[]>>();
		//
		// // initial thoutMap
		// for (String row[] : content) {
		// String id = row[0];
		//
		// List<Double[]> pathList = outMap.get(id);
		// if (pathList == null) {
		// pathList = new ArrayList<Double[]>();
		// }
		// pathList.add(new Double[] { Double.parseDouble(row[1]),
		// Double.parseDouble(row[2]) });
		// outMap.put(id, pathList);
		// }
		//
		// List<Path2D> pathList = new ArrayList<Path2D>();
		// List<Map<String, String>> idList = new ArrayList<Map<String, String>>();
		// outMap.keySet().forEach(key -> {
		// Map<String, String> temptMap = new TreeMap<String, String>();
		// temptMap.put("ID", key);
		// idList.add(temptMap);
		//
		// Path2D path = new Path2D.Double();
		// List<Double[]> coordinates = outMap.get(key);
		// path.moveTo(coordinates.get(0)[0], coordinates.get(0)[1]);
		//
		// for (int index = 1; index < coordinates.size(); index++) {
		// path.lineTo(coordinates.get(index)[0], coordinates.get(index)[1]);
		// }
		//
		// pathList.add(path);
		// });
		//
		// PathToGeoJson geoJson = new PathToGeoJson(pathList);
		// geoJson.setCoordinateSystem(PathToGeoJson.TWD97);
		// geoJson.setAttributeTitle(idList);
		// geoJson.saveGeoJson("S:\\Users\\alter\\Downloads\\subcatchment_all_xy.geoJson");
	}
}