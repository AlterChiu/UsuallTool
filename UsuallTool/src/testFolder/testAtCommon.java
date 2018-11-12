package testFolder;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.gdal.ogr.Geometry;

import Hydro.Rainfall.ReturnPeriod.ReturnTest;
import asciiFunction.AsciiBasicControl;
import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import nl.wldelft.util.io.ShapeFileReader;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.MathEqualtion.Distribution.AtDistribution;
import usualTool.MathEqualtion.Distribution.AtNormalDistribution;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method

		AsciiBasicControl ascii = new AsciiBasicControl("E:\\mapReduce\\modelTest\\OriginalDEM\\ZoneU1_20m.asc");
		List<Map<String, Double>> boudnarys = ascii.getIntersectSideBoundary(1, 0, -164791);

		boudnarys.forEach(e -> System.out.println(e.get("minX") + "\t" + e.get("maxX")));

	}
}