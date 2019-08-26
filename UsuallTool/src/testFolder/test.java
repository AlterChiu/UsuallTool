package testFolder;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.formula.functions.Value;

import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import netCDF.NetcdfBasicControl;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class test {
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		NetcdfFileWriter writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, "E:\\download\\net.nc");
		// add dimensions
		Dimension latDim = writer.addDimension(null, "lat", 64);
		Dimension lonDim = writer.addDimension(null, "lon", 128);

		// add Variable double temperature(lat,lon)
		List<Dimension> dims = new ArrayList<Dimension>();
		dims.add(latDim);
		dims.add(lonDim);
		Variable t = writer.addVariable(null, "temperature", DataType.DOUBLE, dims);
		t.addAttribute(new Attribute("units", "K")); // add a 1D attribute of length 3
		t.addAttribute(new Attribute("scale", 1.2));

		// add a string-valued variable: char svar(80)
		Dimension svar_len = writer.addDimension(null, "svar_len", 80);
		writer.addVariable(null, "svar", DataType.CHAR, "svar_len");

		// add a 2D string-valued variable: char names(names, 80)
		Dimension names = writer.addDimension(null, "names", 3);
		writer.addVariable(null, "names", DataType.CHAR, "names lat");

		// add a scalar variable
		writer.addVariable(null, "scalar", DataType.DOUBLE, new ArrayList<Dimension>());

		// add global attributes
		writer.addGroupAttribute(null, new Attribute("yo", "face"));
		writer.addGroupAttribute(null, new Attribute("versionD", 1.2));
		writer.addGroupAttribute(null, new Attribute("versionF", (float) 1.2));
		writer.addGroupAttribute(null, new Attribute("versionI", 1));
		writer.addGroupAttribute(null, new Attribute("versionS", (short) 2));
		writer.addGroupAttribute(null, new Attribute("versionB", (byte) 3));

		// create the file
		try {
			writer.create();
		} catch (IOException e) {
		}
		writer.close();

		/*
		 * read
		 */
		NetcdfBasicControl nc = new NetcdfBasicControl(
				"E:\\LittleProject\\Dflow-FM\\test\\case netCdfCreation\\Project1.dsproj_data\\FlowFM\\input\\2x2Grid.nc");
		nc.getNetFile().getDimensions().forEach(e->{
			System.out.println(e);
		});;
	}

}
