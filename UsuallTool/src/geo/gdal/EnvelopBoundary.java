package geo.gdal;

public class EnvelopBoundary {

		private double maxX;
		private double maxY;
		private double minX;
		private double minY;

		public EnvelopBoundary(double maxX, double maxY, double minX, double minY) {
			this.maxX = maxX;
			this.maxY = maxY;
			this.minX = minX;
			this.minY = minY;
		}

		public void setMaxX(double maxX) {
			this.maxX = maxX;
		}

		public void setMinX(double minX) {
			this.minX = minX;
		}

		public void setMaxY(double maxY) {
			this.maxY = maxY;
		}

		public void setMinY(double minY) {
			this.minY = minY;
		}

		public double getMaxX() {
			return this.maxX;
		}

		public double getMaxY() {
			return this.maxY;
		}

		public double getMinX() {
			return this.minX;
		}

		public double getMinY() {
			return this.minY;
		}
	}
