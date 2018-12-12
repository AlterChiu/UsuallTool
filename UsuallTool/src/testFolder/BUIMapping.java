package testFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class BUIMapping {
	public static Map<String, String> idMapping;
	public static String[][] stationList;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method
		idMapping = getIdMapping();
		Map<String, List<String>> valueList = getRainfallValues();
		valueList = rainfallMapping(valueList);
		new AtFileWriter(buiMaker(valueList).parallelStream().toArray(String[]::new),
				"E:\\mapReduce\\RainfallData\\全台200y雨量\\屏東地區\\PD_6y200.BUI").textWriter(" ");
	}

	public static Map<String, List<String>> rainfallMapping(Map<String, List<String>> rainfallList) {
		Map<String, List<String>> outMap = new TreeMap<>();
		for (String key : idMapping.keySet()) {
			outMap.put(key, rainfallList.get(idMapping.get(key)));
		}
		return outMap;
	}

	public static Map<String, String> getIdMapping() throws IOException {
		String[][] content = new AtFileReader("E:\\mapReduce\\RainfallData\\全台200y雨量\\屏東地區\\屏東模式集水區對應ID.csv").getCsv(1,
				0);

		Map<String, String> outMap = new TreeMap<String, String>();
		for (String line[] : content) {
			outMap.put(line[0], line[1]);
		}
		return outMap;
	}

	public static Map<String, List<String>> getRainfallValues() throws IOException {
		String[][] content = new AtFileReader("E:\\mapReduce\\RainfallData\\全台200y雨量\\TO200Y12.BUI").getStr();

		Map<String, List<String>> outMap = new TreeMap<>();
		for (int index = 0; index < 305; index++) {
			String stationName = content[index + 8][0].split("\'")[1];

			List<String> valueList = new ArrayList<>();
			for (int times = 0; times < 12; times++) {
				valueList.add(content[321 + times][index]);
			}
			outMap.put(stationName, valueList);
		}
		return outMap;
	}

	public static List<String> buiMaker(Map<String, List<String>> values) {
		List<String> outBui = new ArrayList<>();
		outBui.add("*Name of this file: \\Sobek213\\FIXED\\TW002Y12.BUI");
		outBui.add("*Date and time of construction: 11-10-2015   22:13:58");
		outBui.add("*Enige algemene wenken:");
		outBui.add("*Gebruik de default dataset (1) of de volledige reeks (0) voor overige invoer");
		outBui.add(" " + values.size() + "");
		outBui.add("*Aantal stations");
		outBui.add("*Namen van stations");
		for (String key : values.keySet()) {
			outBui.add("\'" + key + "\'");
		}
		outBui.add("*Aantal gebeurtenissen (omdat het 1 bui betreft is dit altijd 1)");
		outBui.add("*en het aantal seconden per waarnemingstijdstap");
		outBui.add(" 1  3600");
		outBui.add("*Elke commentaarregel wordt begonnen met een * (asteriks).");
		outBui.add("*Eerste record bevat startdatum en -tijd, lengte van de gebeurtenis in dd hh mm ss");
		outBui.add("*Het format is: yyyymmdd:hhmmss:ddhhmmss");
		outBui.add("*Daarna voor elk station de neerslag in mm per tijdstap.");
		outBui.add(" 2000 1 1 0 0 0 0 12 0 0");

		for (int index = 0; index < 12; index++) {
			StringBuilder sb = new StringBuilder();
			for (String key : values.keySet()) {
				sb.append(values.get(key).get(index) + " ");
			}
			outBui.add(sb.toString());
		}
		return outBui;
	}
}