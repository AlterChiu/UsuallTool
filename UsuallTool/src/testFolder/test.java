package testFolder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import asciiFunction.AsciiBasicControl;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class test {

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		String[][] content = new AtFileReader("C:\\Users\\alter\\Downloads\\C1300H014.csv").getCsv();
		Map<String, List<Double>> dayMap = new TreeMap<String, List<Double>>();
		Map<String, Double> dayMean = new TreeMap<>();

		/*
		 * original data
		 */
		for (String[] temptLine : content) {
			String date = temptLine[0];

			List<Double> temptList = dayMap.get(date);
			if (temptList == null) {
				temptList = new ArrayList<Double>();
			}
			temptList.add(Double.parseDouble(temptLine[2]));
			dayMap.put(date, temptList);
		}

		/*
		 * day mean
		 */
		List<String[]> dayOut = new ArrayList<>();
		dayMap.keySet().forEach(day -> {
			dayMean.put(day, new AtCommonMath(dayMap.get(day)).getMean());
			dayOut.add(new String[] { day, new AtCommonMath(dayMap.get(day)).getMean() + "" });
		});
		new AtFileWriter(dayOut.parallelStream().toArray(String[][]::new),
				"C:\\Users\\alter\\Downloads\\C1300H014_day.csv").csvWriter();

		List<String[]> outList = new ArrayList<>();
		outList.add(new String[] { "month", "mean", "first", "second", "third" });

		for (int month = 1; month <= 12; month++) {
			List<Double> temptMonth = new ArrayList<>();
			List<Double> temptMonth1 = new ArrayList<>();
			List<Double> temptMonth2 = new ArrayList<>();
			List<Double> temptMonth3 = new ArrayList<>();

			for (String date : dayMean.keySet()) {
				if (Integer.parseInt(TimeTranslate.StringGetSelected(date, "yyyy/MM/dd", "MM")) == month) {
					temptMonth.add(dayMean.get(date));

					if (Integer.parseInt(TimeTranslate.StringGetSelected(date, "yyyy/MM/dd", "dd")) <= 10) {
						temptMonth1.add(dayMean.get(date));
					} else if (Integer.parseInt(TimeTranslate.StringGetSelected(date, "yyyy/MM/dd", "dd")) <= 20) {
						temptMonth2.add(dayMean.get(date));
					} else {
						temptMonth3.add(dayMean.get(date));
					}
				}
			}
			outList.add(new String[] { month + "", new AtCommonMath(temptMonth).getMean() + "",
					new AtCommonMath(temptMonth1).getMean() + "", new AtCommonMath(temptMonth2).getMean() + "",
					new AtCommonMath(temptMonth3).getMean() + "" });
		}

		new AtFileWriter(outList.parallelStream().toArray(String[][]::new),
				"C:\\Users\\alter\\Downloads\\C1300H014_out.csv").csvWriter();
	}

}
