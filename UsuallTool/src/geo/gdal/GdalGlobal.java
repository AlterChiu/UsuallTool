package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import org.gdal.ogr.Geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GdalGlobal {

	public static String gdalBinFolder = "C:\\code\\JAVA library\\gdal\\bin\\";

	public static Path2D GeomertyToPath2D(Geometry geometry) {
		JsonObject geoJson = new JsonParser().parse(geometry.ExportToJson()).getAsJsonObject();
		String polygonType = geoJson.get("type").getAsString();

		Path2D outPath = new Path2D.Double();
		if (polygonType.toUpperCase().equals("POLYGON")) {
			/*
			 * single polygon type
			 */
			JsonArray coordinates = geoJson.get("coordinate").getAsJsonArray().get(0).getAsJsonArray();
			double startX = coordinates.get(0).getAsJsonArray().get(0).getAsDouble();
			double startY = coordinates.get(0).getAsJsonArray().get(1).getAsDouble();
			outPath.moveTo(startX, startY);

			for (int index = 1; index < coordinates.size() - 1; index++) {
				double temptX = coordinates.get(index).getAsJsonArray().get(0).getAsDouble();
				double temptY = coordinates.get(index).getAsJsonArray().get(1).getAsDouble();
				outPath.lineTo(temptX, temptY);
			}
			return outPath;

		} else if (polygonType.toUpperCase().equals("MULTIPOLYGON")) {
			System.out.println("MULTIPOLYGON");
			return null;
		} else {
			System.out.println("notPOLYGON");
			return null;
		}

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
