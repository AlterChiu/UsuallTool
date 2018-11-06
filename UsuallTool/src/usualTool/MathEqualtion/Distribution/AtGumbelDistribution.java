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

	public double getLocation() {
		return this.location;
	}

	public double getScale() {
		return this.scale;
	}

}
