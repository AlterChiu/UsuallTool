package testFolder;

import java.io.File;

import org.gdal.ogr.Geometry;

import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileAdd = "H:\\RainfallData\\catchment\\119.9687_23.4562\\";
		for(String fileName : new File(fileAdd).list()) {
			System.out.println("\"" + fileName + "\",");
		}

	}

}
