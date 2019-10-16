package testFolder;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String ascFolder = "E:\\LittleProject\\UserMeeting - 2019\\qgis\\modelBoundary\\asc\\";
		String geoFolder = "E:\\LittleProject\\UserMeeting - 2019\\qgis\\modelBoundary\\geoJson\\";

		for (String fileName : new File(ascFolder).list()) {
			Path2D temptPath = new AsciiToPath(ascFolder + fileName).getAsciiPath();

			List<Geometry> geoList = new ArrayList<>();
			geoList.add(GdalGlobal.Path2DToGeometry(temptPath));

			new SpatialWriter().setGeoList(geoList).saveAsGeoJson(geoFolder + fileName);

		}
	}
}
