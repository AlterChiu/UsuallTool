package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.gdal.ogr.Geometry;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import usualTool.MathEqualtion.AtLineIntersection;

public class GdalGlobal {
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;

	public static String gdalBinFolder = "F:\\Qgis\\3.4\\bin\\";

	public static String sagaBinFolder = "F:\\Qgis\\3.4\\apps\\saga-ltr\\";
	public static String grassBinFolder = "F:\\Qgis\\3.4\\apps\\grass\\grass76\\bin\\";

	public static String temptFolder = "F:/Qgis/test/";
	public static String temptFile = temptFolder + "tempt";

	public static Geometry geometryTranlster(Geometry geo, int importCoordinate, int outputCoordinate) {
		SpatialReference inputSpatital = new SpatialReference();
		inputSpatital.ImportFromEPSG(importCoordinate);

		SpatialReference outputSpatital = new SpatialReference();
		outputSpatital.ImportFromEPSG(outputCoordinate);

		CoordinateTransformation geoTrans = new CoordinateTransformation(inputSpatital, outputSpatital);
		Geometry temptGeo = geo.Clone();
		temptGeo.Transform(geoTrans);

		return temptGeo;
	}

	public static List<Path2D> GeomertyToPath2D(Geometry geometry) {
		List<Path2D> outList = new ArrayList<>();

		JsonObject geoJson = new JsonParser().parse(geometry.ExportToJson()).getAsJsonObject();
		String polygonType = geoJson.get("type").getAsString();

		if (polygonType.toUpperCase().equals("POLYGON")) {
			/*
			 * single polygon type
			 */
			Path2D outPath = new Path2D.Double();

			JsonArray coordinates = geoJson.get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
			double startX = coordinates.get(0).getAsJsonArray().get(0).getAsDouble();
			double startY = coordinates.get(0).getAsJsonArray().get(1).getAsDouble();
			outPath.moveTo(startX, startY);

			for (int index = 1; index < coordinates.size() - 1; index++) {
				double temptX = coordinates.get(index).getAsJsonArray().get(0).getAsDouble();
				double temptY = coordinates.get(index).getAsJsonArray().get(1).getAsDouble();
				outPath.lineTo(temptX, temptY);
			}
			outList.add(outPath);

			/*
			 * for multi polygon
			 */
		} else if (polygonType.toUpperCase().equals("MULTIPOLYGON")) {
			JsonArray polygons = geoJson.get("coordinates").getAsJsonArray();

			for (int polygonIndex = 0; polygonIndex < polygons.size(); polygonIndex++) {
				Path2D outPath = new Path2D.Double();
				JsonArray coordinates = polygons.get(polygonIndex).getAsJsonArray().get(0).getAsJsonArray();
				double startX = coordinates.get(0).getAsJsonArray().get(0).getAsDouble();
				double startY = coordinates.get(0).getAsJsonArray().get(1).getAsDouble();
				outPath.moveTo(startX, startY);

				for (int index = 1; index < coordinates.size() - 1; index++) {
					double temptX = coordinates.get(index).getAsJsonArray().get(0).getAsDouble();
					double temptY = coordinates.get(index).getAsJsonArray().get(1).getAsDouble();
					outPath.lineTo(temptX, temptY);
				}
				outList.add(outPath);
			}
		} else {
			System.out.println("notPOLYGON");
		}
		return outList;
	}

	public static Geometry Path2DToGeometry(Path2D path) {
		PathIterator temptPathIteratore = path.getPathIterator(null);
		float coordinate[] = new float[2];

		// start coordinate
		temptPathIteratore.currentSegment(coordinate);
		double startX = coordinate[0];
		double startY = coordinate[1];

		// output geometry ,start point
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"Polygon\" , \"coordinates\" : [[");
		sb.append("[" + startX + "," + startY + "],");
		temptPathIteratore.next();

		for (; !temptPathIteratore.isDone(); temptPathIteratore.next()) {
			temptPathIteratore.currentSegment(coordinate);
			sb.append("[" + coordinate[0] + "," + coordinate[1] + "],");
		}
		sb.append("[" + startX + "," + startY + "] ]] }");

		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry Path2DToGeometry(List<Path2D> pathList) {
		Geometry outGeo = createMultipolygon();

		pathList.forEach(path -> {
			outGeo.AddGeometry(Path2DToGeometry(path));
		});

		return outGeo;
	}

	public static Geometry createMultipolygon() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiPolygon\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry pointToGeometry(Double[] point) {
		StringBuilder sb = new StringBuilder();
		sb.append("{  \"type\" : \"Point\" , \"coordinates\" : [");
		sb.append(point[0] + "," + point[1]);
		sb.append("]}");

		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry lineToGeometry(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"LineString\" , \"coordinates\" : [");
		sb.append("[" + points.get(0)[0] + "," + points.get(0)[1] + "]");
		for (int index = 1; index < points.size(); index++) {
			sb.append(",[" + points.get(index)[0] + "," + points.get(index)[1] + "]");
		}
		sb.append("]}");

		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry lineToGeometry(Double[] point1, Double[] point2) {
		List<Double[]> temptList = new ArrayList<>();
		temptList.add(point1);
		temptList.add(point2);

		return lineToGeometry(temptList);
	}

	public static List<Double[]> getBreakPoint(Geometry geo) {
		JsonObject geoJson = new JsonParser().parse(geo.ExportToJson()).getAsJsonObject();
		String polygonType = geoJson.get("type").getAsString();
		List<Double[]> temptList = new ArrayList<>();

		if (polygonType.toUpperCase().equals("POLYGON")) {
			JsonArray coordinates = geoJson.get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
			for (int index = 0; index < coordinates.size(); index++) {
				double temptX = coordinates.get(index).getAsJsonArray().get(0).getAsDouble();
				double temptY = coordinates.get(index).getAsJsonArray().get(1).getAsDouble();
				temptList.add(new Double[] { temptX, temptY });
			}
			return getBreakPoint(temptList);
		} else {
			return null;
		}
	}

	public static List<Double[]> getBreakPoint(Path2D path) {
		List<Double[]> temptList = new ArrayList<>();
		PathIterator temptPathIteratore = path.getPathIterator(null);
		double[] coordinate = new double[2];

		for (; !temptPathIteratore.isDone(); temptPathIteratore.next()) {
			temptPathIteratore.currentSegment(coordinate);
			temptList.add(new Double[] { coordinate[0], coordinate[1] });
		}
		return getBreakPoint(temptList);
	}

	public static List<Double[]> getBreakPoint(List<Double[]> points) {
		List<Double[]> outList = new ArrayList<>();

		for (int index = 0; index < points.size(); index++) {
			Double[] startPoint = points.get(index);

			// point1
			Double[] point1;
			try {
				point1 = points.get(index + 1);
			} catch (Exception e) {
				point1 = points.get(index + 1 - points.size());
			}
			double slope1 = (point1[1] - startPoint[1]) / (point1[0] - startPoint[0]);

			// point2
			Double[] point2;
			try {
				point2 = points.get(index + 2);
			} catch (Exception e) {
				point2 = points.get(index + 2 - points.size());
			}
			double slope2 = (point2[1] - point1[1]) / (point2[0] - point1[0]);

			if (Math.abs(slope2 - slope1) > 0.0000000001) {
				outList.add(point1);
			}
		}

		return outList;
	}

}
