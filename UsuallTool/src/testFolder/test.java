package testFolder;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import com.google.common.collect.FluentIterable;

import FEWS.Rinfall.BUI.BuiTranslate;
import FEWS.netcdf.DflowNetcdfTranslator;
import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import netCDF.NetcdfBasicControl;
import netCDF.NetcdfWriter;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class test {
	public static void main(String[] args)
			throws IOException, ParseException, InvalidRangeException, OperationNotSupportedException, InterruptedException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		List<String> command = new ArrayList<String>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("F:\\Sobek213\\MapReduce_Batch_TainanZ1.bat");
		command.add("exit");

		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File("F:\\Sobek213"));
		builder.command(command);
		Process process = builder.start();
		process.waitFor();

		long endTime = System.currentTimeMillis();
		System.out.print(endTime - startTime);

	}

}
