package asciiFunction;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class AsciiIntersect {
	private AsciiBasicControl ascii;
	private TreeMap<String, String> property;

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

	// <=========================>
	// <start from the left top of the asciiGrid>
	// <=========================>
	public String[][] getIntersect(double minX, double maxX, double minY, double maxY) {
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
		if (this.ascii.isContain(minX, minY)) {
			return true;
		} else if (this.ascii.isContain(minX, maxY)) {
			return true;
		} else if (this.ascii.isContain(maxX, maxY)) {
			return true;
		} else if (this.ascii.isContain(maxX, minY)) {
			return true;
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
		if (this.ascii.isContain(minX, minY)) {
			return true;
		} else if (this.ascii.isContain(minX, maxY)) {
			return true;
		} else if (this.ascii.isContain(maxX, maxY)) {
			return true;
		} else if (this.ascii.isContain(maxX, minY)) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isIntersect(double minX, double maxX, double minY, double maxY) {
		// if there is any points of boundary is in the ascii
		// return true
		if (this.ascii.isContain(minX, minY)) {
			return true;
		} else if (this.ascii.isContain(minX, maxY)) {
			return true;
		} else if (this.ascii.isContain(maxX, maxY)) {
			return true;
		} else if (this.ascii.isContain(maxX, minY)) {
			return true;
		} else {
			return false;
		}
	}

	public String[][] getIntersect(AsciiBasicControl boundaryAscii) {
		Map<String, String> temptProperty = boundaryAscii.getProperty();
		double boundaryMinX = Double.parseDouble(temptProperty.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(temptProperty.get("topX"));
		double boundaryMaxY = Double.parseDouble(temptProperty.get("topY"));
		double boundaryMinY = Double.parseDouble(temptProperty.get("bottomY"));
		getIntersectBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);
		return this.ascii.getClipAsciiFile(this.IntersectMinX, this.IntersectMinY, this.IntersectMaxX,
				this.IntersectMaxY);
	}

	public Map<String, String> getBoundary(double minX, double maxX, double minY, double maxY) {
		Map<String, String> outMap = new TreeMap<String, String>();
		getIntersectBoundary(minX, maxX, minY, maxY);

		outMap.put("minX", this.IntersectMinX + "");
		outMap.put("maxX", this.IntersectMaxX + "");
		outMap.put("minY", this.IntersectMinY + "");
		outMap.put("maxY", this.IntersectMaxY + "");
		return outMap;

	}

	public Map<String, String> getBoundary(AsciiBasicControl boundaryAscii) {
		Map<String, String> outMap = new TreeMap<String, String>();
		Map<String, String> temptProperty = boundaryAscii.getProperty();
		double boundaryMinX = Double.parseDouble(temptProperty.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(temptProperty.get("topX"));
		double boundaryMaxY = Double.parseDouble(temptProperty.get("topY"));
		double boundaryMinY = Double.parseDouble(temptProperty.get("bottomY"));
		getIntersectBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);

		outMap.put("minX", this.IntersectMinX + "");
		outMap.put("maxX", this.IntersectMaxX + "");
		outMap.put("minY", this.IntersectMinY + "");
		outMap.put("maxY", this.IntersectMaxY + "");
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
