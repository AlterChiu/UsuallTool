package geo.gdal;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import org.gdal.ogr.Geometry;

public class GdalGlobal {

	public static String gdalBinFolder = "C:\\code\\JAVA library\\gdal\\bin\\";

	public static Path2D GeomertyToPath2D(Geometry geometry) {
		Path2D outPath = new Path2D.Double();
		double[][] points = geometry.GetBoundary().GetPoints();

		outPath.moveTo(points[0][0], points[0][1]);
		for (int index = 1; index < points.length; index++) {
			outPath.lineTo(points[index][0], points[index][1]);
		}
		return outPath;
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
