package usualTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.time.DurationFormatUtils;

public interface TimeInterface {
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
	
	

	public static String milliToDate(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
	public static String StringGetSelected(String time, String format,String selected) throws ParseException {
		Long tempLong = StringToLong(time, format);
		return milliToDate(tempLong, selected);
	}
	
	public static String milliToTime(long time, String format) {
		return DurationFormatUtils.formatDuration(time,format);
	}

	public static long StringToLong(String time, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(time).getTime();
	}

	public static String getDate(String time , String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
	
	public static String[] getFormate() {
		String[] content = { "dd-MM-yy", "MM-dd-yy", "yy-MM-dd", "yyyy-MM-dd_HH", "yyyy-MM-dd_HH:mm:ss",
				"yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd a HH:mm:ss", "yyyy-MM-dd a HH:mm:ss" };
		return content;
	}

}
