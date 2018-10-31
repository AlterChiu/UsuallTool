package testFolder;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import geo.gdal.SpatialWriter;
import usualTool.AtFileWriter;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method
		String fileAdd = "F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\temptAscii.asc";
		String saveAdd = "F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\temptAscii.geoJson";

//		gdal.AllRegister();
//		Driver dataSourceDriver = ogr.GetDriverByName("Geojson");
//		DataSource outDataSource = dataSourceDriver.CreateDataSource(saveAdd);
		List<String> outList = new ArrayList<String>();
		outList.add("x,y");

		Path2D path = new AsciiToPath(new AsciiBasicControl(fileAdd)).getAsciiPath();
		PathIterator pathIt = path.getPathIterator(null);

		float coordinate[] = new float[6];
		for (; pathIt.isDone(); pathIt.next()) {
			pathIt.currentSegment(coordinate);

			outList.add(coordinate[0] + "," + coordinate[1]);
		}

		new AtFileWriter(outList.parallelStream().toArray(String[]::new),
				"F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\temptAscii.csv").csvWriter();

//		SpatialWriter spWriter = new SpatialWriter(path);
//		spWriter.saveAsGeoJson(saveAdd);
	}
}