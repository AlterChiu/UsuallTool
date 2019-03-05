package usualTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class AtCommonMath {
	private DescriptiveStatistics ds;
	private List<Double> valueList = new ArrayList<>();

	public AtCommonMath(double[] valueList) {
		this.valueList = Arrays.asList(ArrayUtils.toObject(valueList));
		this.ds = new DescriptiveStatistics(valueList);
	}

	public AtCommonMath(List<Double> valueList) {
		this.valueList = valueList;
		this.ds = new DescriptiveStatistics(valueList.stream().mapToDouble(Double::doubleValue).toArray());
	}

	public AtCommonMath(String[] valueList) {
		for (String value : valueList) {
			this.valueList.add(Double.parseDouble(value));
		}
		this.ds = new DescriptiveStatistics(this.valueList.stream().mapToDouble(Double::doubleValue).toArray());
	}

	public double getMax(int precision) {
		Double tempt = this.ds.getMax();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMin(int precision) {
		Double tempt = this.ds.getMin();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getStd(int precision) {
		Double tempt = this.ds.getStandardDeviation();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getSkewness(int precision) {
		Double tempt = this.ds.getSkewness();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getKurtosis(int precision) {
		Double tempt = this.ds.getKurtosis();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMean(int precision) {
		Double tempt = this.ds.getMean();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getSum(int precision) {
		Double tempt = this.ds.getSum();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getVariance(int precision) {
		Double tempt = this.ds.getVariance();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMedium(int precision) {
		return new BigDecimal(getMedium()).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getPersentage(double persantage) {
		Double tempt = this.ds.getPercentile(persantage);
		return tempt;
	}

	public double getCorrelartion(double[] arrays) {
		Double tempt = new PearsonsCorrelation().correlation(arrays,
				this.valueList.parallelStream().mapToDouble(Double::doubleValue).toArray());
		return tempt;
	}

	public double getRMS(double[] arrays) {
		List<Double> temptList = new ArrayList<>();
		for (int index = 0; index < this.valueList.size(); index++) {
			temptList.add(Math.abs(this.valueList.get(index) - arrays[index]));
		}
		return new AtCommonMath(temptList).getMean() * Math.sqrt(arrays.length);
	}

	public double getMax() {
		Double tempt = this.ds.getMax();
		return tempt;
	}

	public double getMin() {
		Double tempt = this.ds.getMin();
		return tempt;
	}

	public double getStd() {
		Double tempt = this.ds.getStandardDeviation();
		return tempt;
	}

	public double getSkewness() {
		Double tempt = this.ds.getSkewness();
		return tempt;
	}

	public double getKurtosis() {
		Double tempt = this.ds.getKurtosis();
		return tempt;
	}

	public double getMean() {
		Double tempt = this.ds.getMean();
		return tempt;
	}

	public double getSum() {
		Double tempt = this.ds.getSum();
		return tempt;
	}

	public double getVariance() {
		Double tempt = this.ds.getVariance();
		return tempt;
	}

	public double getMedium() {
		List<Double> temptList = getSortedList();
		Double tempt = temptList.get(temptList.size() / 2);
		return tempt;
	}

	// begin from the minValue
	public List<Double> getSortedList() {
		List<Double> sortedList = new ArrayList<>();
		for (double temptDouble : this.ds.getSortedValues()) {
			sortedList.add(temptDouble);
		}
		return sortedList;
	}

	public final void clear() {
		this.valueList.clear();
		this.ds.clear();
	}
}
