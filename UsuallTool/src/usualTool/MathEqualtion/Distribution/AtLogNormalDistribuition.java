package usualTool.MathEqualtion.Distribution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import usualTool.AtCommonMath;

public class AtLogNormalDistribuition implements AtDistribution {
	private double standarDeviation;
	private double mean;
	private NormalDistribution distribution;
	private int pointScale = 3;

	/*
	 * 
	 */
	// <=============================================>
	// < constructor>
	// <=============================================>
	public AtLogNormalDistribuition(List<Double> valueList) {
		List<Double> reviseList = new ArrayList<>();
		valueList.forEach(e -> reviseList.add(Math.log(e)));
		
		AtCommonMath staticsMath = new AtCommonMath(reviseList);
		this.standarDeviation = staticsMath.getStd();
		System.out.println(standarDeviation);
		this.mean = staticsMath.getMean();
		this.distribution = new NormalDistribution(this.mean, this.standarDeviation);
		distribution.reseedRandomGenerator(System.currentTimeMillis());
		staticsMath.clear();
	}

	public AtLogNormalDistribuition(double mean, double std) {
		this.standarDeviation = std;
		this.mean = mean;
		this.distribution = new NormalDistribution(this.mean, this.standarDeviation);
		distribution.reseedRandomGenerator(System.currentTimeMillis());
	}

	// <=============================================>

	@Override
	public double getDoubleRandom() {
		return new BigDecimal(Math.exp(distribution.sample())).setScale(pointScale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	@Override
	public double getIntRandom() {
		return new BigDecimal(Math.exp(distribution.sample())).setScale(pointScale, BigDecimal.ROUND_HALF_UP)
				.intValue();
	}

	@Override
	public double getProbability(double x) {
		x = Math.log(x);
		return distribution.density(x);
	}

	@Override
	public double getProbability(double lowBoundary, double upBoundary) {
		lowBoundary = Math.log(lowBoundary);
		upBoundary = Math.log(upBoundary);
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

		return Math.exp(temptValue);
	}

	@Override
	public double getMaxValue() {
		return getValue(0.99);
	}

	@Override
	public double getMinValue() {
		return getValue(0.00);
	}

	@Override
	public void setPointScale(int scale) {
		pointScale = scale;
	}

	public double getMean() {
		return Math.exp(this.mean);
	}

	public double getStandarDeviation() {
		return Math.exp(this.standarDeviation);

	}

	@Override
	public double getCumulative(double x) {
		return Math.exp(this.mean);
	}
}
