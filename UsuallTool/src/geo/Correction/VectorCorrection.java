
package geo.Correction;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialFeature;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.MathEqualtion.AtLeastSquareMatrix;

public class VectorCorrection {
	private int variables;
	private List<CorrectionSnap> correctionSnapList = new ArrayList<>();

	public VectorCorrection(CorrectionModel correctionModel) throws Exception {
		switch (correctionModel) {
		case fourCoefficientCorrection:
			this.variables = 4;

		case sevenCorefficientCorrection:
			throw new Exception("not available now");
		}
	}

	public void addSnap(double sourceX, double sourceY, double targetX, double targetY) {
		this.addSnap(sourceX, sourceY, targetX, targetY, 1);
	}

	public void addSnap(double sourceX, double sourceY, double targetX, double targetY, double weight) {
		CorrectionSnap coorectionSnap = new CorrectionSnap();
		coorectionSnap.setSourceX(sourceX);
		coorectionSnap.setSourceY(sourceY);
		coorectionSnap.setTargetX(targetX);
		coorectionSnap.setTargetY(targetY);
		coorectionSnap.setWeight(weight);
		this.correctionSnapList.add(coorectionSnap);
	}

	// 4 coefficient result : ratio , thiata , deltaX , deltaY
	// which thiata in radius
	public List<Double> getCorrectionVariables() throws Exception {

		if (this.correctionSnapList.size() * 2 < this.variables) {
			throw new Exception("not enough equaltion to solve correction variables");

			// least square matrix equation
			// V = A * X - L
		} else {
			AtLeastSquareMatrix leastSquare = new AtLeastSquareMatrix(this.variables);
			this.correctionSnapList.forEach(snap -> {
				leastSquare.addEqualtion(new Double[] { snap.getSourceX(), snap.getSourceY(), 1.0, 0.0 },
						snap.getSourceX(), snap.getWeight());
				leastSquare.addEqualtion(new Double[] { snap.getSourceY(), -1 * snap.getSourceX(), 0.0, 1.0 },
						snap.getSourceY(), snap.getWeight());
			});

			// return ratio * Cos(thita) , ratio * Sin(thita) , deltaX , deltaY
			List<Double> variables = leastSquare.getVariablesValue();

			// thita in radius
			double thita = Math.atan(variables.get(1) / variables.get(0));
			double ratio = variables.get(0) / Math.cos(thita);

			return new ArrayList<>(Arrays.asList(new Double[] { ratio, thita, variables.get(2), variables.get(3) }));
		}

	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++ STATIC FUNCTIONS+++++++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>

	public static class GeometryCorrection4 {

		public GeometryCorrection4(String sourceFileAdd, String saveAdd, String saveType, double thita, double ratio,
				double deltaX, double deltaY) throws UnsupportedEncodingException {

			// read file
			// <--------------------------------------------------->
			SpatialReader spatialFile = new SpatialReader(sourceFileAdd);
			List<SpatialFeature> featureList = spatialFile.getFeatureList();
			Map<String, String> featureType = spatialFile.getAttributeTitleType();

			SpatialWriter writer = new SpatialWriter().setFieldType(featureType);

			/*
			 * translation with 4 coefficient correction
			 */
			featureList.forEach(feature -> {
				Geometry geometry = feature.getGeometry();

				GdalGlobal.MultiPolyToSingle(geometry).forEach(singleGeo -> {
					GeometryCorrection_4_Tanslation(singleGeo, thita, ratio, deltaX, deltaY);
					writer.addFeature(singleGeo, feature.getProperties());
				});
			});

			// output shp
			writer.saveAs(saveAdd, saveType);
		}

		private void GeometryCorrection_4_Tanslation(Geometry geometry, double thita, double ratio, double deltaX,
				double deltaY) {

			// translate variables
			// <------------------------------------------------->
			double aCos = ratio * Math.cos(thita);
			double aSin = ratio * Math.sin(thita);

			/*
			 * translation with 4 coefficient correction
			 */
			for (int geoCount = 0; geoCount < geometry.GetGeometryCount(); geoCount++) {

				Geometry temptGeo = geometry.GetGeometryRef(geoCount);
				for (int pointIndex = 0; pointIndex < temptGeo.GetPointCount(); pointIndex++) {
					// source XY
					double x = temptGeo.GetX(pointIndex);
					double y = temptGeo.GetY(pointIndex);

					// z value
					double zValue;
					try {
						zValue = temptGeo.GetZ(pointIndex);
					} catch (Exception e) {
						zValue = 0.0;
					}

					// x value
					double outX = aCos * x + aSin * y + deltaX;
					double outY = -1 * aSin * x + aCos * y + deltaY;

					temptGeo.SetPoint(pointIndex, outX, outY, zValue);
				}
			}
		}

	}

	// thita in radius
	public static List<Double[]> FourCoefficientCorrection(List<Double[]> points, double thita, double ratio,
			double deltaX, double deltaY) {
		List<Double[]> outList = new ArrayList<>();

		points.forEach(point -> {
			try {
				outList.add(
						VectorCorrection.FourCoefficientCorrection(point[0], point[1], thita, ratio, deltaX, deltaY));
			} catch (Exception e) {
				outList.add(null);
			}
		});

		return outList;
	}

	// thita in radius
	public static Double[] FourCoefficientCorrection(double x, double y, double ratio, double thita, double deltaX,
			double deltaY) {

		double outX = ratio * (Math.cos(thita) * x + Math.sin(thita) * y) + deltaX;
		double outY = ratio * (Math.cos(thita) * y - Math.sin(thita) * x) + deltaY;

		return new Double[] { outX, outY };
	}

	public class CorrectionSnap {
		private double sourceX;
		private double sourceY;
		private double targetX;
		private double targetY;
		private double weight = 1;

		public void setWeight(double weight) {
			this.weight = weight;
		}

		public void setSourceX(double sourceX) {
			this.sourceX = sourceX;
		}

		public void setSourceY(double sourceY) {
			this.sourceY = sourceY;
		}

		public void setTargetX(double targetX) {
			this.targetX = targetX;
		}

		public void setTargetY(double targetY) {
			this.targetY = targetY;
		}

		public double getSourceX() {
			return this.sourceX;
		}

		public double getSourceY() {
			return this.sourceY;
		}

		public double getTargetX() {
			return this.targetX;
		}

		public double getTargetY() {
			return this.targetY;
		}

		public double getWeight() {
			return this.weight;
		}
	}

	public static enum CorrectionModel {
		fourCoefficientCorrection, sevenCorefficientCorrection
	}

}
