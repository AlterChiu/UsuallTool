package testFolder;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.FluentIterable;

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

public class test {
	public static void main(String[] args) throws IOException, ParseException, InvalidRangeException {
		// TODO Auto-generated method stub

		String testAdd = "E:\\LittleProject\\Dflow-FM\\sobek\\adaptor\\sobek\\testAscii.asc";
		String testSave = "E:\\LittleProject\\Dflow-FM\\sobek\\adaptor\\sobek\\testAscii.nc";
		String originalAdd = "E:\\LittleProject\\Dflow-FM\\test\\case netCdfCreation\\Project1.dsproj_data\\FlowFM\\input\\2X2Grid.nc";

		DflowNetcdfTranslator dflow = new DflowNetcdfTranslator(new AsciiBasicControl(testAdd));
		dflow.saveAs(testSave);

		NetcdfBasicControl testNc = new NetcdfBasicControl(originalAdd);
		testNc.getNetFile().getVariables().forEach(e -> {
			System.out.println(e);
		});

//		String[] content_or = new AtFileReader("E:\\LittleProject\\Dflow-FM\\sobek\\adaptor\\sobek\\or.txt")
//				.getContain();
//		String[] content_te = new AtFileReader("E:\\LittleProject\\Dflow-FM\\sobek\\adaptor\\sobek\\test.text")
//				.getContain();
//		for (int index = 0; index < content_or.length; index++) {
//			if (!content_or[index].equals(content_te[index])) {
//				System.out.println(index + "\t" + content_or[index]);
//			}
//		}
		
	}

}
