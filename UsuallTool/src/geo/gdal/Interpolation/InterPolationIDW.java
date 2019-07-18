package geo.gdal.Interpolation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import usualTool.AtCommonMath;

public class InterPolationIDW implements AtInterpolation {
	private List<Double[]> xyzList;

	public InterPolationIDW(List<Double[]> xyzList) {
		this.xyzList = xyzList;
	}

	private List<Double> IDWmethold(List<Double[]> xyList) {
		List<Double> zList = new ArrayList<>();

		for (int targetIndex = 0; targetIndex < xyList.size(); targetIndex++) {

			// inverse distance
			List<Double> disList = new ArrayList<>();
			for (int baseIndex = 0; baseIndex < this.xyzList.size(); baseIndex++) {
				disList.add(1. / getDis(this.xyzList.get(baseIndex), xyList.get(targetIndex)));
			}

			// rationalize
			disList = new AtCommonMath(disList).getRatio();

			// get SUM(z * ratioValue)
			double targetValue = 0;
			for (int baseIndex = 0; baseIndex < this.xyzList.size(); baseIndex++) {
				targetValue = targetValue + disList.get(baseIndex) * this.xyzList.get(baseIndex)[2];
			}
			zList.add(targetValue);
		}

		// output zList
		return zList;
	}

	private double getDis(Double[] point1, Double[] point2) {
		return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see geo.gdal.Interpolation.AtInterpolation#getAscii(java.util.Map, double)
	 */
	@Override
	public AsciiBasicControl getAscii(Map<String, Double> boundary, double cellSize) throws IOException {
		// TODO Auto-generated method stub

		XYZToAscii xyzFunction = new XYZToAscii(getXYZ(boundary, cellSize));
		xyzFunction.setCellSize(cellSize);
		xyzFunction.start();

		return new AsciiBasicControl(xyzFunction.getAsciiFile());
	}

	@Override
	public AsciiBasicControl getAscii(AsciiBasicControl ascii) throws IOException {
		// TODO Auto-generated method stub

		XYZToAscii xyzFunction = new XYZToAscii(getXYZ(ascii.getBoundary(), ascii.getCellSize()));
		xyzFunction.setCellSize(ascii.getCellSize());
		xyzFunction.start();

		return new AsciiBasicControl(xyzFunction.getAsciiFile());
	}

	@Override
	public List<Double[]> getXYZ(Map<String, Double> boundary, double cellSize) {
		// TODO Auto-generated method stub
		List<Double[]> xyList = new ArrayList<>();
		for (double x = boundary.get("minX"); x <= boundary.get("maxX") + cellSize * 0.1; x = x + cellSize) {
			for (double y = boundary.get("minY"); y <= boundary.get("maxY") + cellSize * 0.1; y = y + cellSize) {
				xyList.add(new Double[] { x, y });
			}
		}

		return this.getXYZ(xyList);
	}

	@Override
	public List<Double[]> getXYZ(List<Double[]> xyList) {
		// TODO Auto-generated method stub
		List<Double> zList = IDWmethold(xyList);
		List<Double[]> outList = new ArrayList<>();
		for (int index = 0; index < xyList.size(); index++) {
			outList.add(new Double[] { xyList.get(index)[0], xyList.get(index)[1], zList.get(index) });
		}
		return outList;
	}

	@Override
	public List<Double[]> getGeometry(List<Geometry> geometryList) {
		// TODO Auto-generated method stub
		List<Double[]> xyList = new ArrayList<>();
		geometryList.forEach(e -> {
			Geometry centroid = e.Centroid();
			xyList.add(new Double[] { centroid.GetX(), centroid.GetY() });
		});

		return this.getXYZ(xyList);
	}

}
