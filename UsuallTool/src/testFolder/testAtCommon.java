package testFolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import asciiFunction.AsciiBasicControl;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class testAtCommon {
	public static Map<String, String> idMapping;
	public static String[][] stationList;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method

		String folderAdd = "E:\\GraduatedPaper\\Picture\\tainan\\splitMerge\\1\\";
		AsciiBasicControl delicateAscii = new AsciiBasicControl(folderAdd + "delicateDem.asc");
		AsciiBasicControl roughAscii = new AsciiBasicControl(folderAdd + "roughDem_0.asc");

		Map<String, Double> roughBoundary = roughAscii.getBoundary();
		Map<String, Double> delicateBoundary = delicateAscii.getBoundary();

		roughBoundary.put("minX", (delicateBoundary.get("minX") + roughBoundary.get("minX")) / 2);
		new AtFileWriter(roughAscii.getClipAsciiFile(roughBoundary).getAsciiFile(), folderAdd + "roughExtra.asc")
				.textWriter(" ");

	}
}