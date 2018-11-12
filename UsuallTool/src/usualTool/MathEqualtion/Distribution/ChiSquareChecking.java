package usualTool.MathEqualtion.Distribution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtCommonMath;

public class ChiSquareChecking {
	private AtCommonMath statics;
	private List<Double> valueList;
	private double maxValue;
	private double minValue;
	private double midleValue;
	private Map<String, List<Double>> groupMap = new TreeMap<>();

	private int setGroupNumber = 5;

	public ChiSquareChecking(List<Double> valueList) {
		this.statics = new AtCommonMath(this.valueList);
		this.valueList = this.statics.getSortedList();
		this.maxValue = this.valueList.get(this.valueList.size());
		this.minValue = this.valueList.get(0);
		this.midleValue = this.statics.getMedium();

	}

	public void groupListInitialize(int groupSize) {
		this.groupMap.clear();
		List<Double> temptList = this.valueList;
		this.setGroupNumber = groupSize;

		// for the left side
		double leftDiffer = (this.midleValue - this.minValue) / (this.setGroupNumber / 2);
		for (int group = 0; group < this.setGroupNumber / 2; group++) {
			double groupLimit = this.minValue + group * leftDiffer;

			// add the value to groupList which is under limit
			List<Double> groupList = new ArrayList<Double>();
			while (temptList.size() > 0 && temptList.get(0) <= groupLimit) {
				groupList.add(temptList.get(0));
				temptList.remove(0);
			}
			groupMap.put(new BigDecimal(groupLimit).setScale(3, BigDecimal.ROUND_HALF_UP).toString(), groupList);
		}

		// for the right side
		double rightDiffer = (this.maxValue - this.midleValue) / (this.setGroupNumber / 2);
		for (int group = 0; group < new BigDecimal(this.setGroupNumber / 2).setScale(1, BigDecimal.ROUND_UP)
				.intValue(); group++) {
			double groupLimit = this.midleValue + group * rightDiffer;

			// add the value to groupList which is under limit
			List<Double> groupList = new ArrayList<Double>();
			while (temptList.size() > 0 && temptList.get(0) <= groupLimit) {
				groupList.add(temptList.get(0));
				temptList.remove(0);
			}
			groupMap.put(new BigDecimal(groupLimit).setScale(3, BigDecimal.ROUND_HALF_UP).toString(), groupList);
		}

	}

	public double getchiSquareValue(AtDistribution distribution) {
		// check for the map is null or not
		if (this.groupMap == null) {
			this.groupListInitialize(this.setGroupNumber);
		}

		// chiSquare function
		double chiSquare = 0;
		for (int index = 0; index < this.groupMap.size() - 1; index++) {

			// get the possibility from distribution
			List<String> limits = new ArrayList<String>(this.groupMap.keySet());
			double lowLimit = Double.parseDouble(limits.get(index));
			double uperLimit = Double.parseDouble(limits.get(index + 1));
			double possibilityDis = distribution.getProbability(lowLimit, uperLimit);

			// get the possibility from data
			int totalSize = this.valueList.size();
			List<Double> groupValues = this.groupMap.get(limits.get(index + 1)); 
			
			double possibilityData = groupValues.size() / totalSize;

			// get group chiSquare
			chiSquare = chiSquare + Math.pow(possibilityDis - possibilityData, 2) / possibilityDis;
		}

		return chiSquare;
	}

}
