package testFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import Netcdf.NetCDFReader;
import ucar.ma2.InvalidRangeException;
import usualTool.AtDeterminant;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.FileFunction;
import usualTool.TimeTranslate;

public class testAtCommon {

	private static TreeMap<String, String> timesTree = new TreeMap<String, String>();
	private static AtDeterminant deter = new AtDeterminant();
	private static TimeTranslate tt = new TimeTranslate();
	private static FileFunction ff = new FileFunction();

	public static void main(String[] args) throws IOException, InvalidRangeException {
		// TODO Auto-generated method

		String fileAdd = "S:\\HomeWork\\EMIC_2018\\";
		for (String fileName : new File(fileAdd).list()) {
			if (fileName.contains(".csv")) {
				String content[][] = new AtFileReader(fileAdd + fileName).getCsv();

				List<String[]> outList = new ArrayList<String[]>();

				for (String line[] : content) {
					if (!line[1].startsWith("2")) {
						outList.add(line);
					}
				}
				new AtFileWriter(outList.parallelStream().toArray(String[][]::new), fileAdd + fileName).csvWriter();
			}
		}

	}

	// <====================================================================>

	// private static void getMaxD0() {
	// String fileAdd =
	// "S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Tainan\\Result\\0618\\";
	// String[] folderList = new File(fileAdd).list();
	//
	// for (String folder : folderList) {
	// List<AsciiBasicControl> asciiList = new ArrayList<AsciiBasicControl>();
	// for (int index = 1; index <= 49; index++) {
	// asciiList.add(
	// new AsciiBasicControl(fileAdd + folder + "\\dm1d" + String.format("%04d",
	// index) + ".asc"));
	// }
	//
	// AsciiBasicControl ascii = new AsciiBasicControl(
	// fileAdd + folder + "\\dm1d" + String.format("%04d", 0) + ".asc");
	// String[][] content = ascii.getAsciiGrid();
	//
	// for (int row = 0; row < content.length; row++) {
	// for (int column = 0; column < content[0].length; column++) {
	//
	// List<Double> valueList = new ArrayList<Double>();
	// for (AsciiBasicControl temptAscii : asciiList) {
	// valueList.add(Double.parseDouble(temptAscii.getValue(column, row)));
	// }
	// ascii.setValue(column, row, new AtCommonMath(valueList).getMax() + "");
	// }
	// }
	//
	// new AtFileWriter(ascii.getAsciiFile(), fileAdd + folder +
	// "\\dm1dmaxd0.asc").textWriter(" ");
	// }
	//
	// }

	// <=========================================================================================>

	// private static void getBoundary() throws IOException {
	// List<String> boundaryValues = new ArrayList<String>(Arrays
	// .asList(new
	// AtFileReader("S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Kao\\Boundary.DAT").getContain()));
	// String moduleFolder =
	// "S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Kao\\module\\";
	//
	// for (int zone = 1; zone <= 4; zone++) {
	// String litFile = moduleFolder + "Zone" + zone + "\\KAO40Z0" + zone +
	// ".lit\\";
	// String caseList[] = new File(litFile).list();
	//
	// for (String caseFile : caseList) {
	// try {
	// Double.parseDouble(caseFile);
	// List<String> caseBoundary = new ArrayList<String>(Arrays
	// .asList(new AtFileReader(litFile + caseFile +
	// "\\BOUNDARY.DAT").getContainWithOut("<")));
	//
	// for (int index = 0; index < caseBoundary.size(); index++) {
	// if (caseBoundary.get(index).contains("TBLE")) {
	// for (int temptIndex = boundaryValues.size() - 1; temptIndex >44= 0;
	// temptIndex--) {
	// caseBoundary.add(index + 1, boundaryValues.get(temptIndex));
	// }
	// }
	// }
	// new AtFileWriter(caseBoundary.parallelStream().toArray(String[]::new),
	// "S:\\Users\\alter\\Desktop\\EMIC_SobekDAT\\Kao\\Boundary\\Z" + zone +
	// "_BOUNDARY.DAT")
	// .textWriter("");
	// ;
	// } catch (Exception e) {
	// }
	// }
	// }
	// }
}