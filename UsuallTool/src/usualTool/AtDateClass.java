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

	public void addYear(int times) throws ParseException {
		this.year = this.year + times;
		translate();
	}

	public void addHour(int times) throws ParseException {
		this.hour = this.hour + times;
		translate();
	}

	public void addMonth(int times) throws ParseException {
		this.month = this.month + times;
		translate();
	}

	public void addMinutes(int times) throws ParseException {
		this.minute = this.minute + times;
		translate();
	}

	public void addSecond(int times) throws ParseException {
		this.second = this.second + times;
		translate();
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
		StringBuilder dateString = new StringBuilder();
		dateString.append(String.format("%04d", this.year));
		dateString.append(String.format("%02d", this.month));
		dateString.append(String.format("%02d", this.day));
		dateString.append(String.format("%02d", this.minute));
		dateString.append(String.format("%02d", this.second));

		this.dateLong = TimeTranslate.StringToLong(dateString.toString(), "yyyyMMddHHmmss");
	}

	public String getDateString(String outputFormat) {
		return TimeTranslate.milliToDate(this.dateLong, outputFormat);
	}

}
