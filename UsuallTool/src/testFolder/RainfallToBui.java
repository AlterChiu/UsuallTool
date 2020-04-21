package testFolder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class RainfallToBui {

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		String idMappingFile = "E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\精進模式測試\\雲林\\精進模式\\BUI\\Sobek_idMapping.csv";
		String rainfallSummaryTWpolygon = "E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\精進模式測試\\雲林\\200年回歸雨量\\200yRainfall.csv";
		String saveAdd = "E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\精進模式測試\\雲林\\精進模式\\BUI\\R012Y200_WRA.BUI";
		int delayHour = 12;

		Map<String, String> idMapping = getIdMapping(idMappingFile);
		Map<String, Double> polygonSummaryRainfall = getRainfallSummaryMap(rainfallSummaryTWpolygon);
		Map<String, List<String>> polygonRaifallList = new TreeMap<>();

		idMapping.keySet().forEach(key -> {
			polygonRaifallList.put(key, getRainfallSplit(delayHour, polygonSummaryRainfall.get(idMapping.get(key))));
		});

		toBui(polygonRaifallList, saveAdd);
	}

	public static Map<String, String> getIdMapping(String fileAdd) throws IOException {
		Map<String, String> outMap = new TreeMap<>();
		String[][] content = new AtFileReader(fileAdd).getCsv(1, 0);

		for (String temptLine[] : content) {
			outMap.put(temptLine[0], temptLine[1]);
		}
		return outMap;
	}

	public static Map<String, Double> getRainfallSummaryMap(String fileAdd) throws IOException {
		Map<String, Double> outMap = new TreeMap<>();
		String[][] content = new AtFileReader(fileAdd).getCsv(1, 0);

		for (String temptLine[] : content) {
			outMap.put(temptLine[0], Double.parseDouble(temptLine[1]));
		}
		return outMap;

	}

	public static List<String> getRainfallSplit(int hour, Double summary) {
		List<String> outList = new ArrayList<>();
		double dis = 4 * summary / hour / (1 + hour / 2.0);

		for (int index = 1; index <= hour / 2; index++) {
			outList.add(AtCommonMath.getDecimal_String(index * dis, 1));
		}

		for (int index = hour / 2; index >= 1; index--) {
			outList.add(AtCommonMath.getDecimal_String(index * dis,1));
		}

		return outList;
	}

	public static void toBui(Map<String, List<String>> polygonRaifallList, String saveAdd)
			throws ParseException, IOException {
		String startTime = " 2008 01 01 00 00 00";
		String temptKey = new ArrayList<>(polygonRaifallList.keySet()).get(0);
		int delayHour = polygonRaifallList.get(temptKey).size();
		String endTime =TimeTranslate.getTimeString(delayHour*3600000, " dd HH mm ss");

		List<String> outArray = new ArrayList<>();
		outArray.add("*");
		outArray.add("*");
		outArray.add("*Enige algemene wenken:");
		outArray.add("*Gebruik de default dataset (1) of de volledige reeks (0) voor overige invoer");
		outArray.add("1");
		outArray.add("*Aantal stations");
		outArray.add(" "+polygonRaifallList.size());
		outArray.add("*Namen van stations");

		polygonRaifallList.keySet().forEach(key -> outArray.add("\'" + key + "\'"));
		outArray.add("*Aantal gebeurtenissen (omdat het 1 bui betreft is dit altijd 1)");
		outArray.add("*en het aantal seconden per waarnemingstijdstap");
		outArray.add(" 1 3600");

		outArray.add("*Elke commentaarregel wordt begonnen met een * (asteriks).");
		outArray.add("*Eerste record bevat startdatum en -tijd, lengte van de gebeurtenis in dd hh mm ss");
		outArray.add("*Het format is: yyyymmdd:hhmmss:ddhhmmss");
		outArray.add("*Daarna voor elk station de neerslag in mm per tijdstap.");
		outArray.add(startTime + endTime);

		for (int index = 0; index < delayHour; index++) {
			List<String> timeValues = new ArrayList<>();

			for (String key : polygonRaifallList.keySet()) {
				timeValues.add(polygonRaifallList.get(key).get(index));
			}
			outArray.add(String.join(" ", timeValues));
		}

		new AtFileWriter(outArray.parallelStream().toArray(String[]::new), saveAdd).textWriter("");
	}

}
