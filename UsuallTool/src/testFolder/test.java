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

import FEWS.netcdf.DflowNetcdfTranslator;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import geo.common.GdalTranslater;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import netCDF.NetcdfBasicControl;
import ucar.ma2.InvalidRangeException;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args) throws IOException, InterruptedException, InvalidRangeException {
		// TODO Auto-generated method stub

		NetcdfBasicControl nc = new NetcdfBasicControl(
				"E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Tin\\0.5m_800m2\\TIN_05m_800m2_Clip.nc");
//		nc.getNetFile().getVariables().forEach(v -> System.out.println(v));

		List<String> outList = new ArrayList<>();

		List<Object> xList = nc.getVariableValues("mesh2d_node_x");
		List<Object> yList = nc.getVariableValues("mesh2d_node_y");
		List<Object> zList = nc.getVariableValues("mesh2d_node_z");

		for (int index = 0; index < zList.size(); index++) {
//			List<Double> temptValues = (List<Double>) zList.get(index);
//			for (Integer value : temptValues) {
//				if (value == 0) {
//					outList.add(xList.get(index) + "," + yList.get(index));
//				}
//			}

			if ((Double) zList.get(index) < -10) {
				outList.add(xList.get(0) + "," + yList.get(index));
			}
		}
		new AtFileWriter(outList.parallelStream().toArray(String[]::new),
				"E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Tin\\0.5m_800m2\\xyz0.csv").textWriter("");

//		translateToNC();
	}

	private static void translateToNC() throws IOException, InvalidRangeException {
		DflowNetcdfTranslator trans = new DflowNetcdfTranslator(new SpatialReader(
				"E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Tin\\0.5m_800m2\\TIN_05m_800m2_Clip.shp"));

//		trans = trans.checkOverlappingShp();

		trans.set_node_Z_value(new AsciiBasicControl("E:\\Dflow-FM\\test\\Source\\ZoneU1_20m_Fill.asc"));

		String asciiFolder = "E:\\Dflow-FM\\test\\Source\\HydroDem\\asc\\";
		for (String asciiName : new File(asciiFolder).list()) {
			trans.set_node_Z_value(new AsciiBasicControl(asciiFolder + asciiName));
		}

		trans.saveAs("E:\\Dflow-FM\\test\\Source\\TestCase\\SobekZone1_Tin\\0.5m_800m2\\TIN_05m_800m2_Clip.nc");

	}
}
