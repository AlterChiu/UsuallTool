package usualTool.MathEqualtion.Distribution;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import usualTool.AtCommonMath;

public class AtNormalDistribution implements AtDistribution {
	private double standarDeviation;
	private double mean;
	private NormalDistribution distribution;
	private int pointScale = 3;

	public AtNormalDistribution(List<Double> valueList) {
		AtCommonMath staticsMath = new AtCommonMath(valueList);
		this.standarDeviation = staticsMath.getStd();
		this.mean = staticsMath.getMean();
		staticsMath.clear();

		this.distribution = new NormalDistribution(this.mean, this.standarDeviation);
		distribution.reseedRandomGenerator(System.currentTimeMillis());
	}

	public AtNormalDistribution(double mean, double std) {
		this.standarDeviation = std;
		this.mean = mean;
		this.distribution = new NormalDistribution(this.mean, this.standarDeviation);
		distribution.reseedRandomGenerator(System.currentTimeMillis());
	}

	@Override
	public double getDoubleRandom() {
		return new BigDecimal(distribution.sample()).setScale(pointScale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	public double getIntRandom() {
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
			convergenceTime++;
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

	@Override
	public double getCumulative(double x) {
		return distribution.cumulativeProbability(x);
	}

	public double getStandarDeviation() {
		return this.standarDeviation;
	}

	public double getMean() {
		return this.mean;
	}

}
