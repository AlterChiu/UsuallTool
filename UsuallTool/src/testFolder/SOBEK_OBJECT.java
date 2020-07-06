package testFolder;

import org.gdal.ogr.Geometry;

public class SOBEK_OBJECT {

	public static class SobekBankLine {
		private Geometry geo;
		private double[] firstPoint;
		private double[] secondPoint;
		private double distance = Double.POSITIVE_INFINITY;
		private int linkedDirection = -1; // 0 for first point , 1 for secondPoint

		private int linkedPointID = -1;
		private int id = -1;

		public SobekBankLine(Geometry geo) {
			this.geo = geo;
			this.firstPoint = new double[] { geo.GetX(0), geo.GetY(0) };
			this.secondPoint = new double[] { geo.GetX(geo.GetPointCount() - 1), geo.GetY(geo.GetPointCount() - 1) };
		}

		public Geometry getGeo() {
			return this.geo;
		}

		public double[] getFirstPoint() {
			return this.firstPoint;
		}

		public double[] getSecondPoint() {
			return this.secondPoint;
		}

		public void setID(int id) {
			this.id = id;
		}

		public int getID() {
			return this.id;
		}

		public int getLinkedDirection() {
			return this.linkedDirection;
		}

		public void setLinkedDirection(int direction) {
			this.linkedDirection = direction;
		}

		public int getLinkedPointID() {
			return this.linkedPointID;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public void setLinkedPointID(int linkedPoint) {
			this.linkedPointID = linkedPoint;
		}

		public double getDistance() {
			return this.distance;
		}

		public double getDistance(SobekBankLine otherBankLine) {

			double[] firstPoint = otherBankLine.getFirstPoint();
			double[] seconPoint = otherBankLine.getSecondPoint();

			double lengthHeadToHead = Math.sqrt(
					Math.pow(firstPoint[0] - this.firstPoint[0], 2) + Math.pow(firstPoint[1] - this.firstPoint[1], 2));
			double lengthHeadToEnd = Math.sqrt(Math.pow(firstPoint[0] - this.secondPoint[0], 2)
					+ Math.pow(firstPoint[1] - this.secondPoint[1], 2));

			double distance = 0;
			if (lengthHeadToHead > lengthHeadToEnd) {
				distance = lengthHeadToEnd + Math.sqrt(Math.pow(seconPoint[0] - this.firstPoint[0], 2)
						+ Math.pow(seconPoint[1] - this.firstPoint[1], 2));
			} else {
				distance = lengthHeadToHead + Math.sqrt(Math.pow(seconPoint[0] - this.secondPoint[0], 2)
						+ Math.pow(seconPoint[1] - this.secondPoint[1], 2));
			}

			// check current distance
			if (this.distance > distance) {
				this.distance = distance;
				this.linkedPointID = otherBankLine.getID();

				if (lengthHeadToHead > lengthHeadToEnd) {
					this.linkedDirection = 1; // fisrt point link to second
				} else {
					this.linkedDirection = 0; // first point link to first
				}
			}

			// check other distance
			if (otherBankLine.getDistance() > distance) {
				otherBankLine.setDistance(distance);
				otherBankLine.setLinkedPointID(this.id);

				if (lengthHeadToHead > lengthHeadToEnd) {
					// fisrt point link to second
					otherBankLine.setLinkedDirection(1);
				} else {
					// first point link to first
					otherBankLine.setLinkedDirection(0);
				}
			}

			return this.distance;
		}
	}
}
