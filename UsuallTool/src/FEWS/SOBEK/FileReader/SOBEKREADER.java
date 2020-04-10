package FEWS.SOBEK.FileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import usualTool.AtFileReader;

public class SOBEKREADER {
	static public List<List<String>> getTAGConent(String fileAdd, String tag) throws IOException {
		String[][] content = new AtFileReader(fileAdd).getContent(" +");
		List<String> temptList = new ArrayList<>();
		for (String temptLine[] : content) {
			for (String temptText : temptLine) {
				temptList.add(temptText);
			}
		}
		return getTAGConent(temptList, tag);
	}

	static public List<List<String>> getTAGConent(List<String> content, String tag) {
		List<List<String>> outList = new ArrayList<>();

		for (int index = 0; index < content.size(); index++) {
			if (content.get(index).equals(tag.toUpperCase())) {
				List<String> temptList = new ArrayList<>();
				
				while (!content.get(index).equals(tag.toLowerCase())) {
					temptList.add(content.get(index));
					index++;
				}
				temptList.add(content.get(index));
				outList.add(temptList);
			}
		}
		return outList;
	}

	// read struct.dat
	static public List<String> readFile(BufferedReader br) throws IOException {
		List<String> outList = new ArrayList<>();
		String tempt;
		while ((tempt = br.readLine()) != null) {
			Arrays.asList(tempt.split(" +")).forEach(e -> outList.add(e));
		}
		br.close();
		return outList;
	}

}
