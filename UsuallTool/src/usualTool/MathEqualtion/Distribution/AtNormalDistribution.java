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
	}

	public AtNormalDistribution(double mean, double std) {
		this.standarDeviation = std;
		this.mean = mean;
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
	public double getValue(double x) {
		return distribution.cumulativeProbability(x);
	}

	@Override
	public double getMaxValue() {
		return distribution.cumulativeProbability(0.9999);
	}

	@Override
	public double getMinValue() {
		return distribution.cumulativeProbability(0.0001);
	}

	@Override
	public void setPointScale(int scale) {
		pointScale = scale;
	}

	public double getStandarDeviation() {
		return this.standarDeviation;
	}

	public double getMean() {
		return this.mean;
	}

}
