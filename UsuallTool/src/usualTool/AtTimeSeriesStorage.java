package usualTool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class AtTimeSeriesStorage {

	public Boolean isLunar(int year) {
		if (year % 4000 == 0) {
			return false;
		} else if (year % 400 == 0) {
			return true;
		} else if (year % 100 == 0) {
			return false;
		} else if (year % 4 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public class yearTimeSeries {
		private Map<Integer, MonthTimeSeries> monthMap = new TreeMap<>();
		protected Boolean isLunar = false;

		public void initial(int year) {
			monthMap.clear();

		}

	}

	public class MonthTimeSeries {
		private Map<Integer, DayTimeSeries> dayMap = new TreeMap<>();

	}

	public class DayTimeSeries {
		private Map<Integer, HourTimeSeries> hourMap = new TreeMap<>();
		private Boolean isMissing = true;

		public DayTimeSeries() {
			
		}

	}

	public class HourTimeSeries {
		private Map<Integer, MinutesTimeSeires> minutesMap = new TreeMap<>();
		private Boolean isMissing = true;

		public HourTimeSeries() {
			for (int index = 1; index <= 60; index++) {
				minutesMap.put(index, new MinutesTimeSeires());
			}
		}

		public void addValue(double value, int minute) {
			if (minute < 0 || minute >= 60) {
				new Exception("*WARN* not allowable minutes format");
			} else {
				isMissing = false;
				minutesMap.get(minute).addValue(value);
			}
		}

		public Boolean isMissing() {
			return this.isMissing;
		}

		public List<Double> getHourValues() {
			List<Double> temptValues = new ArrayList<>();
			this.minutesMap.keySet().forEach(minute -> {
				if (!this.minutesMap.get(minute).isMissing) {
					this.minutesMap.get(minute).getMinuteValues().forEach(minuteValue -> {
						temptValues.add(minuteValue);
					});
				}
			});
			return temptValues;
		}

		public Map<Integer, MinutesTimeSeires> getMinutesValues() {
			return this.minutesMap;
		}

	}

	public class MinutesTimeSeires {
		private List<Double> valueList = new ArrayList<>();
		private Boolean isMissing = true;

		public void addValue(double value) {
			this.valueList.add(value);
			this.isMissing = false;
		}

		public List<Double> getMinuteValues() {
			return this.valueList;
		}

		public Boolean isMissing() {
			return this.isMissing;
		}

		public double getSum() {
			return new AtCommonMath(this.valueList).getSum();
		}

		public double getMax() {
			return new AtCommonMath(this.valueList).getMax();
		}

		public double getMin() {
			return new AtCommonMath(this.valueList).getMin();
		}

	}

}
