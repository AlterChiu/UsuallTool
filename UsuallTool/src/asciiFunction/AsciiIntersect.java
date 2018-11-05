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

	// <===========================>
	// < get the asciiFile which split by given line>
	// <===========================>
	public List<Map<String, Double>> getIntersect(double xCoefficient, double yCoefficient,
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

	// <=========================>
	// <start from the left top of the asciiGrid>
	// <=========================>
	public AsciiBasicControl getIntersect(double minX, double maxX, double minY, double maxY) throws IOException {
		getIntersectBoundary(minX, maxX, minY, maxY);
		return this.ascii.getClipAsciiFile(this.IntersectMinX, this.IntersectMinY, this.IntersectMaxX,
				this.IntersectMaxY);
	}

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

	public AsciiBasicControl getIntersect(AsciiBasicControl boundaryAscii) throws IOException {
		Map<String, String> temptProperty = boundaryAscii.getProperty();
		double boundaryMinX = Double.parseDouble(temptProperty.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(temptProperty.get("topX"));
		double boundaryMaxY = Double.parseDouble(temptProperty.get("topY"));
		double boundaryMinY = Double.parseDouble(temptProperty.get("bottomY"));
		getIntersectBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);
		return this.ascii.getClipAsciiFile(this.IntersectMinX, this.IntersectMinY, this.IntersectMaxX,
				this.IntersectMaxY);
	}

	public Map<String, Double> getBoundary(double minX, double maxX, double minY, double maxY) {
		Map<String, Double> outMap = new TreeMap<String, Double>();
		getIntersectBoundary(minX, maxX, minY, maxY);

		outMap.put("minX", this.IntersectMinX);
		outMap.put("maxX", this.IntersectMaxX);
		outMap.put("minY", this.IntersectMinY);
		outMap.put("maxY", this.IntersectMaxY);
		return outMap;

	}

	public Map<String, Double> getBoundary(AsciiBasicControl boundaryAscii) {
		Map<String, Double> outMap = new TreeMap<String, Double>();
		Map<String, String> temptProperty = boundaryAscii.getProperty();
		double boundaryMinX = Double.parseDouble(temptProperty.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(temptProperty.get("topX"));
		double boundaryMaxY = Double.parseDouble(temptProperty.get("topY"));
		double boundaryMinY = Double.parseDouble(temptProperty.get("bottomY"));
		getIntersectBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);

		outMap.put("minX", this.IntersectMinX);
		outMap.put("maxX", this.IntersectMaxX);
		outMap.put("minY", this.IntersectMinY);
		outMap.put("maxY", this.IntersectMaxY);
		return outMap;

	}

	private void getIntersectBoundary(double minX, double maxX, double minY, double maxY) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * cellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * cellSize;

		if (temptMinX > minX) {
			this.IntersectMinX = temptMinX;
		} else {
			this.IntersectMinX = minX;
		}

		if (temptMinY > minY) {
			this.IntersectMinY = temptMinY;
		} else {
			this.IntersectMinY = minY;
		}

		if (temptMaxX < maxX) {
			this.IntersectMaxX = temptMaxX;
		} else {
			this.IntersectMaxX = maxX;
		}

		if (temptMaxY < maxY) {
			this.IntersectMaxY = temptMaxY;
		} else {
			this.IntersectMaxY = maxY;
		}
	}
}
