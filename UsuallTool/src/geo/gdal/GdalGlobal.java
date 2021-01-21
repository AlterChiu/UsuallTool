
package geo.gdal;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.gdal.ogr.Geometry;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import usualTool.AtCommonMath;
import usualTool.FileFunction;
import usualTool.MathEqualtion.RandomMaker;

public class GdalGlobal {

	/*
	 * EnviromentSetting path: ./bin; ./bin/proj/apps; ./bin/gdal/apps;
	 * ./bin/ms/apps GDAL_DRIVER: ./bin/gdal/plugins GDAL_DATA: ./bin/gdal-data
	 */

	/*
	 * library & temptFolder
	 */
	public static String qgisBinFolder = "K:\\Qgis\\3.10.7\\";
	public static String gdalBinFolder = qgisBinFolder + "bin\\";
	public static String sagaBinFolder = qgisBinFolder + "apps\\saga-ltr\\";
	public static String grassBinFolder = qgisBinFolder + "apps\\grass\\grass78\\bin\\";
	public static String qgisProcessingPluigins = qgisBinFolder + "apps\\qgis-ltr\\python\\plugins";
	public static String temptFolder = qgisBinFolder + "temptFolder\\";

	public static void setQgisBinFolder(String path) {
		qgisBinFolder = path + "\\";
		gdalBinFolder = qgisBinFolder + "bin\\";
		sagaBinFolder = qgisBinFolder + "apps\\saga-ltr\\";
		grassBinFolder = qgisBinFolder + "apps\\grass\\grass78\\bin\\";
		qgisProcessingPluigins = qgisBinFolder + "apps\\qgis-ltr\\python\\plugins";
	}

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

	public static int dataDecimale = 4;

	// <===================================================>
	// <======== GEOMETRY CREATOR ============================>
	// <===================================================>

	public static Geometry CreateMultipolygon() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiPolygon\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreatePolygon(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"Polygon\" , \"coordinates\" : [[");

		List<String> geoContainer = new ArrayList<>();
		points.forEach(point -> {
			StringBuilder temptSB = new StringBuilder();
			temptSB.append("[" + point[0] + "," + point[1]);
			try {
				temptSB.append("," + point[2]);
			} catch (Exception e) {
			}
			temptSB.append("]");
			geoContainer.add(temptSB.toString());
		});

		sb.append(String.join(",", geoContainer));
		sb.append("]]}");

