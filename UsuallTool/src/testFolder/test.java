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
		String fileAdd = "H:\\RainfallData\\createRainfall\\掃描式事件篩選方法\\Hour\\1\\PT3_200.asc";
		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);

		int row = Integer.parseInt(ascii.getProperty().get("row"));
		int column = Integer.parseInt(ascii.getProperty().get("column"));

		for (int temptColumn = 0; temptColumn < column; temptColumn++) {
			for (int temptRow = 0; temptRow < row; temptRow++) {
				String temptCalue = ascii.getValue(temptColumn, temptRow);
				if (Double.parseDouble(temptCalue) < 0 && !temptCalue.equals(ascii.getProperty().get("noData"))) {
					double[] temptCoordinate = ascii.getCoordinate(temptColumn, temptRow);
					System.out.println(temptCoordinate[0] + "_" + temptCoordinate[1]);
				}
			}
		}

	}

}
