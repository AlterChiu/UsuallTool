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
import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import nl.wldelft.util.io.ShapeFileReader;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.MathEqualtion.Distribution.AtDistribution;
import usualTool.MathEqualtion.Distribution.AtNormalDistribution;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method
		String centerPoints[][] = new AtFileReader("E:\\QpesumsAnalysis\\SHP\\gridXY.csv").getCsv(1, 0);

		List<String> title = new ArrayList<String>();
		title.add("X");
		title.add("Y");
		title.add("PathId");
		title.add("countNum");

		Map<String, String> type = new TreeMap<>();
		type.put("X", "double");
		type.put("Y", "double");
		type.put("PathId", "String");
		type.put("countNum", "int");

		List<Map<String, Object>> table = new ArrayList<>();
		List<Path2D> gemetry = new ArrayList<>();

		int countNum = 0;
		for (String[] point : centerPoints) {
			String centerX = point[0];
			String centerY = point[1];

			double minX = new BigDecimal(Double.parseDouble(centerX) - 0.00625).setScale(4, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			double maxX = new BigDecimal(Double.parseDouble(centerX) + 0.00625).setScale(4, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			double minY = new BigDecimal(Double.parseDouble(centerY) - 0.00625).setScale(4, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			double maxY = new BigDecimal(Double.parseDouble(centerY) + 0.00625).setScale(4, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			Path2D temptPath = new Path2D.Double();
			temptPath.moveTo(minX, minY);
			temptPath.lineTo(minX, maxY);
			temptPath.lineTo(maxX, maxY);
			temptPath.lineTo(maxX, minY);

			Map<String, Object> feature = new TreeMap<>();
			feature.put("X", (Double) Double.parseDouble(centerX));
			feature.put("Y", (Double) Double.parseDouble(centerY));
			feature.put("PathId", (String) centerX + "_" + centerY);
			feature.put("countNum", (Integer) countNum);
			table.add(feature);
			gemetry.add(temptPath);
			countNum++;
		}

		SpatialWriter spWriter = new SpatialWriter(gemetry);
		spWriter.setAttributeTitle(table);
		spWriter.setCoordinateSystem(SpatialWriter.WGS84);
		spWriter.setField(type);
		spWriter.saveAsShp("E:\\QpesumsAnalysis\\SHP\\QpesumsHyAnalysisGrid_WGS84.shp");

	}

}