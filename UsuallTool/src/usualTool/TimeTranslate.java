package usualTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class TimeTranslate {
	public static final String DMY = "dd-MM-yy";
	public static final String MDY = "MM-dd-yy";
	public static final String YMD = "yy-MM-dd";
	public static final String YMDH = "yyyy-MM-dd_HH";
	public static final String YMD_HMS = "yyyy-MM-dd_HH:mm:ss";
	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public static final String YMDHMS3 = "yyyyMMddHHmmss";
	public static final String YMDAHMS_SLASH = "yyyy/MM/dd a HH:mm:ss";
	public static final String YMDAHMS_DASH = "yyyy-MM-dd a HH:mm:ss";
	public static final String YMDslash_HM = "yyyy/MM/dd  HH:mm";
	public static final String YMDdash_HM = "yyyy-MM-dd  HH:mm";

	static public String milliToDate(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}

	static public String StringGetSelected(String time, String format, String selected) throws ParseException {
		Long tempLong = StringToLong(time, format);
		return milliToDate(tempLong, selected);
	}

	static public String milliToTime(long time, String format) {
		return DurationFormatUtils.formatDuration(time, format);
	}

	static public long StringToLong(String time, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(time).getTime();
	}

	static public String addMinute(String time, String format) throws ParseException {
		long milliTime = StringToLong(time, format) + 60000;
		return milliToDate(milliTime, format);
	}

	static public String addMinute(String time, String format, int delay) throws ParseException {
		long milliTime = StringToLong(time, format) + 60000 * delay;
		return milliToDate(milliTime, format);
	}

	static public String addDay(String time, String format) throws ParseException {
		long milliTime = StringToLong(time, format) + 3600000 * 24;
		return milliToDate(milliTime, format);
	}

	static public String addDay(String time, String format, int delay) throws ParseException {
		long milliTime = StringToLong(time, format) + 3600000 * 24 * delay;
		return milliToDate(milliTime, format);
	}

	static public String addHour(String time, String format, int delay) throws ParseException {
		long milliTime = StringToLong(time, format) + 3600000 * delay;
		return milliToDate(milliTime, format);
	}

	static public String addHour(String time, String format) throws ParseException {
		long milliTime = StringToLong(time, format) + 3600000;
		return milliToDate(milliTime, format);
	}

	static public String addMonth(String time, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		long milliTime = StringToLong(time, format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliTime);
		calendar.add(Calendar.MONTH, 1);
		return dateFormat.format(calendar.getTime());
	}

	static public String addMonth(String time, String format, int month) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		long milliTime = StringToLong(time, format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliTime);
		calendar.add(Calendar.MONTH, month);
		return dateFormat.format(calendar.getTime());
	}

	static public String addYear(String time, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		long milliTime = StringToLong(time, format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliTime);
		calendar.add(Calendar.YEAR, 1);
		return dateFormat.format(calendar.getTime());
	}

	static public String addYear(String time, String format, int year) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		long milliTime = StringToLong(time, format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliTime);
		calendar.add(Calendar.YEAR, year);
		return dateFormat.format(calendar.getTime());
	}

	static public String getDate(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
}
