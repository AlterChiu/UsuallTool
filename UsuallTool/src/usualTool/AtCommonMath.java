package usualTool;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class AtCommonMath {
	private static int precision = 3;
	DescriptiveStatistics ds;
	double list[] ;

	public AtCommonMath(double[] valueList) {
		this.list = valueList;
		this.ds = new DescriptiveStatistics(valueList);
	}

	public AtCommonMath(ArrayList<Double> valueList) {
		this.ds = new DescriptiveStatistics(valueList.stream().mapToDouble(Double::doubleValue).toArray());
	}
	public AtCommonMath(String[] valueList){
		ArrayList<Double>tempt = new ArrayList<Double>();
		for(String value : valueList){
			tempt.add(Double.parseDouble(value));
		}
		this.ds = new DescriptiveStatistics(tempt.parallelStream().mapToDouble(Double::doubleValue).toArray());
	}
	
	public double getMax() {
		double tempt = this.ds.getMax();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMin() {
		double tempt = this.ds.getMin();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getStd() {
		double tempt = this.ds.getStandardDeviation();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getSkewness() {
		double tempt = this.ds.getSkewness();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getKurtosis() {
		double tempt = this.ds.getKurtosis();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMean() {
		double tempt = this.ds.getMean();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getSum() {
		double tempt = this.ds.getSum();
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getCorrelartion(double[] arrays) {
		double tempt = new PearsonsCorrelation().correlation(arrays,this.list);
		return new BigDecimal(tempt).setScale(this.precision, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
