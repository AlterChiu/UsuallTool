package geo.path;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class IntersectLine {
	private Path2D polygon;
	private double slope = 0;
	private double interceptLength = 0;
	private List<Double[]> interceptPoints = new ArrayList<Double[]>();

	public IntersectLine(Path2D path) {
		this.polygon = path;
	}

	public List<Double[]> getInterceptPoints(double slope, double interceptLength) {
		this.slope = slope;
		this.interceptLength = interceptLength;
		this.interceptPoints.clear();

		// get the group point which are in the different side by the given line
		List<Double[][]> changedPoints = getChangedPoint();
		// use group point to find out the intersect of two line
		this.calculateIntercept(changedPoints);

		return this.interceptPoints;
	}

	/*
	 * 
	 * 
	 */
	// get the intercept point of two line, one is given line function
	// and the other one is get by "getChangedPoint"
	private void calculateIntercept(List<Double[][]> groupPoints) {
		for (Double[][] groupPoint : groupPoints) {

			// if the line is horizontal
			if (Math.abs((groupPoint[0][1] - groupPoint[1][1])) < 0.00001) {
				double temptSlope = 0.;
				double temptInterceptLength = groupPoint[0][1] - temptSlope * groupPoint[0][0];
				if (Math.abs(temptSlope - this.slope) > 0.00001) {
					this.interceptPoints.add(getLineIntercept(temptSlope, temptInterceptLength));
				}

				// if the line is vertical
			} else if (Math.abs(groupPoint[0][0] - groupPoint[1][0]) < 0.000001) {
				double x = groupPoint[0][0];
				double y = this.slope * x + this.interceptLength;
				this.interceptPoints.add(new Double[] { x, y });

				// the others way
			} else {
				double temptSlope = 0.;
				double temptInterceptLength = groupPoint[0][1] - temptSlope * groupPoint[0][0];
				if (Math.abs(temptSlope - this.slope) > 0.00001) {
					this.interceptPoints.add(getLineIntercept(temptSlope, temptInterceptLength));
				}
			}

		}
	}

	/**
	 * 
	 * @param temptSlope
	 * @param tmeptInterceptLength
	 * @return
	 */
	// get the point which intercept by to line
	private Double[] getLineIntercept(double temptSlope, double tmeptInterceptLength) {
		double x = (tmeptInterceptLength - this.interceptLength) / (this.slope - temptSlope);
		double y = this.slope * x + this.interceptLength;

		return new Double[] { x, y };
	}

	/**
	 * 
	 * 
	 * @return
	 */
	// get the two point, one is inside the polygon and the other one isn't
	private List<Double[][]> getChangedPoint() {
		List<Double[][]> interceptPiont = new ArrayList<Double[][]>();
		PathIterator pathIterator = this.polygon.getPathIterator(null);
		float coordinate[] = new float[6];

		// get first point
		pathIterator.currentSegment(coordinate);
		double lastX = coordinate[0];
		double lastY = coordinate[1];
		int lastSide = getPointSide(lastX, lastY);

		// check if the start point in on the line
		if (lastSide == 0) {
			this.interceptPoints.add(new Double[] { lastX, lastY });

			pathIterator.currentSegment(coordinate);
			lastX = coordinate[0];
			lastY = coordinate[1];
			lastSide = getPointSide(lastX, lastY);
		}
		pathIterator.next();

		// check for other point
		for (; !pathIterator.isDone(); pathIterator.next()) {
			pathIterator.currentSegment(coordinate);
			double thisX = coordinate[0];
			double thisY = coordinate[1];
			int thisSide = getPointSide(thisX, thisY);

			// if the side is different
			// <0 => intersect
			// =0 => sit on that line, move to next point
			// >0 => skip
			if (lastSide * thisSide < 0) {
				interceptPiont.add(new Double[][] { { lastX, lastY }, { thisX, thisY } });
				System.out.println(lastX + "\t" + lastY + "\t" + thisX + "\t" + thisY);
			} else if (thisSide == 0) {
				this.interceptPoints.add(new Double[] { thisX, thisY });

				pathIterator.next();
				thisX = coordinate[0];
				thisY = coordinate[1];
				thisSide = getPointSide(lastX, lastY);
			}

			// move to next property
			lastX = thisX;
			lastY = thisY;
			lastSide = thisSide;
		}

		return interceptPiont;
	}

	/*
	 * 
	 * 
	 */
	// get the side of the point, which base on the given line function
	private int getPointSide(double x, double y) {
		double value = y - this.slope * x - this.interceptLength;
		if (value > 0) {
			return 1;
		} else if (value < 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
