package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GdalGlobal {

	/*
	 * library & temptFolder
	 */
	public static String gdalBinFolder = "K:\\Qgis\\3.4.13\\bin\\";
	public static String sagaBinFolder = "K:\\Qgis\\3.4.13\\apps\\saga-ltr\\";
	public static String grassBinFolder = "K:\\Qgis\\3.4.13\\apps\\grass\\grass78\\bin\\";

	public static String temptFolder = "K:/Qgis/test/";
	public static String temptFile = temptFolder + "tempt";

	/*
	 * Coordinate System
	 */
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;

	public static Geometry GeometryTranslator(Geometry geo, int importCoordinate, int outputCoordinate) {
		SpatialReference inputSpatital = new SpatialReference();
		inputSpatital.ImportFromEPSG(importCoordinate);

		SpatialReference outputSpatital = new SpatialReference();
		outputSpatital.ImportFromEPSG(outputCoordinate);

		CoordinateTransformation geoTrans = new CoordinateTransformation(inputSpatital, outputSpatital);
		Geometry temptGeo = geo.Clone();
		temptGeo.Transform(geoTrans);

		return temptGeo;
	}

	public static List<Path2D> GeomertyToPath2D(Geometry geometry) throws IOException {
		List<Path2D> outList = new ArrayList<>();

		if (geometry.GetGeometryName().toUpperCase().equals("POLYGON")
				|| geometry.GetGeometryName().toUpperCase().equals("POLYLINE")) {
			outList.add(GeomertyToPath2D_Polygon(geometry));

		} else if (geometry.GetGeometryName().toUpperCase().equals("MULTIPOLYGON")
				|| geometry.GetGeometryName().toUpperCase().equals("MULTIPOLYLINE")) {
			for (int index = 0; index < geometry.GetGeometryCount(); index++) {
				outList.add(GeomertyToPath2D_Polygon(geometry.GetGeometryRef(index)));
			}
		} else {
			throw new IOException("not correct geometry type");
		}
		return outList;
	}

	private static Path2D GeomertyToPath2D_Polygon(Geometry geometry) {
		Geometry temptGeo;
		try {
			temptGeo = geometry.Boundary();
		} catch (Exception e) {
			temptGeo = geometry;
		}

		Path2D outPath = new Path2D.Double();

		outPath.moveTo(temptGeo.GetPoint(0)[0], geometry.GetPoint(0)[1]);
		for (int index = 1; index < temptGeo.GetPointCount(); index++) {
			outPath.lineTo(temptGeo.GetPoint(index)[0], geometry.GetPoint(index)[1]);
		}
		return outPath;
	}

	public static Geometry Path2DToGeometry(Path2D path) {
		PathIterator temptPathIteratore = path.getPathIterator(null);
		double coordinate[] = new double[2];

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

	public static Geometry Path2DToMultiGeometry(List<Path2D> pathList) {
		Geometry outGeo = CreateMultipolygon();

		pathList.forEach(path -> {
			outGeo.AddGeometry(Path2DToGeometry(path));
		});

		return outGeo;
	}

	public static Geometry pointToGeometry(Double[] point) {
		StringBuilder sb = new StringBuilder();
		sb.append("{  \"type\" : \"Point\" , \"coordinates\" : [");
		sb.append(point[0] + "," + point[1]);
		sb.append("]}");

		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry pointToMuliGeometry(List<Double[]> points) {
		Geometry outGeo = CreateMultiPoint();
		points.forEach(point -> {
			outGeo.AddGeometry(pointToGeometry(point));
		});
		return outGeo;
	}

	public static Geometry LineToGeometry(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"LineString\" , \"coordinates\" : [");
		sb.append("[" + points.get(0)[0] + "," + points.get(0)[1] + "]");
		for (int index = 1; index < points.size(); index++) {
			sb.append(",[" + points.get(index)[0] + "," + points.get(index)[1] + "]");
		}
		sb.append("]}");

		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry LineToGeometry(Double[] point1, Double[] point2) {
		List<Double[]> temptList = new ArrayList<>();
		temptList.add(point1);
		temptList.add(point2);

		return LineToGeometry(temptList);
	}

	public static Geometry LineToGeometry(Path2D path) {
		List<Double[]> temptList = new ArrayList<>();
		PathIterator iterator = path.getPathIterator(null);
		double coordinate[] = new double[2];

		for (; iterator.isDone(); iterator.next()) {
			iterator.currentSegment(coordinate);
			temptList.add(new Double[] { coordinate[0], coordinate[1] });
		}

		return LineToGeometry(temptList);
	}

	public static Geometry CreateMultipolygon() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiPolygon\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreatePolygon(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();

		// make sure it will close to start point
		points.add(points.get(0));

		sb.append("{\"type\" : \"Polygon\" , \"coordinates\" : [[");
		sb.append(String.join(",", points.parallelStream().map(point -> "[" + point[0] + "," + point[1] + "]")
				.collect(Collectors.toList())));
		sb.append("]]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreateMultiPoint() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiPoint\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreatePoint(double x, double y) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"Point\" , \"coordinates\" : [ " + x + "," + y + " ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreateMultiLine() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiLineString\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreateLine(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"LineString\" , \"coordinates\" : [ ");
		sb.append(String.join(",", points.parallelStream().map(point -> "[" + point[0] + "," + point[1] + "]")
				.collect(Collectors.toList())));
		sb.append("]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreateLine(double x1, double y1, double x2, double y2) {
		List<Double[]> temptList = new ArrayList<>();
		temptList.add(new Double[] { x1, y1 });
		temptList.add(new Double[] { x2, y2 });
		return CreateLine(temptList);
	}

	public static Path2D PointsToPath(List<Double[]> points) {
		Path2D temptPath = new Path2D.Double();
		temptPath.moveTo(points.get(0)[0], points.get(0)[1]);
		for (int index = 1; index < points.size(); index++) {
			temptPath.lineTo(points.get(index)[0], points.get(index)[1]);
		}
		return temptPath;
	}

	public static Geometry mergePolygons(List<Geometry> geoList, Boolean showDetails) {
		/*
		 * check
		 */
		if (geoList.size() <= 0) {
			new Exception("not able to merge polygon");
			return null;
		} else {

			/*
			 * clone
			 */
			List<Geometry> outList = new ArrayList<>();
			for (Geometry geo : geoList) {
				outList.add(geo);
			}

			/*
			 * starting merge
			 */
			while (outList.size() != 1) {
				List<Geometry> temptList = new ArrayList<>();

				if (showDetails)
					System.out.print(outList.size() + "....");
				for (int index = 0; index < outList.size(); index = index + 2) {
					try {
						temptList.add(outList.get(index).Union(outList.get(index + 1)));
					} catch (Exception e) {
						temptList.add(outList.get(index));
					}
				}

				outList = temptList;
			}

			return outList.get(0);
		}
	}

	public static Geometry mergePolygons(List<Geometry> geoList) {
		/*
		 * check
		 */
		if (geoList.size() <= 0) {
			new Exception("not able to merge polygon");
			return null;
		} else {

			/*
			 * clone
			 */
			List<Geometry> outList = new ArrayList<>();
			for (Geometry geo : geoList) {
				outList.add(geo);
			}

			/*
			 * starting merge
			 */
			while (outList.size() != 1) {
				List<Geometry> temptList = new ArrayList<>();
				for (int index = 0; index < outList.size(); index = index + 2) {
					try {
						temptList.add(outList.get(index).Union(outList.get(index + 1)));
					} catch (Exception e) {
						temptList.add(outList.get(index));
					}
				}

				outList = temptList;
			}

			return outList.get(0);
		}
	}

	public static List<Geometry> splitPolygons(Geometry geo) {
		List<Geometry> outList = new ArrayList<>();
		for (int index = 0; index < geo.GetGeometryCount(); index++) {
			outList.add(geo.GetGeometryRef(index));
		}
		return outList;
	}

	public static List<Path2D> getQualTree_Path(double[] centerPoint, double cellSize) {
		return getQualTree_Path(centerPoint, cellSize, 4);
	}

	public static List<Path2D> getQualTree_Path(double[] centerPoint, double cellSize, int dataDecimale) {
		List<Path2D> outList = new ArrayList<>();
		outList.add(getGrid(new double[] { centerPoint[0] - 0.25 * cellSize, centerPoint[1] - 0.25 * cellSize },
				0.5 * cellSize, dataDecimale));
		outList.add(getGrid(new double[] { centerPoint[0] - 0.25 * cellSize, centerPoint[1] + 0.25 * cellSize },
				0.5 * cellSize, dataDecimale));
		outList.add(getGrid(new double[] { centerPoint[0] + 0.25 * cellSize, centerPoint[1] + 0.25 * cellSize },
				0.5 * cellSize, dataDecimale));
		outList.add(getGrid(new double[] { centerPoint[0] + 0.25 * cellSize, centerPoint[1] - 0.25 * cellSize },
				0.5 * cellSize, dataDecimale));
		return outList;
	}

	public static Geometry lineStringToPolygon(Geometry polyLine) {
		JsonObject polyLineObject = new JsonParser().parse(polyLine.ExportToJson()).getAsJsonObject();
		JsonArray pointsArray = polyLineObject.get("coordinates").getAsJsonArray();

		JsonObject outObject = new JsonObject();
		outObject.addProperty("type", "Polygon");

		JsonArray coordinatesArray = new JsonArray();
		coordinatesArray.add(pointsArray);

		outObject.add("coordinates", coordinatesArray);
		return ogr.CreateGeometryFromJson(outObject.toString());
	}

	public static Path2D getGrid(double[] centerPoint, double cellSize) {
		return getGrid(centerPoint, cellSize, 4);
	}

	public static Path2D getGrid(double[] centerPoint, double cellSize, int dataDecima) {
		Path2D temptPath = new Path2D.Double();

		double minX = new BigDecimal(centerPoint[0] - 0.5 * cellSize).setScale(dataDecima, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double maxX = new BigDecimal(centerPoint[0] + 0.5 * cellSize).setScale(dataDecima, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double minY = new BigDecimal(centerPoint[1] - 0.5 * cellSize).setScale(dataDecima, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double maxY = new BigDecimal(centerPoint[1] + 0.5 * cellSize).setScale(dataDecima, BigDecimal.ROUND_HALF_UP)
				.doubleValue();

		temptPath.moveTo(minX, minY);
		temptPath.lineTo(maxX, minY);
		temptPath.lineTo(maxX, maxY);
		temptPath.lineTo(minX, maxY);

		return temptPath;
	}

	public static List<String> GDAL_EnviromentStarting() {
		List<String> outList = new ArrayList<>();
		outList.add("@echo off");
		outList.add("call \"%~dp0\\o4w_env.bat\"");
		outList.add("call \"%OSGEO4W_ROOT%\\apps\\grass\\grass78\\etc\\env.bat\"");
		outList.add("call qt5_env.bat");
		outList.add("call py3_env.bat");
		outList.add("@echo off");
		outList.add(
				"path %OSGEO4W_ROOT%\\apps\\qgis-ltr\\bin;%OSGEO4W_ROOT%\\apps\\grass\\grass78\\lib;%OSGEO4W_ROOT%\\apps\\grass\\grass78\\bin;%PATH%");
		outList.add("set QGIS_PREFIX_PATH=%OSGEO4W_ROOT:\\=/%/apps/qgis-ltr");
		outList.add("set GDAL_FILENAME_IS_UTF8=YES");
		return outList;
	}

	private class mergePolygons_Threads {

	}
}
