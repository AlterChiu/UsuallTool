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

public class GdalGlobal {
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;

	public static String gdalBinFolder = "C:\\code\\JAVA library\\gdal\\bin\\";

	public static String sagaBinFolder = "F:/Qgis/apps/saga-ltr/";
	
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

}
