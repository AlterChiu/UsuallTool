package geo.gdal.vector;

import org.gdal.ogr.Geometry;

import geo.gdal.SpatialReader;

public class GDAL_VECTOR_ToRaster {
	
	public GDAL_VECTOR_ToRaster() {
		Geometry geo = new SpatialReader("").getGeometryList().get(0);
	}
}
