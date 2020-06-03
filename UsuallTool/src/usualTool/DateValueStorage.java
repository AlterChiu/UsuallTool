package usualTool;

import java.io.IOException;
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
		stationList.remove(this.dateKey);

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
				 * 01=> 10 , 00=> 10 , 09=> 10
				 */
				String temptDate = this.fileContent.get(this.dateKey).get(timeIndex);
				temptDate = TimeTranslate.addMinute(temptDate, this.dateFormat, 10);

				String temptFrontDateFormat = this.dateFormat.split("mm")[0] + "mm";
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						temptFrontDateFormat);

				String temptBackDateFormat = "";
				try {
					temptBackDateFormat = this.dateFormat.split("mm")[1];
				} catch (Exception e) {
				}
				String temptBackDate = TimeTranslate.getTimeString(0, temptBackDateFormat);

				temptDate = temptFrontDate + temptBackDate;

				// add value
				try {
					double temptValue = Double.parseDouble(stationValues.get(timeIndex));
					if (temptValue >= 0) {
						stationStorage.get(temptDate).add(temptValue);
					}
				} catch (Exception e) {
				}

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
		stationList.remove(this.dateKey);

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
				String temptDate = this.fileContent.get(this.dateKey).get(timeIndex);
				temptDate = TimeTranslate.addHour(temptDate, this.dateFormat);

				String temptFrontDateFormat = this.dateFormat.split("HH")[0] + "HH";
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						temptFrontDateFormat);

				String temptBackDateFormat = this.dateFormat.split("HH")[1];
				String temptBackDate = TimeTranslate.getTimeString(0, temptBackDateFormat);

				temptDate = temptFrontDate + temptBackDate;

				// add value
				try {
					double temptValue = Double.parseDouble(stationValues.get(timeIndex));
					if (temptValue >= 0) {
						stationStorage.get(temptDate).add(temptValue);
					}
				} catch (Exception e) {
				}

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

	public Map<String, List<String>> toDay(StaticsModel staticsModel) throws ParseException {
		List<String> stationList = new ArrayList<>(this.fileContent.keySet());
		stationList.remove(this.dateKey);

		// Setting time title
		Map<String, List<String>> outMap = new LinkedHashMap<>();
		outMap.put(this.dateKey, getTotalTimeStorage(24 * 60).keySet().parallelStream().collect(Collectors.toList()));

		for (String stationID : stationList) {
			Map<String, List<Double>> stationStorage = getTotalTimeStorage(24 * 60);
			List<String> stationValues = this.fileContent.get(stationID);

			for (int timeIndex = 0; timeIndex < stationValues.size(); timeIndex++) {
				/*
				 * translate date to day
				 * 
				 * 01 00:00=> 02 00:00 , 01 00:01=> 02 00:00, 01 23:59=> 02 00:00
				 */
				String temptDate = this.fileContent.get(this.dateKey).get(timeIndex);
				temptDate = TimeTranslate.addHour(temptDate, this.dateFormat);

				String temptFrontDateFormat = this.dateFormat.split("dd")[0] + "dd";
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						temptFrontDateFormat);
				
				String temptBackDateFormat = this.dateFormat.split("dd")[1];
				String temptBackDate = TimeTranslate.getTimeString(0, temptBackDateFormat);

				temptDate = temptFrontDate + temptBackDate;

				// add value
				try {
					double temptValue = Double.parseDouble(stationValues.get(timeIndex));
					if (temptValue >= 0) {
						stationStorage.get(temptDate).add(temptValue);
					}
				} catch (Exception e) {
				}

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

	public Map<String, List<String>> toMonth(StaticsModel staticsModel) throws ParseException {
		List<String> stationList = new ArrayList<>(this.fileContent.keySet());
		stationList.remove(this.dateKey);

		// Setting time title
		Map<String, List<String>> outMap = new LinkedHashMap<>();
		outMap.put(this.dateKey, getTotalTimeStorage_Month().keySet().parallelStream().collect(Collectors.toList()));

		for (String stationID : stationList) {
			Map<String, List<Double>> stationStorage = getTotalTimeStorage_Month();
			List<String> stationValues = this.fileContent.get(stationID);

			for (int timeIndex = 0; timeIndex < stationValues.size(); timeIndex++) {
				/*
				 * translate date to day
				 * 
				 * 01 00:00=> 02 00:00 , 01 00:01=> 02 00:00, 01 23:59=> 02 00:00
				 */
				String temptDate = this.fileContent.get(this.dateKey).get(timeIndex);
				temptDate = TimeTranslate.addMonth(temptDate, this.dateFormat);

				String temptFrontDateFormat = this.dateFormat.split("MM")[0] + "MM";
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						temptFrontDateFormat);

				String temptBackDateFormat = this.dateFormat.split("MM")[1];
				String temptBackDate = TimeTranslate.getTimeString(0, temptBackDateFormat);

				temptDate = temptFrontDate + temptBackDate;

				// add value
				try {
					double temptValue = Double.parseDouble(stationValues.get(timeIndex));
					if (temptValue >= 0) {
						stationStorage.get(temptDate).add(temptValue);
					}
				} catch (Exception e) {
				}

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

	public Map<String, List<String>> toYear(StaticsModel staticsModel) throws ParseException {
		List<String> stationList = new ArrayList<>(this.fileContent.keySet());
		stationList.remove(this.dateKey);

		// Setting time title
		Map<String, List<String>> outMap = new LinkedHashMap<>();
		outMap.put(this.dateKey, getTotalTimeStorage_Year().keySet().parallelStream().collect(Collectors.toList()));

		for (String stationID : stationList) {
			Map<String, List<Double>> stationStorage = getTotalTimeStorage_Year();
			List<String> stationValues = this.fileContent.get(stationID);

			for (int timeIndex = 0; timeIndex < stationValues.size(); timeIndex++) {
				/*
				 * translate date to day
				 * 
				 * 1999/01/01 => 2000/01/01 , 1999/12/31=> 2000/01/01
				 */
				String temptDate = this.fileContent.get(this.dateKey).get(timeIndex);
				temptDate = TimeTranslate.addYear(temptDate, this.dateFormat);

				String temptFrontDateFormat = this.dateFormat.split("yyyy")[0] + "yyyy";
				String temptFrontDate = TimeTranslate.getDateStringTranslte(temptDate, this.dateFormat,
						temptFrontDateFormat);

				String temptBackDateFormat = this.dateFormat.split("yyyy")[1];
				String temptBackDate = TimeTranslate.getTimeString(0, temptBackDateFormat);

				temptDate = temptFrontDate + temptBackDate;

				// add value
				try {
					double temptValue = Double.parseDouble(stationValues.get(timeIndex));
					if (temptValue >= 0) {
						stationStorage.get(temptDate).add(temptValue);
					}
				} catch (Exception e) {
				}

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

	public void saveTo10Min(String saveAdd, StaticsModel staticsModel) throws IOException, ParseException {
		new AtFileWriter(dateStorageFromMapToArray(to10Min(staticsModel), this.dateKey), saveAdd).csvWriter();
	}

	public void saveToHour(String saveAdd, StaticsModel staticsModel) throws IOException, ParseException {
		new AtFileWriter(dateStorageFromMapToArray(toHour(staticsModel), this.dateKey), saveAdd).csvWriter();
	}

	public void saveToDay(String saveAdd, StaticsModel staticsModel) throws IOException, ParseException {
		new AtFileWriter(dateStorageFromMapToArray(toDay(staticsModel), this.dateKey), saveAdd).csvWriter();
	}

	public void saveToMonth(String saveAdd, StaticsModel staticsModel) throws IOException, ParseException {
		new AtFileWriter(dateStorageFromMapToArray(toMonth(staticsModel), this.dateKey), saveAdd).csvWriter();
	}

	public void saveToYear(String saveAdd, StaticsModel staticsModel) throws IOException, ParseException {
		new AtFileWriter(dateStorageFromMapToArray(toYear(staticsModel), this.dateKey), saveAdd).csvWriter();
	}
	// <====================================================================>

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++ Format Translation ++++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
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

	public Map<String, List<String>> dateStorageFromArrayToMap(String[][] content) {
		Map<String, List<String>> outMap = new LinkedHashMap<>();

		for (int column = 0; column < content[0].length; column++) {

			// set Station values
			String id = content[0][column];
			List<String> temptList = new ArrayList<>();
			System.out.println(id);
			for (int row = 1; row < content.length; row++) {
				temptList.add(content[row][column]);
			}
			outMap.put(id, temptList);
		}

		return outMap;
	}
	// <====================================================================>

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++Private Function++++++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	private Map<String, List<Double>> getTotalTimeStorage(int minuteSteps) throws ParseException {
		String temptStartDate = this.startDate;

		Map<String, List<Double>> valueStorage = new TreeMap<>();
		valueStorage.put(temptStartDate, new ArrayList<Double>());
		while (!temptStartDate.equals(this.endDate)) {
			temptStartDate = TimeTranslate.addMinute(temptStartDate, dateFormat, minuteSteps);
			valueStorage.put(temptStartDate, new ArrayList<Double>());
		}
		valueStorage.put(TimeTranslate.addMinute(endDate, dateFormat, minuteSteps), new ArrayList<Double>());
		return valueStorage;
	}

	private Map<String, List<Double>> getTotalTimeStorage_Month() throws ParseException {
		String temptStartDate = this.startDate;

		Map<String, List<Double>> valueStorage = new TreeMap<>();
		valueStorage.put(temptStartDate, new ArrayList<Double>());
		while (!temptStartDate.equals(this.endDate)) {
			temptStartDate = TimeTranslate.addMonth(temptStartDate, this.dateFormat);
			valueStorage.put(temptStartDate, new ArrayList<Double>());
		}
		valueStorage.put(TimeTranslate.addMonth(endDate, dateFormat), new ArrayList<Double>());
		return valueStorage;
	}

	private Map<String, List<Double>> getTotalTimeStorage_Year() throws ParseException {
		String temptStartDate = this.startDate;

		Map<String, List<Double>> valueStorage = new TreeMap<>();
		valueStorage.put(temptStartDate, new ArrayList<Double>());
		while (!temptStartDate.equals(this.endDate)) {
			temptStartDate = TimeTranslate.addYear(temptStartDate, this.dateFormat);
			valueStorage.put(temptStartDate, new ArrayList<Double>());
		}
		valueStorage.put(TimeTranslate.addYear(endDate, dateFormat), new ArrayList<Double>());
		return valueStorage;
	}
	// <====================================================================>
}
