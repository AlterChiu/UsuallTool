package usualTool;

import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import usualTool.AtCommonMath.StaticsModel;

public class DateValueStorage {

	/*
	 * this class is to reStorage data to difference dateFormat
	 * 
	 * from minute to 10 minutes or hours
	 * 
	 * file format should be
	 * 
	 * date, station1 , station2....
	 * 
	 */

	private String dateFormat = "yyyy/MM/dd HH:mm";
	private String startDate = "2000/01/01 00:00";
	private String endDate = "2020/12/31 00:00";
	private String dateKey = "date";
	private Map<String, List<String>> fileContent = new LinkedHashMap<>();

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++Constructor++++++++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	public DateValueStorage(String[][] fileContent, String dateFormat) throws ParseException {

		this.fileContent = dateStorageFromArrayToMap(fileContent);
		this.dateKey = fileContent[0][0];

		if (!this.dateFormat.equals(dateFormat)) {
			this.startDate = TimeTranslate.getDateStringTranslte(this.startDate, this.dateFormat, dateFormat);
			this.endDate = TimeTranslate.getDateStringTranslte(this.endDate, this.dateFormat, dateFormat);
			this.dateFormat = dateFormat;
		}
	}

	public DateValueStorage(Map<String, List<String>> fileContent, String dateTimeKey, String dateFormat)
			throws ParseException {

		this.fileContent = fileContent;
		this.dateKey = dateTimeKey;

		if (!this.dateFormat.equals(dateFormat)) {
			this.startDate = TimeTranslate.getDateStringTranslte(this.startDate, this.dateFormat, dateFormat);
			this.endDate = TimeTranslate.getDateStringTranslte(this.endDate, this.dateFormat, dateFormat);
			this.dateFormat = dateFormat;
		}
	}
	// <====================================================================>

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++Modified Function+++++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>

	public void setStartDate(String date, String dateFormat) throws ParseException {
		this.startDate = TimeTranslate.getDateStringTranslte(date, dateFormat, this.dateFormat);
	}

	public void setEndDate(String date, String dateFormat) throws ParseException {
		this.endDate = TimeTranslate.getDateStringTranslte(date, dateFormat, this.dateFormat);
	}

	public void setDateFormat(String dateFormat) {
		try {
			this.fileContent.put(dateFormat, this.fileContent.get(this.dateFormat));
		} catch (Exception e) {
		}
		this.dateFormat = dateFormat;
	}

	public Map<String, List<String>> to10Min(StaticsModel staticsModel) throws ParseException {
		List<String> stationList = new ArrayList<>(this.fileContent.keySet());

		// Setting time title
		Map<String, List<String>> outMap = new LinkedHashMap<>();
		outMap.put(this.dateKey, getTotalTimeStorage(10).keySet().parallelStream().collect(Collectors.toList()));

		for (String stationID : stationList) {
			Map<String, List<Double>> stationStorage = getTotalTimeStorage(10);
			List<String> stationValues = this.fileContent.get(stationID);

			for (int timeIndex = 0; timeIndex < stationValues.size(); timeIndex++) {
				/*
				 * translate date to 10min
				 * 
				 * 02 =>10 , 16 =>20 , 13 =>20 , 00 =>00
				 */
				String temptDate = stationValues.get(timeIndex);
				String minite = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat, "mm");
				minite = AtCommonMath.getDecimal_String(Double.parseDouble(minite) / 10., 0, RoundingMode.UP) + "0";

				String frontDateFormat = this.dateFormat.split("mm")[0];
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						frontDateFormat);

				String backDateFormat = this.dateFormat.split("mm")[1];
				String temptBackDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat, backDateFormat);

				temptDate = temptFrontDate + minite + temptBackDate;

