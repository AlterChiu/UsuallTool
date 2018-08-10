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
		double temptMinX = Double.parseDouble(this.property.get("bottomX"));
		double temptMaxX = Double.parseDouble(this.property.get("topX"));
		double temptMaxY = Double.parseDouble(this.property.get("topY"));
		double temptMinY = Double.parseDouble(this.property.get("bottomY"));

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

	private void getMergeBoundary(double minX, double maxX, double minY, double maxY) {
		double temptMinX = Double.parseDouble(this.property.get("bottomX"));
		double temptMaxX = Double.parseDouble(this.property.get("topX"));
		double temptMaxY = Double.parseDouble(this.property.get("topY"));
		double temptMinY = Double.parseDouble(this.property.get("bottomY"));

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
