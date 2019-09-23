package testFolder;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.gdal.ogr.Geometry;

import com.google.common.collect.FluentIterable;

import FEWS.Rinfall.BUI.BuiTranslate;
import FEWS.netcdf.DflowNetcdfTranslator;
import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
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
import usualTool.FileFunction;

public class test {
	public static void main(String[] args) throws IOException, ParseException, InvalidRangeException,
			OperationNotSupportedException, InterruptedException {
		// TODO Auto-generated method stub
		String folder = "F:\\DFX\\DevRelease\\DFX\\Video\\1568779670\\";
		SpatialReader sr = new SpatialReader("E:\\download\\SHP\\94193096_poly.shp");

	}

}
