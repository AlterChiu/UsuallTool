package testFolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class BUITranslate {
	public static Map<String, String> idMapping;
	public static String[][] stationList;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method
		String content[][] = new AtFileReader("E:\\mapReduce\\RainfallData\\全台200y雨量\\屏東地區\\PD_6y200.BUI").getStr();
		System.out.println(content.length);
		for (int index = 0; index <3389; index++) {
			double start = Double.parseDouble(content[3404][index]);
			double top = Double.parseDouble(content[3409][index]);
			double end = Double.parseDouble(content[3415][index]);

			for (int times = 0; times < 3; times++) {
				content[3404 + times][index] = new BigDecimal((top - start) / 2 * times + start)
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			}
			for (int times = 2; times < 12; times++) {
				content[3404 + times][index] = new BigDecimal(top - (top - end) / 9 * (times - 2))
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			}
		}

		new AtFileWriter(content, "E:\\mapReduce\\RainfallData\\全台200y雨量\\屏東地區\\PD_3y200.BUI").textWriter(" ");
	}
}