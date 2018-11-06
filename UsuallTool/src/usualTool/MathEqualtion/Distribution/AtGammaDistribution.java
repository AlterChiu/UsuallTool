package usualTool.MathEqualtion.Distribution;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.math3.distribution.GammaDistribution;

import usualTool.AtCommonMath;

public class AtGammaDistribution implements AtDistribution {
	private double shape;
	private double scale;
	private GammaDistribution distribution;
	private int pointScale = 3;

	public AtGammaDistribution(List<Double> valueList) {
		setParameter(new AtCommonMath(valueList));
		this.distribution = new GammaDistribution(this.shape, this.scale);
	}

	public AtGammaDistribution(double shape, double scale) {
		if (shape <= 0 || scale <= 0) {
			System.out.print("error while initialize AtGammaDistribution while ");
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
			System.out.println("mean value couldn't lower than 0, while AtGammaDistribution");
		} else {
			this.scale = staticsMath.getVariance() / staticsMath.getMean();
			this.shape = (Math.pow(staticsMath.getSkewness() / staticsMath.getKurtosis() * 3, 2)
					+ staticsMath.getMean() / scale) / 2;
		}
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

	public double getScale() {
		return this.scale;
	}

	public double getShape() {
		return this.shape;
	}

}
