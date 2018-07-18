package asciiFunction;

import java.io.IOException;
import java.util.TreeMap;

public class AsciiBoundary {
	private AsciiBasicControl ascii;
	private TreeMap<String, String> property;

	private double interceptMinX;
	private double interceptMaxX;
	private double interceptMinY;
	private double interceptMaxY;

	public AsciiBoundary(String fileAdd) throws IOException {
		this.ascii = new AsciiBasicControl(fileAdd);
		this.property = ascii.getProperty();
	}

	public AsciiBoundary(String[][] asciiFile) throws IOException {
		this.ascii = new AsciiBasicControl(asciiFile);
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
		double boundaryMinX = Double.parseDouble(this.property.get("bottomX"));
		double boundaryMaxX = Double.parseDouble(this.property.get("topX"));
		double boundaryMaxY = Double.parseDouble(this.property.get("topY"));
		double boundaryMinY = Double.parseDouble(this.property.get("bottomY"));
		getInterceptBoundary(boundaryMinX, boundaryMaxX, boundaryMinY, boundaryMaxY);
		return this.ascii.getClipAsciiFile(this.interceptMinX, this.interceptMinY, this.interceptMaxX,
				this.interceptMaxY);
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
}
