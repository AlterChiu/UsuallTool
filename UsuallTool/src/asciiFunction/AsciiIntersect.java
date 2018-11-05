package asciiFunction;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import geo.path.IntersectLine;
import usualTool.AtCommonMath;

public class AsciiIntersect {
	private AsciiBasicControl ascii;
	private Map<String, String> property;

	private double IntersectMinX;
	private double IntersectMaxX;
	private double IntersectMinY;
	private double IntersectMaxY;

	public AsciiIntersect(String fileAdd) throws IOException {
		this.ascii = new AsciiBasicControl(fileAdd);
		this.property = ascii.getProperty();
	}

	public AsciiIntersect(String[][] asciiFile) throws IOException {
		this.ascii = new AsciiBasicControl(asciiFile);
		this.property = ascii.getProperty();
	}

	public AsciiIntersect(AsciiBasicControl ascii) {
		this.ascii = ascii;
		this.property = ascii.getProperty();
	}

	/*
	 * 
	 */
	// <===========================>
	// < get the asciiFile which split by given line>
	// <===========================>
	public Boolean isIntersect(double xCoefficient, double yCoefficient, double intersectCoefficient) {
		Map<String, Double> boundary = this.ascii.getBoundary();

		double crossTopX = -1 * (yCoefficient * boundary.get("maxY") + intersectCoefficient) / xCoefficient;
		double crossBottomX = -1 * (yCoefficient * boundary.get("minY") + intersectCoefficient) / xCoefficient;
		double crossRightY = -1 * (xCoefficient * boundary.get("maxX") + intersectCoefficient) / yCoefficient;
		double crossLeftY = -1 * (xCoefficient * boundary.get("maxX") + intersectCoefficient) / yCoefficient;

		if (crossTopX <= boundary.get("maxX") && crossTopX >= boundary.get("minX")) {
			return true;
		} else if (crossBottomX <= boundary.get("maxX") && crossBottomX >= boundary.get("minX")) {
			return true;
		} else if (crossRightY <= boundary.get("maxY") && crossRightY >= boundary.get("minY")) {
			return true;
		} else if (crossLeftY <= boundary.get("maxY") && crossLeftY >= boundary.get("minY")) {
			return true;
		} else {
			return false;
		}
	}

	public List<Map<String, Double>> getIntersectSideBoundary(double xCoefficient, double yCoefficient,
			double intersectCoefficient) {
		List<Map<String, Double>> outBoundary = new ArrayList<>();

		Map<String, Double> boundary = this.ascii.getBoundary();
		Path2D temptPath = new Path2D.Double();
		temptPath.moveTo(boundary.get("minX"), boundary.get("maxY"));
		temptPath.lineTo(boundary.get("minX"), boundary.get("minY"));
		temptPath.lineTo(boundary.get("maxX"), boundary.get("minY"));
		temptPath.lineTo(boundary.get("maxX"), boundary.get("maxY"));

		List<List<Double[]>> sidePoints = new IntersectLine(temptPath).getSidePoints(xCoefficient, yCoefficient,
				intersectCoefficient);

		for (int index = 0; index < sidePoints.size(); index++) {
			List<Double> temptXList = new ArrayList<Double>();
			List<Double> temptYList = new ArrayList<Double>();

			List<Double[]> temptPoints = sidePoints.get(index);
			temptPoints.forEach(point -> {
				temptXList.add(point[0]);
				temptYList.add(point[1]);
			});
			AtCommonMath xStatics = new AtCommonMath(temptXList);
			AtCommonMath yStatics = new AtCommonMath(temptYList);
			double minX = xStatics.getMin();
			double maxX = xStatics.getMax();
			double minY = yStatics.getMin();
			double maxY = yStatics.getMax();

			Map<String, Double> temptBoundary = new TreeMap<>();
			temptBoundary.put("maxX", maxX);
			temptBoundary.put("minX", minX);
			temptBoundary.put("minY", minY);
			temptBoundary.put("maxY", maxY);

			outBoundary.add(temptBoundary);
		}

		return outBoundary;
	}

	public List<AsciiBasicControl> getIntersectSideAscii(double xCoefficient, double yCoefficient,
			double intersectCoefficient) throws IOException {

		List<AsciiBasicControl> outAsciiList = new ArrayList<>();
		for (Map<String, Double> temptBoundary : getIntersectSideBoundary(xCoefficient, yCoefficient,
				intersectCoefficient)) {
			outAsciiList.add(this.ascii.getClipAsciiFile(temptBoundary));
		}
		return outAsciiList;
	}

	// <=======================================================================>

