package geo.common;

public class GeoVector {

	// get the center point of two points
	public static Double[] getCenterPoint(Double[] point1, Double[] point2) {
		return new Double[] { (point1[0] + point2[0]) / 2, (point1[1] + point2[1]) / 2 };
	}

	// get the distance of two points
	public static double getDis(Double[] point1, Double[] point2) {
		return Math.pow(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2), 0.5);
	}

	// get the parmeter of Perpendicular Bisector(vertical line)
	// return {a,b} of Y = ax+b
	public static Double[] getPerpendicularBisector(Double[] point1, Double[] point2) {
		Double[] centerPoint = GeoVector.getCenterPoint(point1, point2);
		double slope = -1 * ((point1[1] - point2[1]) / (point1[0] - point2[0]));
		double intercept = centerPoint[1] - slope * centerPoint[0];
		return new Double[] { slope, intercept };
	}

}
