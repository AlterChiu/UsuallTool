package Hydro.Rainfall.ReturnPeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import usualTool.AtCommonMath;
import usualTool.MathEqualtion.AtMathFunction;

public class ReturnPeriod_LPT3 implements RetrunPeriod {
	private List<Double> valueList = new ArrayList<Double>();
	private AtCommonMath commonMath;
	private double mean;
	private double dis;
	private double sk;

	public ReturnPeriod_LPT3(List<Double> valueList) {
		List<Double> adjustedList = new ArrayList<Double>(
				valueList.stream().map(value -> Math.log(value)).collect(Collectors.toList()));
		this.commonMath = new AtCommonMath(adjustedList);
		int valueSize = valueList.size();
		this.valueList.clear();
		adjustedList.clear();

		this.mean = this.commonMath.getMean();
		this.dis = this.commonMath.getStd();
		this.sk = this.commonMath.getSkewness() * Math.sqrt((valueSize * (valueSize - 1))) / (valueSize - 2)
				* (1 + 8.5 / valueSize);
	}

	@Override
	public double getPeriodRainfall(int year) {
		// TODO Auto-generated method stub
		return Math.exp(this.mean + getReturnPeriodCoefficient(year) * this.dis);
	}

	@Override
	public double getReturnPeriodCoefficient(int year) {
		double t = AtMathFunction.StandardDeviation(1. / year);
		double Csy = this.sk;
		double Kt = 2 / Csy * Math.pow((1 + Csy * t / 6 - Csy * Csy / 36), 3) - 2 / Csy;
		return Kt;
	}

	@Override
	public double getMeanValue() {
		// TODO Auto-generated method stub
		return Math.exp(this.mean);
	}

}
