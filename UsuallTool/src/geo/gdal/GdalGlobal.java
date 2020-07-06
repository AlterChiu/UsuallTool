package geo.gdal;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.File;
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

import usualTool.MathEqualtion.RandomMaker;

public class GdalGlobal {

	/*
	 * library & temptFolder
	 */
	public static String qgisBinFolder = "K:\\Qgis\\3.4.13\\";
	public static String gdalBinFolder = qgisBinFolder + "bin\\";
	public static String sagaBinFolder = qgisBinFolder + "apps\\saga-ltr\\";
	public static String grassBinFolder = qgisBinFolder + "apps\\grass\\grass78\\bin\\";

	public static String temptFolder = qgisBinFolder + "temptFolder";

	/*
	 * Coordinate System
	 */
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;
	public static String TWD97_121_prj4 = "Proj4: +proj=tmerc +lat_0=0 +lon_0=121 +k=0.9999 +x_0=250000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";
	public static String WGS84_prj4 = "Proj4: +proj=longlat +datum=WGS84 +no_defs";

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

	public static Geometry GeometryTranslator(Geometry geo, String importCoordinate, String outputCoordinate) {
		SpatialReference inputSpatital = new SpatialReference();
		inputSpatital.ImportFromProj4(importCoordinate);

		SpatialReference outputSpatital = new SpatialReference();
		outputSpatital.ImportFromProj4(outputCoordinate);

		CoordinateTransformation geoTrans = new CoordinateTransformation(inputSpatital, outputSpatital);
		Geometry temptGeo = geo.Clone();
		temptGeo.Transform(geoTrans);

		return temptGeo;
	}

	public static List<Path2D> GeomertyToPath2D(Geometry geometry) throws IOException {
		List<Path2D> outList = new ArrayList<>();

		if (geometry.GetGeometryName().toUpperCase().equals("POLYGON")) {
			outList.add(GeomertyToPath2D_Polygon(geometry.GetGeometryRef(0)));

		} else if (geometry.GetGeometryName().toUpperCase().equals("MULTIPOLYGON")) {
			for (int index = 0; index < geometry.GetGeometryCount(); index++) {
				outList.add(GeomertyToPath2D_BoundaryPolygon(geometry.GetGeometryRef(index)));
			}
		} else if (geometry.GetGeometryName().toUpperCase().equals("POLYLINE")
				|| geometry.GetGeometryName().toUpperCase().equals("MULTIPOLYLINE")) {
			throw new IOException("not allowable for geometry type LineString");
		} else {
			throw new IOException("not correct geometry type");
		}
		return outList;
	}

	private static Path2D GeomertyToPath2D_BoundaryPolygon(Geometry geometry) {
		Path2D outPath = new Path2D.Double();

		Geometry temptGeo = geometry.Boundary();
		outPath.moveTo(temptGeo.GetPoint(0)[0], temptGeo.GetPoint(0)[1]);
		for (int index = 1; index < temptGeo.GetPointCount(); index++) {
			outPath.lineTo(temptGeo.GetPoint(index)[0], temptGeo.GetPoint(index)[1]);
		}
		return outPath;
	}

	private static Path2D GeomertyToPath2D_Polygon(Geometry geometry) {
		Path2D outPath = new Path2D.Double();

		outPath.moveTo(geometry.GetPoint(0)[0], geometry.GetPoint(0)[1]);
		for (int index = 1; index < geometry.GetPointCount(); index++) {
			outPath.lineTo(geometry.GetPoint(index)[0], geometry.GetPoint(index)[1]);
		}
		return outPath;
	}

	public static Path2D pathFixPoint(Path2D path, double leftBottomX, double leftBottomY) {
		Rectangle rec = path.getBounds();
		double minX = rec.getMinX();
		double minY = rec.getMinY();

		double moveX = leftBottomX - minX;
		double moveY = leftBottomY - minY;

		return pathAdjustPanel(path, moveX, moveY);
	}

