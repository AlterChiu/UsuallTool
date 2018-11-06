package usualTool.MathEqualtion.Distribution;

public interface AtDistribution {

	public double getDoubleRandom();

	public double getIntRandom();

	public double getProbability(double x);

	public double getProbability(double lowBoundary, double upBoundary);

	public double getCumulative(double x);

	public double getValue(double cumulative);

	public double getMaxValue();

	public double getMinValue();

	public void setPointScale(int scale);
}
