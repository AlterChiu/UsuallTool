package testFolder;

import java.awt.Rectangle;
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
import org.gdal.ogr.ogr;

import FEWS.netcdf.DflowNetcdfTranslator;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import geo.common.GdalTranslater;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import netCDF.NetcdfBasicControl;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class Dflow_Adaptor {

	public static void main(String[] args) throws IOException, InterruptedException, InvalidRangeException {
		// TODO Auto-generated method stub

		// Zone1
		String SCS_Boundary = "E:\\Dflow-FM\\test\\Source\\Zone1_Source\\SobekZ1_Boundary_Polygon.json";// 不用動
		String SCS_Property = "E:\\Dflow-FM\\test\\Source\\Zone1_Source\\Zone1_ScsNode_List";// 不用動
		String SCS_Output = "E:\\Dflow-FM\\test\\Source\\ScsNode\\zone1\\20170730\\";// 更改事件專案
		String SCS_Value = "E:\\Dflow-FM\\test\\Source\\Zone1_Source\\input\\TNZ1_20170730_scsNode.csv";// 更改事件專案
		GenerateScsNode(SCS_Boundary, SCS_Output, SCS_Property, SCS_Value);
//
//		double targetMinX = 169316;// 更改位置
//		double targetMaxX = 171830;// 更改位置
//		double targetMinY = 2550409;// 更改位置
//		double targetMaxY = 2552885;// 更改位置
//		String NC_BaseAscii = "E:\\Dflow-FM\\sobek\\asc\\Zone1\\ZoneU1_20m_Fill.asc";// 不用動
//		String NC_OutputFolder = "E:\\Dflow-FM\\test\\Source\\Zone1_Source\\yanShiuRiver\\Hydro3_5m\\output\\";// 更改成輸出傳案
//		String NC_TargetAscii = "E:\\Dflow-FM\\test\\Source\\Zone1_Source\\yanShiuRiver\\Hydro3_5m\\input\\merge.asc";// 更改讀取專案
//		GenerateNetcdfFile(targetMinX, targetMaxX, targetMinY, targetMaxY, NC_OutputFolder, NC_BaseAscii,
//				NC_TargetAscii);

		// Zone3
//		String SCS_Boundary = "E:\\Dflow-FM\\test\\Source\\Zone3_Source\\boundary.geoJson";// 不用動
//		String SCS_Property = "E:\\Dflow-FM\\test\\Source\\Zone3_Source\\zone3_ScsNodeList";// 不用動
//		String SCS_Output = "E:\\Dflow-FM\\test\\Source\\ScsNode\\zone3\\20180823\\";// 更改事件專案
//		String SCS_Value = "E:\\Dflow-FM\\test\\Source\\Zone3_Source\\input\\20180823_TNZ3_ScsNode.csv";// 更改事件專案
//		GenerateScsNode(SCS_Boundary, SCS_Output, SCS_Property, SCS_Value);
//
//		double targetMinX = 167055;// 更改位置
//		double targetMaxX = 169532;// 更改位置
//		double targetMinY = 2535630;// 更改位置
//		double targetMaxY = 2537118;// 更改位置
//		String NC_BaseAscii = "E:\\Dflow-FM\\test\\Source\\Zone3_Source\\zone3(New20m_DEM).asc";// 不用動
//		String NC_OutputFolder = "E:\\Dflow-FM\\test\\Source\\Zone3_Source\\threeGrandpaRiver\\HyDEM2_5m\\output\\";// 更改成輸出傳案
//		String NC_TargetAscii = "E:\\Dflow-FM\\test\\Source\\Zone3_Source\\threeGrandpaRiver\\HyDEM2_5m\\input\\merge.asc";// 更改讀取專案
//		GenerateNetcdfFile(targetMinX, targetMaxX, targetMinY, targetMaxY, NC_OutputFolder, NC_BaseAscii,
//				NC_TargetAscii);

	}

	private static void GenerateScsNode(String SCS_Boundary, String SCS_Output, String SCS_Property, String SCS_Value)
			throws IOException {

		String outputFolder = SCS_Output;
		Geometry bounbary = new SpatialReader(SCS_Boundary).getGeometryList().get(0);

		List<String> scsNodePli = new ArrayList<>();
		List<String> extList = new ArrayList<>();

		String scsValues[][] = new AtFileReader(SCS_Value).getCsv();
		List<String> scsValueID = new ArrayList<>(Arrays.asList(scsValues[0]));
		scsValueID.remove(0);

		List<String> scsPropertiesID = new ArrayList<>();
		String scsProperties[][] = new AtFileReader(SCS_Property).getContent("\t", 1, 0);
		// create .pli
		// create ext
		for (int index = 1; index < scsProperties.length; index++) {
			String temptLine[] = scsProperties[index];
			if (scsValueID.contains(temptLine[1])) {

				Geometry point = GdalGlobal.CreatePoint(Double.parseDouble(temptLine[6]),
						Double.parseDouble(temptLine[7]));

				if (bounbary.Contains(point)) {
					scsPropertiesID.add(temptLine[1]);

					scsNodePli.add("Scs_Node_" + temptLine[1]);
					scsNodePli.add("    1    2");
					scsNodePli.add(temptLine[6] + "  " + temptLine[7]);

					extList.add("");
					extList.add("QUANTITY=discharge_salinity_temperature_sorsin");
					extList.add("FILENAME=" + "Scs_Node_" + temptLine[1] + ".pli");
					extList.add("FILETYPE=9");
					extList.add("METHOD=1");
					extList.add("OPERAND=O");
					extList.add("AREA=1");
				}
			}
		}
		new AtFileWriter(scsNodePli.parallelStream().toArray(String[]::new), outputFolder + "\\Scs_Node.pli")
				.textWriter("");
		new AtFileWriter(extList.parallelStream().toArray(String[]::new), outputFolder + "\\FlowFM.ext").textWriter("");

		// create .tim
		for (int column = 1; column < scsValues[0].length; column++) {
			if (scsPropertiesID.contains(scsValues[0][column])) {
				List<String> scsNodeExt = new ArrayList<>();

				for (int row = 1; row < scsValues.length; row++) {
					scsNodeExt.add(String.format("%6.7e", (row - 1) * 60.) + " "
							+ String.format("%6.7e", Double.parseDouble(scsValues[row][column])));
				}

				new AtFileWriter(scsNodeExt.parallelStream().toArray(String[]::new),
						outputFolder + "\\Scs_Node_" + scsValues[0][column] + ".tim").textWriter("");
			}
		}

	}

	private static void GenerateNetcdfFile(double targetMinX, double targetMaxX, double targetMinY, double targetMaxY,
			String NC_OutputFolder, String NC_BaseAscii, String NC_TargetAscii)
			throws IOException, InvalidRangeException {
		String outputFolder = NC_OutputFolder;

		AsciiBasicControl baseAscii = new AsciiBasicControl(NC_BaseAscii);
		AsciiBasicControl ascii5M = new AsciiBasicControl(NC_TargetAscii);
		List<Geometry> geoList = new ArrayList<>();

		for (int row = 0; row < Integer.parseInt(baseAscii.getProperty().get("row")); row++) {
			for (int column = 0; column < Integer.parseInt(baseAscii.getProperty().get("column")); column++) {
				double[] coordinate = baseAscii.getCoordinate(column, row);

				// double qualTree
				if (coordinate[0] > targetMinX && coordinate[0] < targetMaxX && coordinate[1] < targetMaxY
						&& coordinate[1] > targetMinY) {
					getQualTree(coordinate[0], coordinate[1], baseAscii.getCellSize()).forEach(temptPath -> {
						Rectangle rec = temptPath.getBounds();
						getQualTree(rec.getCenterX(), rec.getCenterY(), baseAscii.getCellSize() / 2)
								.forEach(outPath -> geoList.add(GdalGlobal.Path2DToGeometry(outPath)));
					});

					// qualTree
				} else if (coordinate[0] > targetMinX - 100 && coordinate[0] < targetMaxX + 100
						&& coordinate[1] < targetMaxY + 100 && coordinate[1] > targetMinY - 100) {
					getQualTree(coordinate[0], coordinate[1], baseAscii.getCellSize()).forEach(temptPath -> {
						geoList.add(GdalGlobal.Path2DToGeometry(temptPath));
					});

					// 20m grid
				} else if (!baseAscii.getValue(column, row).equals(baseAscii.getNullValue())) {
					geoList.add(GdalGlobal
							.Path2DToGeometry(getGrid(coordinate[0], coordinate[1], baseAscii.getCellSize())));
				}
			}
		}

		// generate nc
		DflowNetcdfTranslator df = new DflowNetcdfTranslator(geoList);
		df.checkQualTreeOverLappingLine();
		df.setNodeLevel(baseAscii);
		df.setNodeLevel(ascii5M);
		df.saveAs(outputFolder + "\\netcdf.nc");

		List<String> bedLevel = new ArrayList<>();
		createBedLevel(baseAscii).forEach(e -> bedLevel.add(e));
		createBedLevel(ascii5M).forEach(e -> bedLevel.add(e));
		new AtFileWriter(bedLevel.parallelStream().toArray(String[]::new), outputFolder + "\\merge_bedLevel.xyz")
				.textWriter("");

		// create boundary
		List<Geometry> boundaryList = geoList;
		while (boundaryList.size() != 1) {
			List<Geometry> temptList = new ArrayList<>();

			for (int index = 0; index < boundaryList.size(); index = index + 2) {
				try {
					temptList.add(boundaryList.get(index).Union(boundaryList.get(index + 1)));
				} catch (Exception e) {
					temptList.add(boundaryList.get(index));
				}
			}
			boundaryList = temptList;
		}
		new SpatialWriter().setGeoList(boundaryList).saveAsGeoJson(outputFolder + "\\boundary.geoJson");

	}

	private static Path2D getGrid(double centerX, double centerY, double cellSize) {
		double minX = AtCommonMath.getDecimal_Double(centerX - 0.5 * cellSize, 3);
		double maxX = AtCommonMath.getDecimal_Double(centerX + 0.5 * cellSize, 3);
		double minY = AtCommonMath.getDecimal_Double(centerY - 0.5 * cellSize, 3);
		double maxY = AtCommonMath.getDecimal_Double(centerY + 0.5 * cellSize, 3);

		Path2D path = new Path2D.Double();
		path.moveTo(minX, maxY);
		path.lineTo(minX, minY);
		path.lineTo(maxX, minY);
		path.lineTo(maxX, maxY);
		return path;
	}

	private static List<Path2D> getQualTree(double centerX, double centerY, double cellSize) {
		List<Path2D> outList = new ArrayList<>();

		outList.add(getGrid(centerX - 0.25 * cellSize, centerY - 0.25 * cellSize, 0.5 * cellSize));
		outList.add(getGrid(centerX - 0.25 * cellSize, centerY + 0.25 * cellSize, 0.5 * cellSize));
		outList.add(getGrid(centerX + 0.25 * cellSize, centerY - 0.25 * cellSize, 0.5 * cellSize));
		outList.add(getGrid(centerX + 0.25 * cellSize, centerY + 0.25 * cellSize, 0.5 * cellSize));
		return outList;
	}

	private static List<String> createBedLevel(AsciiBasicControl ascii) {
		List<String> outList = new ArrayList<>();
		for (int row = 0; row < Integer.parseInt(ascii.getProperty().get("row")); row++) {
			for (int column = 0; column < Integer.parseInt(ascii.getProperty().get("column")); column++) {

				String temptValue = ascii.getValue(column, row);
				if (!temptValue.equals(ascii.getNullValue())) {
					double[] coordinate = ascii.getCoordinate(column, row);
					outList.add(coordinate[0] + " " + coordinate[1] + " " + temptValue);
				}
			}
		}
		return outList;
	}
}
