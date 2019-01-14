package Hydro.Rainfall.ReturnPeriod;

import java.util.List;

import usualTool.AtCommonMath;
import usualTool.MathEqualtion.AtMathFunction;

public class ReturnPeriod_PT3 implements RetrunPeriod {
	private AtCommonMath commonMath;
	private double mean;
	private double dis;
	private double sk;

	public ReturnPeriod_PT3(List<Double> valueList) {
		this.commonMath = new AtCommonMath(valueList);
		int valueSize = valueList.size();
		valueList.clear();

		this.mean = this.commonMath.getMean();
		this.dis = this.commonMath.getStd();
		this.sk = this.commonMath.getSkewness() * Math.sqrt((valueSize * (valueSize - 1))) / (valueSize - 2)
				* (1 + 8.5 / valueSize);
	}

	@Override
	public double getPeriodRainfall(int year) {
		// TODO Auto-generated method stub
		return this.mean + getReturnPeriodCoefficient(year) * this.dis;
	}

	@Override
	public double getReturnPeriodCoefficient(int year) {
		// TODO Auto-generated method stub
		double t = AtMathFunction.StandardDeviation(1. / year);
		double Csy = this.sk;
		double Kt = 2 / Csy * Math.pow((1 + Csy * t / 6 - Csy * Csy / 36), 3) - 2 / Csy;
		return Kt;
	}

	@Override
	public double getMeanValue() {
		// TODO Auto-generated method stub
		return this.mean;
	}

}
