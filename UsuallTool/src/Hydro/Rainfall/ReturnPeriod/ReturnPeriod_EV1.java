package Hydro.Rainfall.ReturnPeriod;

import java.util.List;

import usualTool.AtCommonMath;

public class ReturnPeriod_EV1 implements RetrunPeriod {
	private AtCommonMath commonMath;
	private double mean;
	private double dis;

	public ReturnPeriod_EV1(List<Double> valueList) {
		this.commonMath = new AtCommonMath(valueList);
		valueList.clear();

		this.mean = this.commonMath.getMean();
		this.dis = this.commonMath.getStd();
	}

	@Override
	public double getPeriodRainfall(int year) {
		// TODO Auto-generated method stub
		return this.mean + getReturnPeriodCoefficient(year) * this.dis;
	}

	@Override
	public double getReturnPeriodCoefficient(int year) {
		return Math.sqrt(6) / Math.PI * (-0.5772157 - Math.log((1 - 1. / year)));
	}

	@Override
	public double getMeanValue() {
		// TODO Auto-generated method stub
		return this.mean;
	}

}
