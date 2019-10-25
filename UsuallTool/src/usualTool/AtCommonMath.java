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

	public double getClosestValue(double targetValue) {
		double dis = Double.POSITIVE_INFINITY;
		double outValue = Double.NaN;

		for (double temptValue : this.valueList) {
			double temptDis = Math.abs(targetValue - temptValue);
			if (temptDis < dis) {
				dis = temptDis;
				outValue = temptValue;
			}
		}

		return outValue;
	}

	public List<Double> getRatio() {
		double sum = this.getSum();
		List<Double> ratio = new ArrayList<>();
		this.valueList.forEach(e -> ratio.add(e / sum));
		return ratio;
	}

	public static double getDecimal_Double(double value, int scale) {
		return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static double getDecimal_Double(String value, int scale) {
		return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static String getDecimal_String(double value, int scale) {
		return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
	}

	public static String getDecimal_String(String value, int scale) {
		return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
	}

	public static int getDecimal_Int(double value, int scale) {
		return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).intValue();
	}

	public static int getDecimal_Int(String value, int scale) {
		return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).intValue();
	}

	public static double getAzimuth(double[] vector) {
		return getAzimuth(new double[] { 0, 0 }, vector);
	}

	public static double getAzimuth(double[] startPoint, double[] endPoint) {
		return getAzimuth(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
	}

	// return azimuth, start from north, clockwise
	public static double getAzimuth(double startPointX, double startPointY, double endPointX, double endPointY) {
		double[] vector = new double[] { endPointX - startPointX, endPointY - startPointY };

		int quadrand = getMathQuadrand(vector);
		if (quadrand == 0) {

			// on coordinate axis

			// +x
			if (vector[0] > 0) {
				return 3. / 4 * 2 * Math.PI;
			}
			// -x
			else if (vector[0] < 0) {
				return 1. / 4 * 2 * Math.PI;
			}
			// +y
			else if (vector[1] > 0) {
				return 0;
			}
			// -y
			else {
				return 2. / 4 * 2 * Math.PI;
			}

			// if not on coordinate

		}
		// ++
		else if (quadrand == 1) {
			return Math.atan(Math.abs(vector[0]) / Math.abs(vector[1]));
		}
		// -+
		else if (quadrand == 2) {
			return 2 * Math.PI - Math.atan(Math.abs(vector[0]) / Math.abs(vector[1]));
		}
		// --
		else if (quadrand == 3) {
			return 2. / 4 * 2 * Math.PI + Math.atan(Math.abs(vector[0]) / Math.abs(vector[1]));
		}
		// +-
		else {
			return 2. / 4 * 2 * Math.PI - Math.atan(Math.abs(vector[0]) / Math.abs(vector[1]));
		}
	}

	// return quadrand in math
	// 2 1
	// 3 4
	// if on coordinate axis => return 0
	public static int getMathQuadrand(double[] vector) {
		if (vector[0] > 0 && vector[1] > 0) {
			return 1;
		} else if (vector[0] > 0 && vector[1] < 0) {
			return 4;
		} else if (vector[0] < 0 && vector[1] > 0) {
			return 3;
		} else if (vector[0] < 0 && vector[1] < 0) {
			return 2;
		} else {
			return 0;
		}
	}

	public final void clear() {
		this.ds.clear();
	}
}
