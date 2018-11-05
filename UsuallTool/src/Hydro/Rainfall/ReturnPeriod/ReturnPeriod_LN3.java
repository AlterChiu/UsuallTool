package Hydro.Rainfall.ReturnPeriod;

import java.util.List;

import usualTool.AtCommonMath;
import usualTool.MathEqualtion.AtMathFunction;

public class ReturnPeriod_LN3 implements RetrunPeriod {
	private AtCommonMath commonMath;
	private double mean;
	private double dis;
	private double sk;

	public ReturnPeriod_LN3(List<Double> valueList) {
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
		double t = AtMathFunction.StandardDeviation(1 / year);
		double w = (-1 * this.sk + Math.sqrt(this.sk * this.sk + 4)) / 2;
		double z = (1 - Math.pow(w, 2 / 3)) / Math.pow(w, 1 / 3);
		double Sy = Math.sqrt(Math.log(z * z + 1));
		double Kt_top = Math.exp(Sy * t - Sy * Sy / 2) - 1;
		double Kt_down = Math.sqrt(Math.exp(Sy * Sy) - 1);
		return Kt_top / Kt_down;
	}

	@Override
	public double getMeanValue() {
		// TODO Auto-generated method stub
		return this.mean;
	}
}
