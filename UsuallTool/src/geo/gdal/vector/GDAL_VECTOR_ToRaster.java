
package geo.gdal.vector;

import java.io.UnsupportedEncodingException;
import org.gdal.ogr.Geometry;
import geo.gdal.SpatialReader;

public class GDAL_VECTOR_ToRaster {

	public GDAL_VECTOR_ToRaster() throws UnsupportedEncodingException {
		Geometry geo = new SpatialReader("").getGeometryList().get(0);
	}
}