	public static Path2D pathAdjustPanel(Path2D path, double moveX, double moveY) {
		List<Double[]> pointList = getPathPoints(path);
		List<Double[]> adjustedPointList = new ArrayList<>();

		pointList.forEach(point -> {
			double adjustX = point[0] + moveX;
			double adjustY = point[1] + moveY;
			adjustedPointList.add(new Double[] { adjustX, adjustY });
		});

		return CreatePath2D(adjustedPointList);
	}

	public static Path2D pathAdjustMirroXLine(Path2D path) {
		Rectangle rec = path.getBounds();
		double centerY = rec.getCenterY();

		return pathAdjustMirroXLine(path, centerY);
	}

	public static Path2D pathAdjustMirroXLine(Path2D path, double flipY) {
		List<Double[]> pointList = getPathPoints(path);
		List<Double[]> adjustedPointList = new ArrayList<>();

		pointList.forEach(point -> {
			double adjustX = point[0];
			double adjustY = flipY * 2 - point[1];
			adjustedPointList.add(new Double[] { adjustX, adjustY });
		});

		return CreatePath2D(adjustedPointList);
	}

	public static Path2D pathAdjustMirroYLine(Path2D path) {
		Rectangle rec = path.getBounds();
		double centerX = rec.getCenterX();

		return pathAdjustMirroYLine(path, centerX);
	}

	public static Path2D pathAdjustMirroYLine(Path2D path, double flipX) {

		List<Double[]> pointList = getPathPoints(path);
		List<Double[]> adjustedPointList = new ArrayList<>();

		pointList.forEach(point -> {
			double adjustX = flipX * 2 - point[0];
			double adjustY = point[1];
			adjustedPointList.add(new Double[] { adjustX, adjustY });
		});

		return CreatePath2D(adjustedPointList);
	}

	public static Path2D pathAdjustRatio(Path2D path, double fixX, double fixY, double ratioX, double ratioY) {

		List<Double[]> pointList = getPathPoints(path);
		List<Double[]> adjustedPointList = new ArrayList<>();

		pointList.forEach(point -> {
			double adjustX = fixX + (point[0] - fixX) * ratioX;
			double adjustY = fixY + (point[1] - fixY) * ratioY;
			adjustedPointList.add(new Double[] { adjustX, adjustY });
		});

		return CreatePath2D(adjustedPointList);
	}

	public static Path2D pathAdjustRatio(Path2D path, double ratio) {
		Rectangle rec = path.getBounds();
		return pathAdjustRatio(path, rec.getCenterX(), rec.getCenterY(), ratio, ratio);
	}

	public static List<Double[]> getPathPoints(Path2D path) {
		List<Double[]> outList = new ArrayList<>();

		PathIterator temptPathIteratore = path.getPathIterator(null);
		double coordinate[] = new double[2];

		for (; !temptPathIteratore.isDone(); temptPathIteratore.next()) {
			temptPathIteratore.currentSegment(coordinate);
			outList.add(new Double[] { coordinate[0], coordinate[1] });
		}
		return outList;
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

	public static Path2D CreatePath2D(List<Double[]> points) {
		Path2D outPath = new Path2D.Double();

		outPath.moveTo(points.get(0)[0], points.get(0)[1]);
		for (int index = 1; index < points.size(); index++) {
			outPath.lineTo(points.get(index)[0], points.get(index)[1]);
		}
		return outPath;
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

	// additionFormat should be like this ".csv",".shp"
	public static String newTempFileName(String folder, String additionFormat) {
		StringBuilder temptName = new StringBuilder();

		RandomMaker radom = new RandomMaker();
		for (int index = 0; index < 10; index++) {
			temptName.append(radom.RandomInt(0, 9));
		}
		String temptWholeName = temptName.toString() + additionFormat;

		if (new File(folder + temptWholeName).exists()) {
			return newTempFileName(folder, additionFormat);
		} else {
			return temptWholeName;
		}
	}
}
