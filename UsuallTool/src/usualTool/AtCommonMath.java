package usualTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class AtCommonMath {
	private static int precision = 3;
	DescriptiveStatistics ds;
	double list[];

	public AtCommonMath(double[] valueList) {
		this.list = valueList;
		this.ds = new DescriptiveStatistics(valueList);
	}

	public AtCommonMath(List<Double> valueList) {
		this.list = valueList.stream().mapToDouble(Double::doubleValue).toArray();
		this.ds = new DescriptiveStatistics(valueList.stream().mapToDouble(Double::doubleValue).toArray());
	}

	public AtCommonMath(String[] valueList) {
		ArrayList<Double> tempt = new ArrayList<Double>();
		for (String value : valueList) {
			tempt.add(Double.parseDouble(value));
		}
		this.list = tempt.stream().mapToDouble(Double::doubleValue).toArray();
		this.ds = new DescriptiveStatistics(tempt.parallelStream().mapToDouble(Double::doubleValue).toArray());
	}

	public double getMax() {
		double tempt = this.ds.getMax();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMin() {
		double tempt = this.ds.getMin();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getStd() {
		double tempt = this.ds.getStandardDeviation();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getSkewness() {
		double tempt = this.ds.getSkewness();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getKurtosis() {
		double tempt = this.ds.getKurtosis();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMean() {
		double tempt = this.ds.getMean();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getSum() {
		double tempt = this.ds.getSum();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getCorrelartion(double[] arrays) {
		double tempt = new PearsonsCorrelation().correlation(arrays, this.list);
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getClosestValue(double value) {
		double temptValue = 999999999;
		double temptClosest = -99999;
		for (int index = 0; index < this.list.length; index++) {
			if (Math.abs(this.list[index] - value) < temptValue) {
				temptClosest = this.list[index];
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptClosest;
	}

	public int getClosestIndex(double value) {
		int temptIndex = 0;
		double temptValue = 999999999;
		for (int index = 0; index < this.list.length; index++) {
			if (Math.abs(this.list[index] - value) < temptValue) {
				temptIndex = index;
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptIndex;
	}

	public double getClosestValue(double value, double maxDis) {
		double temptValue = 999999999;
		double temptClosest = -99999;
		for (int index = 0; index < this.list.length; index++) {
			double temptDis = Math.abs(this.list[index] - value);
			if (temptDis < temptValue && temptDis <= maxDis) {
				temptClosest = this.list[index];
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptClosest;
	}

	public int getClosestIndex(double value, double maxDis) {
		int temptIndex = 0;
		double temptValue = 999999999;
		for (int index = 0; index < this.list.length; index++) {
			double temptDis = Math.abs(this.list[index] - value);
			if (temptDis < temptValue && temptDis <= maxDis) {
				temptIndex = index;
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptIndex;
	}

	public double getFarestValue(double value) {
		double temptValue = 0;
		double temptFarest = 0;
		for (int index = 0; index < this.list.length; index++) {
			if (Math.abs(this.list[index] - value) > temptValue) {
				temptFarest = this.list[index];
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptFarest;
	}

	public int getFarestIndex(double value) {
		double temptValue = 0;
		int temptFarest = 0;
		for (int index = 0; index < this.list.length; index++) {
			if (Math.abs(this.list[index] - value) > temptValue) {
				temptFarest = index;
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptFarest;
	}

	public double getFarestValue(double value, double minDis) {
		double temptValue = 0;
		double temptFarest = 0;
		for (int index = 0; index < this.list.length; index++) {
			double temptDis = Math.abs(this.list[index] - value);
			if (temptDis > temptValue && temptDis >= minDis) {
				temptFarest = this.list[index];
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptFarest;
	}

	public int getFarestIndex(double value, double minDis) {
		double temptValue = 0;
		int temptFarest = 0;
		for (int index = 0; index < this.list.length; index++) {
			double temptDis = Math.abs(this.list[index] - value);
			if (temptDis > temptValue && temptDis >= minDis) {
				temptFarest = index;
				temptValue = Math.abs(this.list[index] - value);
			}
		}
		return temptFarest;
	}

	public final void clear() {
		this.list = null;
		this.ds.clear();
	}
}
