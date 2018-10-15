package asciiFunction;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class AsciiIntercept {
	private AsciiBasicControl ascii;
	private TreeMap<String, String> property;

	private double interceptMinX;
	private double interceptMaxX;
	private double interceptMinY;
	private double interceptMaxY;

	public AsciiIntercept(String fileAdd) throws IOException {
		this.ascii = new AsciiBasicControl(fileAdd);
		this.property = ascii.getProperty();
	}

	public AsciiIntercept(String[][] asciiFile) throws IOException {
		this.ascii = new AsciiBasicControl(asciiFile);
		this.property = ascii.getProperty();
	}

	public AsciiIntercept(AsciiBasicControl ascii) {
		this.ascii = ascii;
		this.property = ascii.getProperty();
	}

	// <=========================>
	// <start from the left top of the asciiGrid>
	// <=========================>
	public String[][] getIntercept(double minX, double maxX, double minY, double maxY) {
		getInterceptBoundary(minX, maxX, minY, maxY);
		return this.ascii.getClipAsciiFile(this.interceptMinX, this.interceptMinY, this.interceptMaxX,
				this.interceptMaxY);
	}

	public Boolean isInterseption(AsciiBasicControl temptAscii) {
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

	public Boolean isInterseption(Map<String, Double> boundary) {
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

	public Boolean isInterseption(double minX, double maxX, double minY, double maxY) {
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

	public String[][] getIntercept(AsciiBasicControl boundaryAscii) {
		Map<String, String> temptProperty = boundaryAscii.getProperty();
		double boundaryMinX = Double.parseDouble(temptProperty.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(temptProperty.get("topX"));
		double boundaryMaxY = Double.parseDouble(temptProperty.get("topY"));
		double boundaryMinY = Double.parseDouble(temptProperty.get("bottomY"));
		getInterceptBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);
		return this.ascii.getClipAsciiFile(this.interceptMinX, this.interceptMinY, this.interceptMaxX,
				this.interceptMaxY);
	}

	public Map<String, String> getBoundary(double minX, double maxX, double minY, double maxY) {
		Map<String, String> outMap = new TreeMap<String, String>();
		getInterceptBoundary(minX, maxX, minY, maxY);

		outMap.put("minX", this.interceptMinX + "");
		outMap.put("maxX", this.interceptMaxX + "");
		outMap.put("minY", this.interceptMinY + "");
		outMap.put("maxY", this.interceptMaxY + "");
		return outMap;

	}

	public Map<String, String> getBoundary(AsciiBasicControl boundaryAscii) {
		Map<String, String> outMap = new TreeMap<String, String>();
		Map<String, String> temptProperty = boundaryAscii.getProperty();
		double boundaryMinX = Double.parseDouble(temptProperty.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(temptProperty.get("topX"));
		double boundaryMaxY = Double.parseDouble(temptProperty.get("topY"));
		double boundaryMinY = Double.parseDouble(temptProperty.get("bottomY"));
		getInterceptBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);

		outMap.put("minX", this.interceptMinX + "");
		outMap.put("maxX", this.interceptMaxX + "");
		outMap.put("minY", this.interceptMinY + "");
		outMap.put("maxY", this.interceptMaxY + "");
		return outMap;

	}

	private void getInterceptBoundary(double minX, double maxX, double minY, double maxY) {
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		double temptMinX = Double.parseDouble(this.property.get("bottomX")) - 0.5 * cellSize;
		double temptMaxX = Double.parseDouble(this.property.get("topX")) + 0.5 * cellSize;
		double temptMaxY = Double.parseDouble(this.property.get("topY")) + 0.5 * cellSize;
		double temptMinY = Double.parseDouble(this.property.get("bottomY")) - 0.5 * cellSize;

		if (temptMinX > minX) {
			this.interceptMinX = temptMinX;
		} else {
			this.interceptMinX = minX;
		}

		if (temptMinY > minY) {
			this.interceptMinY = temptMinY;
		} else {
			this.interceptMinY = minY;
		}

		if (temptMaxX < maxX) {
			this.interceptMaxX = temptMaxX;
		} else {
			this.interceptMaxX = maxX;
		}

		if (temptMaxY < maxY) {
			this.interceptMaxY = temptMaxY;
		} else {
			this.interceptMaxY = maxY;
		}
	}
}