		return Geometry.CreateFromJson(sb.toString());
	}

	public static Path2D CreatePath2D(List<Double[]> points) {
		Path2D outPath = new Path2D.Double();

		outPath.moveTo(points.get(0)[0], points.get(0)[1]);
		for (int index = 1; index < points.size(); index++) {
			outPath.lineTo(points.get(index)[0], points.get(index)[1]);
		}
		return outPath;
	}

	/*
	 * point
	 */
	public static Geometry pointToGeometry(Double[] point) {
		StringBuilder sb = new StringBuilder();
		sb.append("{  \"type\" : \"Point\" , \"coordinates\" : [");
		sb.append(point[0] + "," + point[1]);
		try {
			sb.append("," + point[2]);
		} catch (Exception e) {
		}
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

	public static Geometry CreateMultiPoint() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiPoint\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreatePoint(double x, double y) {
		return CreatePoint(x, y, 0);
	}

	public static Geometry CreatePoint(double x, double y, double z) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"Point\" , \"coordinates\" : [ " + x + "," + y + "," + z + " ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	/*
	 * line
	 */
	public static Geometry LineToGeometry(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"LineString\" , \"coordinates\" : [");

		List<String> geoContainer = new ArrayList<>();
		points.forEach(point -> {
			StringBuilder temptSB = new StringBuilder();
			temptSB.append("[" + point[0] + "," + point[1]);
			try {
				temptSB.append("," + point[2]);
			} catch (Exception e) {
			}
			temptSB.append("]");
			geoContainer.add(temptSB.toString());
		});

		sb.append(String.join(",", geoContainer));
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

	public static Geometry CreateMultiLine() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"MultiLineString\" , \"coordinates\" : [  ]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreateLine(List<Double[]> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"type\" : \"LineString\" , \"coordinates\" : [ ");

		List<String> pointsCoordinates = new ArrayList<>();
		points.forEach(point -> {
			StringBuilder temptCoordinate = new StringBuilder();
			temptCoordinate.append("[");
			temptCoordinate.append(point[0]);
			temptCoordinate.append(",");
			temptCoordinate.append(point[1]);

			try {
				double z = point[2];
				temptCoordinate.append(",");
				temptCoordinate.append(z);
			} catch (Exception e) {
				temptCoordinate.append(",0.0");
			}

			temptCoordinate.append("]");
			pointsCoordinates.add(temptCoordinate.toString());
		});

		sb.append(String.join(",", pointsCoordinates));
		sb.append("]}");
		return Geometry.CreateFromJson(sb.toString());
	}

	public static Geometry CreateLine(double x1, double y1, double x2, double y2) {
		return CreateLine(x1, y1, 0, x2, y2, 0);
	}

	public static Geometry CreateLine(double x1, double y1, double z1, double x2, double y2, double z2) {
		List<Double[]> temptList = new ArrayList<>();
		temptList.add(new Double[] { x1, y1, z1 });
		temptList.add(new Double[] { x2, y2, z2 });
		return CreateLine(temptList);
	}

	/*
	 * editor
	 */
	public static Geometry GeometryPanel(Geometry geometry, double deltaX, double deltaY, double deltaZ) {
		Geometry temptGeo = geometry.Clone();

		// for multiPolygon
		if (geometry.GetGeometryName().toUpperCase().contains("MULT")) {

			for (int multiIndex = 0; multiIndex < geometry.GetGeometryCount(); multiIndex++) {
				Geometry temptSingleGeo = temptGeo.GetGeometryRef(multiIndex);

				for (int geoCount = 0; geoCount < temptSingleGeo.GetGeometryCount(); geoCount++) {
					Geometry temptPoly = temptSingleGeo.GetGeometryRef(geoCount);

					for (int pointCount = 0; pointCount < temptPoly.GetPointCount(); pointCount++) {
						double zValue;
						try {
							zValue = temptPoly.GetZ(pointCount);
						} catch (Exception e) {
							zValue = 0.0;
						}
						temptPoly.SetPoint(pointCount, temptPoly.GetX(pointCount) + deltaX,
								temptPoly.GetY(pointCount) + deltaY, zValue + deltaZ);
					}
				}
			}

			// for single
		} else {
			for (int geoCount = 0; geoCount < temptGeo.GetGeometryCount(); geoCount++) {
				Geometry temptPoly = temptGeo.GetGeometryRef(geoCount);

				for (int pointCount = 0; pointCount < temptPoly.GetPointCount(); pointCount++) {
					double zValue;
					try {
						zValue = temptPoly.GetZ(pointCount);
					} catch (Exception e) {
						zValue = 0.0;
					}
					temptPoly.SetPoint(pointCount, temptPoly.GetX(pointCount) + deltaX,
							temptPoly.GetY(pointCount) + deltaY, zValue + deltaZ);
				}
			}
		}

		return temptGeo;
	}

	// <===================================================>
	// <======== GDAL PROCESSING ==============================>
	// <===================================================>
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

	public static List<Geometry> GeometryToPointGeos(Geometry geo) {
		Set<String> coordinateKeys = GeometryToPointKeySet_Z(geo, dataDecimale);

		List<Geometry> outGeoList = new ArrayList<>();
		coordinateKeys.forEach(pointKey -> {
			String[] point = pointKey.split("_");
			double x = Double.parseDouble(point[0]);
			double y = Double.parseDouble(point[1]);
			double z = Double.parseDouble(point[2]);
			outGeoList.add(GdalGlobal.CreatePoint(x, y, z));
		});

		return outGeoList;
	}

	public static List<Geometry> MultiPolyToSingle(Geometry multiPolygon) {
		List<Geometry> outList = new ArrayList<>();
		if (multiPolygon.GetGeometryName().toUpperCase().contains("MULTI")) {
			for (int index = 0; index < multiPolygon.GetGeometryCount(); index++) {
				GdalGlobal.MultiPolyToSingle(multiPolygon.GetGeometryRef(index)).forEach(geo -> outList.add(geo));
			}
		} else {
			outList.add(multiPolygon);
		}
		return outList;
	}

	public static List<Geometry> MultiPolyToSingle(List<Geometry> geos) {
		List<Geometry> outGeo = new ArrayList<>();
		geos.forEach(geo -> {
			MultiPolyToSingle(geo).forEach(temptGeo -> outGeo.add(temptGeo));
		});
		return outGeo;
	}

	public static Set<String> GeometryToPointKeySet(List<Geometry> geoList) {
		return GeometryToPointKeySet(geoList, GdalGlobal.dataDecimale);
	}

	public static Set<String> GeometryToPointKeySet(List<Geometry> geoList, int dataDecimale) {
		Set<String> verticeSet = new HashSet<>();
		geoList.forEach(geo -> {
			GeometryToPointKeySet(geo, dataDecimale).forEach(keySet -> {
				verticeSet.add(keySet);
			});
		});
		return verticeSet;
	}

	public static Set<String> GeometryToPointKeySet(Geometry geometry) {
		return GeometryToPointKeySet(geometry, GdalGlobal.dataDecimale);
	}

	public static Set<String> GeometryToPointKeySet_Z(Geometry geometry, int dataDecimale) {
		Set<String> coordinateKeys = new HashSet<>();

		MultiPolyToSingle(geometry).forEach(geo -> {

			// points
			if (geo.GetGeometryName().toUpperCase().contains("POINT")) {
				String xString = AtCommonMath.getDecimal_String(geo.GetX(), dataDecimale);
				String yString = AtCommonMath.getDecimal_String(geo.GetY(), dataDecimale);
				String zString;
				try {
					zString = AtCommonMath.getDecimal_String(geo.GetZ(), dataDecimale);
				} catch (Exception e) {
					zString = AtCommonMath.getDecimal_String(0, dataDecimale);
				}
				coordinateKeys.add(xString + "_" + yString + "_" + zString);

				// line
			} else if (geo.GetGeometryName().toUpperCase().contains("LineString")) {
				for (double[] point : geo.GetPoints()) {
					String xString = AtCommonMath.getDecimal_String(point[0], dataDecimale);
					String yString = AtCommonMath.getDecimal_String(point[1], dataDecimale);
					String zString;
					try {
						zString = AtCommonMath.getDecimal_String(point[2], dataDecimale);
					} catch (Exception e) {
						zString = AtCommonMath.getDecimal_String(0, dataDecimale);
					}
					coordinateKeys.add(xString + "_" + yString + "_" + zString);

				}

				// polygon
			} else if (geo.GetGeometryName().toUpperCase().contains("POLYGON")) {
				for (int index = 0; index < geo.GetGeometryCount(); index++) {
					Geometry temptGeo = geo.GetGeometryRef(index);
					for (double[] point : temptGeo.GetPoints()) {
						String xString = AtCommonMath.getDecimal_String(point[0], dataDecimale);
						String yString = AtCommonMath.getDecimal_String(point[1], dataDecimale);
						String zString;
						try {
							zString = AtCommonMath.getDecimal_String(point[2], dataDecimale);
						} catch (Exception e) {
							zString = AtCommonMath.getDecimal_String(0, dataDecimale);
						}
						coordinateKeys.add(xString + "_" + yString + "_" + zString);
					}
				}
			}
		});
		return coordinateKeys;
	}

	public static Set<String> GeometryToPointKeySet(Geometry geometry, int dataDecimale) {
		Set<String> coordinateKeys = new HashSet<>();

		MultiPolyToSingle(geometry).forEach(geo -> {
			String xString;
			String yString;

			// points
			if (geo.GetGeometryName().toUpperCase().contains("POINT")) {
				xString = AtCommonMath.getDecimal_String(geo.GetX(), dataDecimale);
				yString = AtCommonMath.getDecimal_String(geo.GetY(), dataDecimale);
				coordinateKeys.add(xString + "_" + yString);

				// line
			} else if (geo.GetGeometryName().toUpperCase().contains("LINESTRING")) {
				for (double[] point : geo.GetPoints()) {
					xString = AtCommonMath.getDecimal_String(point[0], dataDecimale);
					yString = AtCommonMath.getDecimal_String(point[1], dataDecimale);
					coordinateKeys.add(xString + "_" + yString);
				}

				// polygon
			} else if (geo.GetGeometryName().toUpperCase().contains("POLYGON")) {
				for (int index = 0; index < geo.GetGeometryCount(); index++) {
					Geometry temptGeo = geo.GetGeometryRef(index);
					for (double[] point : temptGeo.GetPoints()) {
						xString = AtCommonMath.getDecimal_String(point[0], dataDecimale);
						yString = AtCommonMath.getDecimal_String(point[1], dataDecimale);
						coordinateKeys.add(xString + "_" + yString);
					}
				}
			}
		});
		return coordinateKeys;
	}

	// <===================================================>
	// <======== JAVA PATH2D ADJUST FUNCTION ====================>
	// <===================================================>

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

	public static List<Path2D> GeomertyToPath2D(Geometry geometry) throws IOException {
		List<Path2D> outList = new ArrayList<>();

		GdalGlobal.MultiPolyToSingle(geometry).forEach(geo -> {
			if (geo.GetGeometryName().toUpperCase().equals("POLYGON")
					|| geo.GetGeometryName().toUpperCase().equals("POLYGONZ")) {
				GeomertyToPath2D_PolygonProcessing(geo).forEach(path2D -> outList.add(path2D));
			}
		});
		return outList;
	}

	private static List<Path2D> GeomertyToPath2D_PolygonProcessing(Geometry geometry) {
		List<Path2D> outList = new ArrayList<>();

		// get basic path
		for (int geoIndex = 0; geoIndex < geometry.GetGeometryCount(); geoIndex++) {

			Path2D outPath = new Path2D.Double();
			Geometry temptGeo = geometry.GetGeometryRef(geoIndex);
			double[] outPathStartPoint = new double[2];
			outPathStartPoint[0] = temptGeo.GetX(0);
			outPathStartPoint[1] = temptGeo.GetY(0);

			// picture first path
			outPath.moveTo(temptGeo.GetPoint(0)[0], temptGeo.GetPoint(0)[1]);
			for (int index = 1; index < temptGeo.GetPointCount(); index++) {
				outPath.lineTo(temptGeo.GetPoint(index)[0], temptGeo.GetPoint(index)[1]);
			}
			outPath.closePath();
			outList.add(outPath);
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

	// <===================================================>
	// <======== QGIS ENVIROMENT SETTING========================>
	// <===================================================>
	public static List<String> GDAL_EnviromentStarting() {
		List<String> outList = new ArrayList<>();
		outList.add("@echo off");
		outList.add("call \"" + gdalBinFolder + "\\o4w_env.bat\"");
		outList.add("call \"%OSGEO4W_ROOT%\\apps\\grass\\grass78\\etc\\env.bat\"");
		outList.add("call " + gdalBinFolder + "\\qt5_env.bat");
		outList.add("call " + gdalBinFolder + "\\py3_env.bat");
		outList.add("@echo off");
		outList.add(
				"path %OSGEO4W_ROOT%\\apps\\qgis-ltr\\bin;%OSGEO4W_ROOT%\\apps\\grass\\grass78\\lib;%OSGEO4W_ROOT%\\apps\\grass\\grass78\\bin;%PATH%");
		outList.add("set QGIS_PREFIX_PATH=%OSGEO4W_ROOT:\\=/%/apps/qgis-ltr");
		outList.add("set GDAL_FILENAME_IS_UTF8=YES");

		// for python
		outList.add("set QT_PLUGIN_PATH=%OSGEO4W_ROOT%\\apps\\qgis-ltr\\qtplugins;%OSGEO4W_ROOT%\\apps\\qt5\\plugins");
		outList.add("set PYTHONPATH=%OSGEO4W_ROOT%\\apps\\qgis-ltr\\python;%PYTHONPATH%");
		return outList;
	}

	public static List<String> QGIS_Processing_PythonInitialize() {
		List<String> outList = new ArrayList<>();
		outList.add("import sys");
		outList.add("sys.path.append(\"" + qgisProcessingPluigins.replace("\\", "/") + "\")");

		outList.add("from qgis.core import (");
		outList.add("     QgsApplication,");
		outList.add("     QgsProcessingFeedback,");
		outList.add("     QgsVectorLayer");
		outList.add(")");
		outList.add("import qgis.utils");

		outList.add("QgsApplication.setPrefixPath('/usr', True)");
		outList.add("qgs = QgsApplication([], False)");
		outList.add("qgs.initQgis()");

		outList.add("import processing");
		outList.add("from qgis.analysis import QgsNativeAlgorithms");

		outList.add("from processing.core.Processing import Processing");
		outList.add("Processing.initialize()");
		outList.add("QgsApplication.processingRegistry().addProvider(QgsNativeAlgorithms())");

		return outList;
	}

	public static String getTempFileName(String folder, String additionFormat) {
		StringBuilder temptName = new StringBuilder();

		RandomMaker radom = new RandomMaker();
		for (int index = 0; index < 10; index++) {
			temptName.append(radom.RandomInt(0, 9));
		}
		String temptWholeName = temptName.toString() + additionFormat;

		if (new File(folder + temptWholeName).exists()) {
			return getTempFileName(folder, additionFormat);
		} else {
			return temptWholeName;
		}
	}

	public static String createTemptFolder(String preFixName) {
		String folderPath = GdalGlobal.temptFolder + preFixName + GdalGlobal.getTempFileName(GdalGlobal.temptFolder, "")
				+ "\\";
		FileFunction.newFolder(folderPath);
		for (String fileName : new File(folderPath).list()) {
			FileFunction.delete(folderPath + "\\" + fileName);
		}
		return folderPath;
	}

	public static String extensionAutoDetect(String filePath) {
		String extension = FilenameUtils.getExtension(filePath).toLowerCase();

		switch (extension) {
		case "shp":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_ESRIShapefile;
		case "json":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_GeoJSON;
		case "geojson":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_GeoJSON;
		case "kml":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_KML;
		case "nc":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_netCDF;
		case "dwg":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_DWG;
		case "cad":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_CAD;
		case "csv":
			return GdalGlobal_DataFormat.DATAFORMAT_VECTOR_CSV;
		case "asc":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_AAIGrid;
		case "tif":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_GTiff;
		case "gettif":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_GTiff;
		case "tiff":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_GTiff;
		case "geotiff":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_GTiff;
		case "png":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_PNG;
		case "jpg":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_JPEG;
		case "jpeg":
			return GdalGlobal_DataFormat.DATAFORMAT_RASTER_JPEG;
		default:
			new Exception("no avalible extension");
			return "";
		}
	}

}
