package testFolder;

import org.gdal.ogr.Geometry;

import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpatialReader spReader = new SpatialReader("H:/SHP/dongman_merge(Sobek).shp");
		Geometry polygon = spReader.getGeometryList().get(0);

		for (double[] point : polygon.GetBoundary().GetPoints()) {
			System.out.println(point[0] + "\t" + point[1]);
		}

	}

}