	/*
	 * 
	 */
	// <=========================>
	// < get the intersect by giving boundary >
	// <=========================>
	public Boolean isIntersect(double minX, double maxX, double minY, double maxY) {
		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * tmeptCellSize;

		if (temptMinX < maxX && temptMaxX > minX) {
			if (minY < temptMaxY && maxY > temptMinY) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Double> getIntersectBoundary(double minX, double maxX, double minY, double maxY) {
		return intersectBoundary(minX, maxX, minY, maxY);
	}

	public AsciiBasicControl getIntersectAscii(double minX, double maxX, double minY, double maxY) throws IOException {
		return this.ascii.getClipAsciiFile(intersectBoundary(minX, maxX, minY, maxY));
	}
	// <=======================================================================>

	/*
	 * 
	 */
	// <=======================>
	// < get the intersect by giving asciiFile>
	// <=======================>
	public Boolean isIntersect(AsciiBasicControl temptAscii) {
		double cellSize = Double.parseDouble(temptAscii.getProperty().get("cellSize"));
		double minX = Double.parseDouble(temptAscii.getProperty().get("bottomX")) - 0.5 * cellSize;
		double maxX = Double.parseDouble(temptAscii.getProperty().get("topX")) + 0.5 * cellSize;
		double minY = Double.parseDouble(temptAscii.getProperty().get("bottomY")) - 0.5 * cellSize;
		double maxY = Double.parseDouble(temptAscii.getProperty().get("topY")) + 0.5 * cellSize;

		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * tmeptCellSize;

		if (temptMinX < maxX && temptMaxX > minX) {
			if (minY < temptMaxY && maxY > temptMinY) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Double> getIntersectBoundary(AsciiBasicControl boundaryAscii) {
		Map<String, Double> temptProperty = boundaryAscii.getBoundary();
		return intersectBoundary(temptProperty);
	}

	public AsciiBasicControl getIntersectAscii(AsciiBasicControl boundaryAscii) throws IOException {
		Map<String, Double> temptProperty = boundaryAscii.getBoundary();
		return this.ascii.getClipAsciiFile(intersectBoundary(temptProperty));
	}
	// <=================================================================>

	/*
	 * 
	 */
	// <============================>
	// < get the intersect by giving boundary map>
	// <============================>
	public Boolean isIntersect(Map<String, Double> boundary) {
		double minX = boundary.get("minX");
		double maxX = boundary.get("maxX");
		double minY = boundary.get("minY");
		double maxY = boundary.get("maxY");

		// if there is any points of boundary is in the ascii
		// return true
		double tmeptCellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * tmeptCellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * tmeptCellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * tmeptCellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * tmeptCellSize;

		if (temptMinX < maxX && temptMaxX > minX) {
			if (minY < temptMaxY && maxY > temptMinY) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Double> getIntersectBoundary(Map<String, Double> boundary) {
		return intersectBoundary(boundary);
	}

	public AsciiBasicControl getIntersectAscii(Map<String, Double> boundary) throws IOException {
		return this.ascii.getClipAsciiFile(intersectBoundary(boundary));
	}
	// <=============================================================>

	/*
	 * 
	 * private function
	 */
	// <===========================================================>
	private Map<String, Double> intersectBoundary(Map<String, Double> temptBoundary) {
		Map<String, Double> boundary = this.ascii.getBoundary();
		if (boundary.get("minX") > temptBoundary.get("minX")) {
			this.IntersectMinX = boundary.get("minX");
		} else {
			this.IntersectMinX = temptBoundary.get("minX");
		}

		if (boundary.get("minY") > temptBoundary.get("minY")) {
			this.IntersectMinY = boundary.get("minY");
		} else {
			this.IntersectMinY = temptBoundary.get("minY");
		}

		if (boundary.get("maxX") < temptBoundary.get("maxX")) {
			this.IntersectMaxX = boundary.get("maxX");
		} else {
			this.IntersectMaxX = temptBoundary.get("maxX");
		}

		if (boundary.get("maxY") < temptBoundary.get("maxY")) {
			this.IntersectMaxY = boundary.get("maxY");
		} else {
			this.IntersectMaxY = temptBoundary.get("maxY");
		}

		Map<String, Double> outBoundary = new TreeMap<String, Double>();
		outBoundary.put("minX", IntersectMinX);
		outBoundary.put("maxX", IntersectMaxX);
		outBoundary.put("minY", IntersectMinY);
		outBoundary.put("maxY", IntersectMaxY);

		return outBoundary;
	}

	private Map<String, Double> intersectBoundary(double minX, double maxX, double minY, double maxY) {
		Map<String, Double> boundary = this.ascii.getBoundary();
		if (boundary.get("minX") > minX) {
			this.IntersectMinX = boundary.get("minX");
		} else {
			this.IntersectMinX = minX;
		}

		if (boundary.get("minY") > minY) {
			this.IntersectMinY = boundary.get("minY");
		} else {
			this.IntersectMinY = minY;
		}

		if (boundary.get("maxX") < maxX) {
			this.IntersectMaxX = boundary.get("maxX");
		} else {
			this.IntersectMaxX = maxX;
		}

		if (boundary.get("maxY") < maxY) {
			this.IntersectMaxY = boundary.get("maxY");
		} else {
			this.IntersectMaxY = maxY;
		}

		Map<String, Double> outBoundary = new TreeMap<String, Double>();
		outBoundary.put("minX", IntersectMinX);
		outBoundary.put("maxX", IntersectMaxX);
		outBoundary.put("minY", IntersectMinY);
		outBoundary.put("maxY", IntersectMaxY);

		return outBoundary;
	}
}
