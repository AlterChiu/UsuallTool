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
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiToPath;
import geo.gdal.SpatialWriter;
import usualTool.AtFileWriter;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method
		String fileAdd = "F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\temptAscii.asc";
		String saveAdd = "F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\temptAscii.geoJson";
		
		 SpatialWriter spWriter = new SpatialWriter(new AsciiToPath(fileAdd).getAsciiPath());
		 spWriter.setCoordinateSystem(SpatialWriter.TWD97_121);
		 
		spWriter.saveAsGeoJson(saveAdd);
	}
}