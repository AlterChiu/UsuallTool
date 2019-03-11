package testFolder;

import org.gdal.ogr.Geometry;

import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpatialReader spReader = new SpatialReader("H:\\SHP\\東門溪測試/dongman_merge(Sobek).shp");
		System.out.println(spReader.getGeometryList().get(0).ExportToJson());

	}

}
