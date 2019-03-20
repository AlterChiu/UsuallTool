package testFolder;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_EV1;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LN3;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LPT3;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_PT3;
import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stud
		List<Double> outList = new ArrayList<>();
		Arrays.asList(getRainfallList()).forEach(e -> outList.add(e));

		System.out.println("EV1\t" + new ReturnPeriod_EV1(outList).getPeriodRainfall(1000));
		System.out.println("LN3\t" + new ReturnPeriod_LN3(outList).getPeriodRainfall(1000));
		System.out.println("LPT3\t" + new ReturnPeriod_LPT3(outList).getPeriodRainfall(1000));
		System.out.println("PT3\t" + new ReturnPeriod_PT3(outList).getPeriodRainfall(1000));
	}

	private static Double[] getRainfallList() {
		return new Double[] { 188.109990, 297.169980, 192.529980, 212.770000, 145.850010, 218.919980, 427.820010,
				168.390000, 221.880000, 243.280010, 345.590000 };
	}

}
