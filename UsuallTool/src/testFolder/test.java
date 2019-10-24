package testFolder;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import netCDF.NetcdfBasicControl;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		NetcdfBasicControl nc = new NetcdfBasicControl(
//				"E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Hydro3_94193079\\output\\HydroMerge20m.nc");
//
//		nc.getNetFile().getVariables().forEach(e -> System.out.println(e));
//
//		List<String> temptList = new ArrayList<>();
//		List<Object> zList = nc.getVariableValues("mesh2d_node_z");
//		List<Object> xList = nc.getVariableValues("mesh2d_node_x");
//		List<Object> yList = nc.getVariableValues("mesh2d_node_y");
//
//		for (int index = 0; index < zList.size(); index++) {
//			if ((Double) (zList.get(index)) < -990) {
//				temptList.add(xList.get(index) + "," + yList.get(index));
//			}
//		}
//
//		new AtFileWriter(temptList.parallelStream().toArray(String[]::new), "E:\\download\\tempt.txt").textWriter(" ");

		AsciiBasicControl ascii = new AsciiBasicControl(
				"E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Hydro3_94193079\\input\\94193079.asc");

		ascii.setValue(170584.9, 2551599.7, "-0.78");
		ascii.setValue(170590.6, 2551599.3, "-0.78");
		ascii.setValue(170585.7, 2551594.7, "-0.78");
		ascii.setValue(170591.9, 2551599.7, "-0.78");
		ascii.setValue(170596.9, 2551599.7, "-0.78");
		ascii.setValue(170601.9, 2551599.7, "-0.78");
		ascii.setValue(170606.9, 2551599.7, "-0.78");
		
		ascii.setValue(170584.9, 2551594.7, "-0.78");
		ascii.setValue(170590.6, 2551594.3, "-0.78");
		ascii.setValue(170591.9, 2551594.7, "-0.78");
		ascii.setValue(170596.9, 2551594.7, "-0.78");
		ascii.setValue(170601.9, 2551594.7, "-0.78");
		ascii.setValue(170606.9, 2551594.7, "-0.78");

		ascii.setValue(170579.0, 2551589.7, "-0.78");
		ascii.setValue(170584.1, 2551589.7, "-0.78");
		ascii.setValue(170589.1, 2551589.7, "-0.78");
		ascii.setValue(170594.1, 2551589.7, "-0.78");
		ascii.setValue(170599.1, 2551589.7, "-0.78");
		ascii.setValue(170501.1, 2551589.7, "-0.78");

		ascii.setValue(170574.0, 2551582.7, "-0.78");
		ascii.setValue(170579.0, 2551582.7, "-0.78");
		ascii.setValue(170584.1, 2551582.7, "-0.78");
		ascii.setValue(170589.1, 2551582.7, "-0.78");
		ascii.setValue(170594.1, 2551582.7, "-0.78");
		ascii.setValue(170599.1, 2551582.7, "-0.78");
		ascii.setValue(170501.1, 2551582.7, "-0.78");

		ascii.setValue(170574.0, 2551577.7, "-0.78");
		ascii.setValue(170579.0, 2551577.7, "-0.78");
		ascii.setValue(170584.1, 2551577.7, "-0.78");
		ascii.setValue(170589.1, 2551577.7, "-0.78");
		ascii.setValue(170594.1, 2551577.7, "-0.78");
		ascii.setValue(170599.1, 2551577.7, "-0.78");
		
		ascii.setValue(170569.0, 2551572.7, "-0.78");
		ascii.setValue(170574.0, 2551572.7, "-0.78");
		ascii.setValue(170579.0, 2551572.7, "-0.78");
		ascii.setValue(170584.1, 2551572.7, "-0.78");
		ascii.setValue(170589.1, 2551572.7, "-0.78");
		ascii.setValue(170594.1, 2551572.7, "-0.78");
		
		ascii.setValue(170564.0, 2551567.7, "-0.78");
		ascii.setValue(170569.0, 2551567.7, "-0.78");
		ascii.setValue(170574.0, 2551567.7, "-0.78");
		ascii.setValue(170579.0, 2551567.7, "-0.78");
		ascii.setValue(170584.1, 2551567.7, "-0.78");
		ascii.setValue(170589.1, 2551567.7, "-0.78");
		
		ascii.setValue(170564.0, 2551562.7, "-0.78");
		ascii.setValue(170569.0, 2551562.7, "-0.78");
		ascii.setValue(170574.0, 2551562.7, "-0.78");
		ascii.setValue(170579.0, 2551562.7, "-0.78");
		ascii.setValue(170584.1, 2551562.7, "-0.78");
		ascii.setValue(170589.1, 2551562.7, "-0.78");
		
		ascii.setValue(170559.1, 2551557.7, "-0.78");
		ascii.setValue(170564.0, 2551557.7, "-0.78");
		ascii.setValue(170569.0, 2551557.7, "-0.78");
		ascii.setValue(170574.0, 2551557.7, "-0.78");
		ascii.setValue(170579.0, 2551557.7, "-0.78");
		ascii.setValue(170584.1, 2551557.7, "-0.78");
		
		
		ascii.setValue(170554.1, 2551552.7, "-0.78");
		ascii.setValue(170559.1, 2551552.7, "-0.78");
		ascii.setValue(170564.0, 2551552.7, "-0.78");
		ascii.setValue(170569.0, 2551552.7, "-0.78");
		ascii.setValue(170574.0, 2551552.7, "-0.78");
		ascii.setValue(170579.0, 2551552.7, "-0.78");
	

		

		new AtFileWriter(ascii.getAsciiFile(),
				"E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Hydro3_94193079\\input\\94193079_BrigeOut.asc")
						.textWriter(" ");

	}
}
