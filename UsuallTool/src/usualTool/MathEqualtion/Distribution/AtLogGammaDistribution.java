package usualTool.MathEqualtion.Distribution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.GammaDistribution;

import usualTool.AtCommonMath;

public class AtLogGammaDistribution implements AtDistribution {

	private double shape;
	private double scale;
	private GammaDistribution distribution;
	private int pointScale = 3;

	/*
	 * 
	 */
	// <=============================================>
	// < constructor>
	// <=============================================>
	public AtLogGammaDistribution(List<Double> valueList) {
		List<Double> reviseList = new ArrayList<>();
		valueList.forEach(e -> reviseList.add(Math.log(e)));

		setParameter(new AtCommonMath(valueList));
		this.distribution = new GammaDistribution(this.shape, this.scale);
	}

	public AtLogGammaDistribution(double shape, double scale) {
		if (shape <= 0 || scale <= 0) {
			System.out.print("error while initialize AtLogGammaDistribution while ");
			if (shape <= 0) {
				System.out.print(",shape parameter lower than 0");
			}
			if (scale <= 0) {
				System.out.print(",scale parameter lower than 0");
			}
		} else {
			this.shape = shape;
			this.scale = scale;
			this.distribution = new GammaDistribution(this.shape, this.scale);
		}
	}

	private void setParameter(AtCommonMath staticsMath) {
		if (staticsMath.getMean() <= 0) {
			System.out.println("mean value couldn't lower than 0, while LogGammaDistribution");
		} else {
			this.scale = staticsMath.getVariance() / staticsMath.getMean();
			this.shape = (Math.pow(staticsMath.getSkewness() / staticsMath.getKurtosis() * 3, 2)
					+ staticsMath.getMean() / scale) / 2;
		}
	}

	// <================================================>

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
		return getValue(0.01);
	}

	@Override
	public void setPointScale(int scale) {
		pointScale = scale;
	}

	@Override
	public double getCumulative(double x) {
		return Math.exp(distribution.cumulativeProbability(x));
	}
}
