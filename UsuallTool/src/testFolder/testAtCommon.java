package testFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import FEWS.PIXml.AtPiXmlReader;
import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import usualTool.AtFileWriter;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;

public class testAtCommon {

	public static void main(String[] args) throws OperationNotSupportedException, IOException {
		// TODO Auto-generated method
//		String fileAdd = "F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\observed.xml";
		String fileAdd = "F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\simulation.xml";

		AtPiXmlReader piReader = new AtPiXmlReader();
		List<String> outList = new ArrayList<String>();

		List<TimeSeriesArray> seriesArray = piReader.getTimeSeriesArrays(fileAdd);
		seriesArray.forEach(series -> {
			if (series.getHeader().getLocationId().equals("1156")) {
				for (int index = 0; index < series.size(); index++) {
					outList.add(series.getValue(index) + "");
				}
			}
		});

		new AtFileWriter(outList.parallelStream().toArray(String[]::new),
				"F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\simulation_Tanshiu.txt").tabWriter();
		
//		new AtFileWriter(outList.parallelStream().toArray(String[]::new),
//				"F:\\FEWS\\FEWS_Taiwan_2017\\Taiwan\\Export\\Alter\\observed_Tanshiu.txt").tabWriter();

	}
}