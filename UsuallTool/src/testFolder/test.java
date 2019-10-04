package testFolder;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import FEWS.netcdf.DflowNetcdfTranslator;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiToPath;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import netCDF.NetcdfBasicControl;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class test {
	public static void main(String[] args) throws IOException, InvalidRangeException, CloneNotSupportedException {
//

		String fileAdd = "F:\\DFX\\DevRelease\\DFX\\Video\\\\1570182311\\";
		String[] fileList = new File(fileAdd).list();

		for (int index = 0; index < fileList.length; index++) {
			FileFunction.reNameFile(fileAdd + fileList[index],
					fileAdd + "frame_" + String.format("%04d", index) + ".png");
		}

//		DflowNetcdfTranslator nc = new DflowNetcdfTranslator(
//				new AsciiBasicControl("E:\\LittleProject\\新竹SOBEK模型\\成果展示\\20190517案例\\asc\\dm1d0000.asc"));
//		nc.set_node_Z_value(new AsciiBasicControl("E:\\LittleProject\\新竹SOBEK模型\\成果展示\\20190517案例\\asc\\dm1d0000.asc"));
//
//		List<Double> simulationTime = new ArrayList<>();
//		List<AsciiBasicControl> asciiList = new ArrayList<>();
//		for (int index = 0; index <= 27; index++) {
//			simulationTime.add(index * 3600.);
//			asciiList.add(new AsciiBasicControl("E:\\LittleProject\\新竹SOBEK模型\\成果展示\\20190517案例\\asc\\dm1d"
//					+ String.format("%04d", index) + ".asc"));
//		}
//
//		nc.set_outputTimeSeries(3600, simulationTime);
//		nc.addWaterDepth(asciiList);
//
//		nc.saveAs("E:\\LittleProject\\新竹SOBEK模型\\成果展示\\20190517案例\\nc\\FlowFM_net.nc");

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
