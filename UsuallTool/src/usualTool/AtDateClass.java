package usualTool;

import java.text.ParseException;

public class AtDateClass {
	private int nullValue = -999;
	private int year = nullValue;
	private int month = nullValue;
	private int day = nullValue;
	private int hour = nullValue;
	private int minute = nullValue;
	private int second = nullValue;
	private long dateLong = -999;

	public AtDateClass(String dateString, String inputFormat) throws ParseException {
		this.dateLong = TimeTranslate.StringToLong(dateString, inputFormat);
		process();
	}

	public AtDateClass(long dateLong) {
		this.dateLong = dateLong;
		process();
	}

	private void process() {
		setYear();
		setMonth();
		setDay();
		setHour();
		setMinite();
		setSecond();
	}

	private void setYear() {
		this.year = Integer.parseInt(TimeTranslate.milliToTime(dateLong, "yyyy"));
	}

	private void setMonth() {
		this.month = Integer.parseInt(TimeTranslate.milliToTime(dateLong, "MM"));
	}

	private void setDay() {
		this.day = Integer.parseInt(TimeTranslate.milliToTime(dateLong, "dd"));
	}

	private void setHour() {
		this.hour = Integer.parseInt(TimeTranslate.milliToTime(dateLong, "HH"));
	}

	private void setMinite() {
		this.minute = Integer.parseInt(TimeTranslate.milliToTime(dateLong, "mm"));
	}

	private void setSecond() {
		this.second = Integer.parseInt(TimeTranslate.milliToTime(dateLong, "ss"));
	}

	public AtDateClass addHour(int times) throws ParseException {
		this.dateLong = this.dateLong + (long)times * (long)3600000;
		translate();
		return this;
	}

	public AtDateClass addMinutes(int times) throws ParseException {
		this.dateLong = this.dateLong + (long)times * (long)60000;
		translate();
		return this;
	}

	public AtDateClass addSecond(int times) throws ParseException {
		this.dateLong = this.dateLong + (long)times * (long)1000;
		translate();
		return this;
	}

	public int getYearFormat() {
		return this.year;
	}

	public int getMonthFormat() {
		return this.month;
	}

	public int getDayFormat() {
		return this.day;
	}

	public int getHourFormat() {
		return this.hour;
	}

	public int getMiniteFormat() {
		return this.minute;
	}

	public int getSecondFormat() {
		return this.second;
	}

	public double getDayPass(long dateLong) {
		return (this.dateLong - dateLong) / 1000 / 3600 / 24;
	}

	public double getDayPass() {
		return this.getDayPass(0);
	}

	public double getHourPass(long dateLong) {
		return (this.dateLong - dateLong) / 1000 / 3600;
	}

	public double getHourPass() {
		return this.getHourPass(0);
	}

	public double getMinutePass(long dateLong) {
		return (this.dateLong - dateLong) / 1000 / 60;
	}

	public double getMinutePass() {
		return this.getMinutePass(0);
	}

	public double getSecondPass(long dateLong) {
		return (this.dateLong - dateLong) / 1000;
	}

	public double getSecondPass() {
		return this.getSecondPass(0);
	}

	public long getDateLong() {
		return this.dateLong;
	}

	private void translate() throws ParseException {
		this.year = Integer.parseInt(TimeTranslate.milliToDate(this.dateLong, "yyyy"));
		this.month = Integer.parseInt(TimeTranslate.milliToDate(this.dateLong, "HH"));
		this.day = Integer.parseInt(TimeTranslate.milliToDate(this.dateLong, "dd"));
		this.hour = Integer.parseInt(TimeTranslate.milliToDate(this.dateLong, "HH"));
		this.minute = Integer.parseInt(TimeTranslate.milliToDate(this.dateLong, "mm"));
		this.second = Integer.parseInt(TimeTranslate.milliToDate(this.dateLong, "ss"));
	}

	public String getDateString(String outputFormat) {
		return TimeTranslate.milliToDate(this.dateLong, outputFormat);
	}

	public static Boolean isLunar(int year) {
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

	public static int getTotalDayInMont(int year, int month) throws Exception {
		if (month > 12 || month <= 0) {
			throw new Exception("error month");
		} else if (year <= 0) {
			throw new Exception("error year");
		} else if (month == 1) {
			return 31;
		} else if (month == 2) {
			if (isLunar(year)) {
				return 29;
			} else {
				return 28;
			}
		} else if (month == 3) {
			return 31;
		} else if (month == 4) {
			return 30;
		} else if (month == 5) {
			return 31;
		} else if (month == 6) {
			return 30;
		} else if (month == 7) {
			return 31;
		} else if (month == 8) {
			return 31;
		} else if (month == 9) {
			return 30;
		} else if (month == 10) {
			return 31;
		} else if (month == 11) {
			return 30;
		} else {
			return 31;
		}
	}

}
