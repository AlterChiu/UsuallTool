
package FEWS.netcdf;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import geo.gdal.RasterReader;
import netCDF.NetcdfWriter;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import usualTool.TimeTranslate;

public class FewsNetcdfWriter {

	public static void createFloodDepth(String fileAdd, List<RasterReader> rasters, List<Long> dateList)
			throws IOException, InvalidRangeException, ParseException {
		NetcdfWriter writer = new NetcdfWriter(fileAdd);
		List<Double> xList = rasters.get(0).getXList();
		List<Double> yList = rasters.get(0).getYList();

		// set dimension
		// <==========================================>
		writer.addDimension("time", rasters.size());
		writer.addDimension("x", xList.size());
		writer.addDimension("y", yList.size());

		// set variables
		// <==========================================>
		writer.addVariable("crs", DataType.INT);
		writer.addVariableAttribute("crs", "long_name", "coordinate reference system");
		writer.addVariableAttribute("crs", "grid_mapping_name", "latitude_longitude");
		writer.addVariableAttribute("crs", "longitude_of_prime_meridian", 0.0);
		writer.addVariableAttribute("crs", "semi_major_axis", 6378137.0);
		writer.addVariableAttribute("crs", "inverse_flattening", 298.257223563);
		writer.addVariableAttribute("crs", "crs_wkt",
				"GEOGCS[\"WGS 84\",\nDATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],\nPRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],\nUNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],\nAUTHORITY[\"EPSG\",\"4326\"]]");
		writer.addVariableAttribute("crs", "proj4_params", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
		writer.addVariableAttribute("crs", "epsg_code", "EPSG:4326");

		writer.addVariable("time", DataType.DOUBLE, "time");
		writer.addVariableAttribute("time", "standard_name", "time");
		writer.addVariableAttribute("time", "long_name", "time");
		writer.addVariableAttribute("time", "units", "minutes since 1970-01-01 00:00:00.0 +0000");
		writer.addVariableAttribute("time", "axis", "T");

		writer.addVariable("y", DataType.DOUBLE, "y");
		writer.addVariableAttribute("y", "standard_name", "latitude");
		writer.addVariableAttribute("y", "long_name", "y coordinate according to WGS 1984");
		writer.addVariableAttribute("y", "units", "degrees_north");
		writer.addVariableAttribute("y", "axis", "Y");
		writer.addVariableAttribute("y", "_FillValue", 9.96921E36);

		writer.addVariable("x", DataType.DOUBLE, "x");
		writer.addVariableAttribute("x", "standard_name", "longitude");
		writer.addVariableAttribute("x", "long_name", "x coordinate according to WGS 1984");
		writer.addVariableAttribute("x", "units", "degrees_east");
		writer.addVariableAttribute("x", "axis", "X");
		writer.addVariableAttribute("x", "_FillValue", 9.96921E36);

		writer.addVariable("depth_below_surface_simulated", DataType.FLOAT, "time y  x");
		writer.addVariableAttribute("depth_below_surface_simulated", "long_name", "depth_below_surface_simulated");
		writer.addVariableAttribute("depth_below_surface_simulated", "units", "m");
		writer.addVariableAttribute("depth_below_surface_simulated", "_FillValue", -999.0f);
		writer.addVariableAttribute("depth_below_surface_simulated", "grid_mapping", "crs");

		writer.create();

		// set values
		// <==========================================>

		// y
		writer.addValue("y", ArrayDouble.factory(yList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

		// x
		writer.addValue("x", ArrayDouble.factory(xList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

		// time
		List<Double> timeList = new ArrayList<>();
		long startTime = TimeTranslate.getDateLong("1970-01-01 08:00", "yyyy-MM-dd HH:mm");
		for (long temptDate : dateList) {
			timeList.add((double) (temptDate - startTime) / (1000 * 60));
		}
		writer.addValue("time",
				ArrayDouble.factory(timeList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

		// depth_below_surface_simulated
		double[][][] depthList = new double[dateList.size()][yList.size()][];
		for (int time = 0; time < dateList.size(); time++) {
			for (int row = 0; row < yList.size(); row++) {

				List<Double> temptList = new ArrayList<>();
				for (int column = 0; column < xList.size(); column++) {
					temptList.add(0.0);
				}
				depthList[time][row] = temptList.parallelStream().mapToDouble(Double::doubleValue).toArray();
			}
		}
		writer.addValue("depth_below_surface_simulated", ArrayDouble.factory(depthList));

		writer.close();
	}

	public static void createFloodDepth(String fileAdd, RasterReader raster, long date)
			throws IOException, InvalidRangeException, ParseException {
		List<RasterReader> rasterList = new ArrayList<>();
		rasterList.add(raster);

		List<Long> dateList = new ArrayList<>();
		dateList.add(date);

		FewsNetcdfWriter.createFloodDepth(fileAdd, rasterList, dateList);
	}

}
