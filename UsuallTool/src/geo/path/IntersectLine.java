package geo.path;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import usualTool.MathEqualtion.AtLineIntersection;

public class IntersectLine {
	private Path2D polygon;
	private double interceptLength = 0;
	private double xCoefficient = 0;
	private double yCoefficient = 0;
	private List<Double[]> pathPoints = new ArrayList<Double[]>();
	private Set<Double[]> interceptPoints = new HashSet<Double[]>();

	public IntersectLine(Path2D path) {
		this.polygon = path;

		// get the coordinate points on the path
		this.pathPoints = getPathCoordinates();
	}

	/*
	 * ax+by+c=0
	 */
	public Set<Double[]> getInterceptPoints(double xCoefficient, double yCoefficient, double interceptLength) {
		this.interceptPoints.clear();
		this.xCoefficient = xCoefficient;
		this.yCoefficient = yCoefficient;
		this.interceptLength = interceptLength;

		// get the group point which are in the different side by the given line
		List<Double[][]> changedPoints = getChangedPoint();

		// use group point to find out the intersect of two line
		this.calculateIntercept(changedPoints);

		return this.interceptPoints;
	}

	public List<List<Double[]>> getSidePoints(double xCoefficient, double yCoefficient, double interceptLength) {
		List<Double[]> positiveSide = new ArrayList<>();
		List<Double[]> nagtiveSide = new ArrayList<>();
		List<List<Double[]>> outList = new ArrayList<>();

		this.interceptPoints.clear();
		this.xCoefficient = xCoefficient;
		this.yCoefficient = yCoefficient;
		this.interceptLength = interceptLength;

		for (Double[] point : this.pathPoints) {
			int side = getPointSide(point[0], point[1]);
			if (side < 0) {
				nagtiveSide.add(point);
			} else if (side > 0) {
				positiveSide.add(point);
			} else {
				nagtiveSide.add(point);
				positiveSide.add(point);
			}
		}
		outList.add(positiveSide);
		outList.add(nagtiveSide);

		return outList;
	}

	/*
	 * 
	 * get the coordinate of points which on the path
	 */
	private List<Double[]> getPathCoordinates() {
		this.pathPoints.clear();

		PathIterator pathIterator = this.polygon.getPathIterator(null);
		float coordinate[] = new float[6];

		// get point coordinate
		for (; !pathIterator.isDone(); pathIterator.next()) {
			pathIterator.currentSegment(coordinate);
			this.pathPoints.add(new Double[] { (double) coordinate[0], (double) coordinate[1] });
		}

		return this.pathPoints;
	}

	/*
	 * 
	 * 
	 */
	// get the intercept point of two line, one is given line function
	// and the other one is get by "getChangedPoint"
	private void calculateIntercept(List<Double[][]> groupPoints) {
		for (Double[][] groupPoint : groupPoints) {
			double coefficientX;
			double coefficientY;
			double coefficientIncept;

			// if the line is vertical
			if (Math.abs((groupPoint[0][1] - groupPoint[1][1])) < 0.000001) {
				coefficientX = 0;
				coefficientY = 1;
				coefficientIncept = -1 * groupPoint[0][1];

				// if the line is horizontal
			} else if (Math.abs(groupPoint[0][0] - groupPoint[1][0]) < 0.000001) {
				coefficientX = 1;
				coefficientY = 0;
				coefficientIncept = -1 * groupPoint[0][0];

				// the others way
			} else {
				double temptSlope = (groupPoint[0][1] - groupPoint[1][1]) / (groupPoint[0][0] - groupPoint[1][0]);
				double intercept = groupPoint[0][1] - temptSlope * groupPoint[0][0];

				coefficientX = -1 * temptSlope;
				coefficientY = 1;
				coefficientIncept = -1 * intercept;
			}

			AtLineIntersection lineIntersection = new AtLineIntersection(coefficientX, coefficientY, coefficientIncept,
					this.xCoefficient, this.yCoefficient, this.interceptLength);
			if (lineIntersection.isIntersect()) {
				double interceptPoint[] = lineIntersection.getIntersect();
				this.interceptPoints.add(new Double[] { interceptPoint[0], interceptPoint[1] });
			}
		}
	}

	/**
	 * 
	 * 
	 * @return
	 */
	// get the two point, one is inside the polygon and the other one isn't
	private List<Double[][]> getChangedPoint() {
		List<Double[][]> changePoint = new ArrayList<Double[][]>();
		int totalPoints = this.pathPoints.size();

		for (int index = 0; index < totalPoints; index++) {
			double lastX = this.pathPoints.get(index)[0];
			double lastY = this.pathPoints.get(index)[1];
			int lastSide = this.getPointSide(lastX, lastY);

			double thisX;
			double thisY;
			if (index != totalPoints - 1) {
				thisX = this.pathPoints.get(index + 1)[0];
				thisY = this.pathPoints.get(index + 1)[1];
			} else {
				thisX = this.pathPoints.get(0)[0];
				thisY = this.pathPoints.get(0)[1];
			}
			int thisSide = this.getPointSide(thisX, thisY);

			// if the side is different
			// <0 => intersect
			// =0 => sit on that line, move to next point
			// >0 => skip
			if (lastSide * thisSide < 0) {
				changePoint.add(new Double[][] { { lastX, lastY }, { thisX, thisY } });
			} else if (thisSide == 0) {
				this.interceptPoints.add(new Double[] { thisX, thisY });
				index = index + 2;
			}
		}

		return changePoint;
	}

	/*
	 * 
	 * 
	 */
	// get the side of the point, which base on the given line function
	private int getPointSide(double x, double y) {
		double value = y * this.yCoefficient + x * this.xCoefficient + this.interceptLength;

		if (value > 0) {
			return 1;
		} else if (value < 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
