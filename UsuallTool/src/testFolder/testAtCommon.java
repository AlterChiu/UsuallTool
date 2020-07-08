package testFolder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Microsoft.Office.PowerPoint.PPTBasicControl;
import Microsoft.Office.PowerPoint.PPTBasicControl.PPTTextBox;
import https.Rest.geo.wms.WMSBasicControl;
import usualTool.AtCommonMath.StaticsModel;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.AtTimeSeriesDataStorage;
import usualTool.DateValueStorage;

public class testAtCommon {
	public static void main(String[] args) throws Exception {
		String fileAdd = "C:\\Users\\alter\\Downloads\\新竹水利會-水資源聯合運用模組基礎資料 - 去除最大值 - 時間.txt";
		String saceAdd = "C:\\Users\\alter\\Downloads\\新竹水利會-水資源聯合運用模組基礎資料 - 平均到天.txt";

		DateValueStorage dataStorage = new DateValueStorage(new AtFileReader(fileAdd).getContent("\t"),
				"yyyy/MM/dd HH:mm");

		dataStorage.setStartDate("2015/01/01", "yyyy/MM/dd");
		dataStorage.setEndDate("2020/04/30", "yyyy/MM/dd");
		dataStorage.saveToDay(saceAdd, StaticsModel.getMean);
	}

	public static void fromNegetiveToUnrealable() throws IOException {
		double[] maxValues = new double[] { 28.35, 249.03, 35.3, 101.37, 53.42, 1.045, 2.6986, 1.463, 2.777, 0.71219,
				1.203, 2.29, 0.176, 2.94, 0.455, 1.17172, 0.176, 1.936036, 0.117, 1.28416 };

		String fileAdd = "C:\\Users\\alter\\Downloads\\新竹水利會-水資源聯合運用模組基礎資料 - 去除負值.txt";
		String saveAdd = "C:\\Users\\alter\\Downloads\\新竹水利會-水資源聯合運用模組基礎資料 - 去除最大值.txt";

		List<String[]> outList = new ArrayList<>();

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileAdd)));
		String tempt;
		while ((tempt = br.readLine()) != null) {
			List<String> temptList = new ArrayList<>();
			String temptContent[] = tempt.split("\t");

			for (int index = 0; index < maxValues.length; index++) {
				try {
					double temptValue = Double.parseDouble(temptContent[index]);
					if (temptValue > maxValues[index]) {
						temptList.add("");
					} else {
						temptList.add(temptContent[index]);
					}
				} catch (Exception e) {
					temptList.add("");
				}
			}

			outList.add(temptList.parallelStream().toArray(String[]::new));
		}
		br.close();

		new AtFileWriter(outList.parallelStream().toArray(String[][]::new), saveAdd).textWriter("\t");

	}
}
