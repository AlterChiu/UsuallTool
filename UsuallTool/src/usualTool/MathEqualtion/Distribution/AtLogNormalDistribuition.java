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
		this.mean = staticsMath.getMean();
		this.distribution = new NormalDistribution(this.mean, this.standarDeviation);
		staticsMath.clear();
	}

	public AtLogNormalDistribuition(double mean, double std) {
		this.standarDeviation = std;
		this.mean = mean;
		this.distribution = new NormalDistribution(this.mean, this.standarDeviation);
	}

	// <=============================================>

	@Override
	public double getDoubleRandom() {
		distribution.reseedRandomGenerator(System.currentTimeMillis());
		return new BigDecimal(Math.exp(distribution.sample())).setScale(pointScale, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	@Override
	public double getIntRandom() {
		distribution.reseedRandomGenerator(System.currentTimeMillis());
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
		return Math.exp(distribution.cumulativeProbability(cumulative));
	}

	@Override
	public double getMaxValue() {
		return Math.exp(distribution.cumulativeProbability(0.9999));
	}

	@Override
	public double getMinValue() {
		return Math.exp(distribution.cumulativeProbability(0.0001));
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
}
