package usualTool.MathEqualtion.Distribution;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.GumbelDistribution;

import usualTool.AtCommonMath;

public class AtGumbelDistribution implements AtDistribution {
	private double constantGama = 0.5772156649;
	private double location;
	private double scale;
	private GumbelDistribution distribution;
	private int pointScale = 3;

	public AtGumbelDistribution(List<Double> valueList) {
		setParameter(new AtCommonMath(valueList));
		this.distribution = new GumbelDistribution(this.location, this.scale);
	}

	public AtGumbelDistribution(double location, double scale) {
		if (scale <= 0) {
			System.out.print("error while initialize AtGammaDistribution while ");
			System.out.print(",scale parameter lower than 0");
		} else {
			this.location = location;
			this.scale = scale;
			this.distribution = new GumbelDistribution(this.location, this.scale);
		}
	}

	private void setParameter(AtCommonMath staticsMath) {
		double scale = 6 / Math.pow(Math.PI, 2) * staticsMath.getVariance();
		double location1 = (Math.log(Math.log(2)) * staticsMath.getMean() + constantGama * staticsMath.getMedium())
				/ (Math.log(Math.log(2)) + constantGama);
		double location2 = staticsMath.getMean() - scale * constantGama;
		double location3 = staticsMath.getMedium() + scale * Math.log(Math.log(2));

		this.scale = scale;
		this.location = (location1 + location2 + location3) / 3;
	}

	@Override
	public double getDoubleRandom() {
		distribution.reseedRandomGenerator(System.currentTimeMillis());
		return new BigDecimal(distribution.sample()).setScale(pointScale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	public double getIntRandom() {
		distribution.reseedRandomGenerator(System.currentTimeMillis());
		return new BigDecimal(distribution.sample()).setScale(pointScale, BigDecimal.ROUND_HALF_UP).intValue();
	}

	@Override
	public double getProbability(double x) {
		return distribution.density(x);
	}

	@Override
	public double getProbability(double lowBoundary, double upBoundary) {
		return distribution.probability(lowBoundary, upBoundary);
	}

	@Override
	public double getValue(double cumulative) {
		double minValue = this.distribution.getSupportLowerBound();
		if (minValue < -9999) {
			minValue = -9999;
		}

		double maxValue = this.distribution.getSupportUpperBound();
		if (maxValue > 9999) {
			maxValue = 9999;
		}

		double temptValue = (minValue + maxValue) / 2;
		double tempCum = this.getCumulative(temptValue);
		int convergenceTime = 0;

		// convergence to close cumulative by precise is 1%
		while (Math.abs(tempCum - cumulative) > 0.005 && convergenceTime < 20) {
			// move to left
			if (tempCum > cumulative) {
				maxValue = temptValue;
				temptValue = (minValue + maxValue) / 2;

				// move to right
			} else {
				minValue = temptValue;
				temptValue = (minValue + maxValue) / 2;
			}
			tempCum = this.getCumulative(temptValue);
		}

		return temptValue;
	}

	@Override
	public double getMaxValue() {
		return getValue(0.99);
	}

	@Override
	public double getMinValue() {
		return getValue(0.01);
	}

	@Override
	public void setPointScale(int scale) {
		pointScale = scale;
	}

	public double getLocation() {
		return this.location;
	}

	public double getScale() {
		return this.scale;
	}

	@Override
	public double getCumulative(double x) {
		return distribution.cumulativeProbability(x);
	}

}