				// add value
				stationStorage.get(temptDate).add(Double.parseDouble(stationValues.get(timeIndex)));
			}

			// get statics to values
			// get statistic
			List<String> temptOutList = new ArrayList<>();
			stationStorage.keySet().forEach(key -> {
				try {
					temptOutList
							.add(String.valueOf(AtCommonMath.getListStatistic(stationStorage.get(key), staticsModel)));
				} catch (Exception e) {
					temptOutList.add("0.00");
				}
			});

			// save to station Storage
			outMap.put(stationID, temptOutList);
		}

		return outMap;
	}

	public Map<String, List<String>> toHour(StaticsModel staticsModel) throws ParseException {
		List<String> stationList = new ArrayList<>(this.fileContent.keySet());

		// Setting time title
		Map<String, List<String>> outMap = new LinkedHashMap<>();
		outMap.put(this.dateKey, getTotalTimeStorage(60).keySet().parallelStream().collect(Collectors.toList()));

		for (String stationID : stationList) {
			Map<String, List<Double>> stationStorage = getTotalTimeStorage(60);
			List<String> stationValues = this.fileContent.get(stationID);

			for (int timeIndex = 0; timeIndex < stationValues.size(); timeIndex++) {
				/*
				 * translate date to Hour
				 * 
				 * 01:59=> 02:00 , 01:00=> 02:00 , 02:05=> 03:00
				 */
				String temptDate = stationValues.get(timeIndex);
				temptDate = TimeTranslate.addHour(temptDate, this.dateFormat);

				String temptFrontDateFormat = this.dateFormat.split("HH")[0] + "HH";
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						temptFrontDateFormat);

				String temptBackDateFormat = this.dateFormat.split("HH")[1];
				String temptBackDate = TimeTranslate.getTimeString(0, temptBackDateFormat);

				temptDate = temptFrontDate + temptBackDate;

				// add value
				stationStorage.get(temptDate).add(Double.parseDouble(stationValues.get(timeIndex)));
			}

			// get statics to values
			// get statistic
			List<String> temptOutList = new ArrayList<>();
			stationStorage.keySet().forEach(key -> {
				try {
					temptOutList
							.add(String.valueOf(AtCommonMath.getListStatistic(stationStorage.get(key), staticsModel)));
				} catch (Exception e) {
					temptOutList.add("0.00");
				}
			});

			// save to station Storage
			outMap.put(stationID, temptOutList);
		}

		return outMap;
	}

	// <====================================================================>

	public String[][] dateStorageFromMapToArray(Map<String, List<String>> mapContent, String dateKey) {
		int timeSize = mapContent.get(this.dateKey).size();
		List<String> stationIdList = new ArrayList<>(mapContent.keySet());

		List<String[]> outList = new ArrayList<>();
		outList.add(stationIdList.parallelStream().toArray(String[]::new)); // set title

		for (int timeIndex = 0; timeIndex < timeSize; timeIndex++) { // set each timeSteps

			List<String> temptList = new ArrayList<>();
			for (String stationID : stationIdList) {
				temptList.add(mapContent.get(stationID).get(timeIndex));
			}
			outList.add(temptList.parallelStream().toArray(String[]::new));
		}

		return outList.parallelStream().toArray(String[][]::new); // output
	}

	private Map<String, List<String>> dateStorageFromArrayToMap(String[][] content) {
		Map<String, List<String>> outMap = new LinkedHashMap<>();

		for (int column = 0; column < content[0].length; column++) {

			// set Station values
			String id = content[0][column];
			List<String> temptList = new ArrayList<>();
			for (int row = 1; row < content.length; row++) {
				temptList.add(content[row][column]);
			}
			outMap.put(id, temptList);
		}

		return outMap;
	}

	private Map<String, List<Double>> getTotalTimeStorage(int minuteSteps) throws ParseException {
		Map<String, List<Double>> valueStorage = new TreeMap<>();

		valueStorage.put(startDate, new ArrayList<Double>());
		while (!startDate.equals(endDate)) {
			startDate = TimeTranslate.addMinute(startDate, dateFormat, minuteSteps);
			valueStorage.put(startDate, new ArrayList<Double>());
		}
		return valueStorage;
	}

}
