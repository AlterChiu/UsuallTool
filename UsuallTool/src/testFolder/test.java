package testFolder;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gdal.ogr.Geometry;

import FEWS.netcdf.DflowNetcdfTranslator;
import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;

public class test {
	public static void main(String[] args) throws IOException, InvalidRangeException {
//		Geometry boundary_40 = new SpatialReader("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\multiLayerBOundary_1.shp")
//				.getGeometryList().get(0);
//
//		Geometry boundary_20 = new SpatialReader("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\multiLayerBOundary_0.shp")
//				.getGeometryList().get(0);
//
//		List<Geometry> outGeoList = new ArrayList<>();
//		fillBoundary(boundary_40, 40.0).forEach(geo -> outGeoList.add(geo));
//		fillBoundary(boundary_20, 20.0).forEach(geo -> outGeoList.add(geo));
//
//		SpatialWriter sp = new SpatialWriter();
//		sp.setCoordinateSystem(SpatialWriter.TWD97_121);
//		sp.setGeoList(outGeoList);
//		sp.saveAsShp("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\boundaryGrid.shp");


//		DflowNetcdfTranslator translate = new DflowNetcdfTranslator(
//				new SpatialReader("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\meshGridTest.shp"));
//		translate.saveAs("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\meshGridTest.nc");
		
		
		DflowNetcdfTranslator translate = new DflowNetcdfTranslator(
				new SpatialReader("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\boundaryGrid.shp"));
		translate.saveAs("E:\\LittleProject\\Dflow-FM\\sobek\\asc\\boundaryGrid.nc");

	}

	private static List<Geometry> fillBoundary(Geometry geo, double cellSize) {

		List<Geometry> outList = new ArrayList<>();
		Path2D pathBoundary = GdalGlobal.GeomertyToPath2D(geo).get(0);
		Geometry geoBoundary = geo.Boundary();

		List<Double> xList = new ArrayList<>();
		List<Double> yList = new ArrayList<>();
		Arrays.asList(geo.GetBoundary().GetPoints()).forEach(point -> {
			xList.add(point[0]);
			yList.add(point[1]);
		});

		AtCommonMath xStatics = new AtCommonMath(xList);
		AtCommonMath yStatics = new AtCommonMath(yList);
		double maxX = xStatics.getMax();
		double minX = xStatics.getMin();
		int xSize = new BigDecimal(maxX - minX).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
		maxX = minX + xSize + cellSize;

		double maxY = yStatics.getMax();
		double minY = yStatics.getMin();
		int ySize = new BigDecimal(maxY - minY).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
		maxY = minY + ySize + cellSize;

		for (int temptX = 0; temptX < xSize - 1; temptX++) {
			for (int temptY = 0; temptY < ySize - 1; temptY++) {

				double temptMinX = minX + temptX * cellSize;
				double temptMaxX = minX + (temptX + 1) * cellSize;
				double temptMinY = minY + temptY * cellSize;
				double temptMaxY = minY + (temptY + 1) * cellSize;

				Path2D temptPath = new Path2D.Double();
				temptPath.moveTo(temptMinX, temptMinY);
				temptPath.lineTo(temptMaxX, temptMinY);
				temptPath.lineTo(temptMaxX, temptMaxY);
				temptPath.lineTo(temptMinX, temptMaxY);
				Geometry temptGeo = GdalGlobal.Path2DToGeometry(temptPath);

				if (pathBoundary.contains(temptMinX, temptMinY) && pathBoundary.contains(temptMinX, temptMaxY)
						&& pathBoundary.contains(temptMaxX, temptMinY) && pathBoundary.contains(temptMaxX, temptMaxY)) {
					outList.add(temptGeo);
				} else {
					if (geoBoundary.Crosses(temptGeo)) {
						outList.add(temptGeo.Intersection(geo));
					}
				}

			}
		}

		return outList;
	}

}
