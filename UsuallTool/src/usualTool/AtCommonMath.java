package usualTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class AtCommonMath {
	private static int precision = 3;
	private DescriptiveStatistics ds;
	private List<Double> sortedList = new ArrayList<Double>();

	public AtCommonMath(double[] valueList) {
		this.ds = new DescriptiveStatistics(valueList);
		for (double temptDouble : this.ds.getSortedValues()) {
			this.sortedList.add(temptDouble);
		}
	}

	public AtCommonMath(List<Double> valueList) {
		this.ds = new DescriptiveStatistics(valueList.stream().mapToDouble(Double::doubleValue).toArray());
		for (double temptDouble : this.ds.getSortedValues()) {
			this.sortedList.add(temptDouble);
		}
	}

	public AtCommonMath(String[] valueList) {
		ArrayList<Double> tempt = new ArrayList<Double>();
		for (String value : valueList) {
			tempt.add(Double.parseDouble(value));
		}
		this.ds = new DescriptiveStatistics(tempt.parallelStream().mapToDouble(Double::doubleValue).toArray());
		for (double temptDouble : this.ds.getSortedValues()) {
			this.sortedList.add(temptDouble);
		}
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

	public double getVariance() {
		double tempt = this.ds.getVariance();
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMedium() {
		double tempt = this.sortedList.get(this.sortedList.size() / 2);
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getPersentage(double persantage) {
		double tempt = this.ds.getPercentile(persantage);
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getCorrelartion(double[] arrays) {
		double tempt = new PearsonsCorrelation().correlation(arrays,
				this.sortedList.parallelStream().mapToDouble(Double::doubleValue).toArray());
		return new BigDecimal(tempt).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public final void clear() {
		this.sortedList.clear();
		this.ds.clear();
	}
}
