package testFolder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.gdal.gdal.gdal;

import asciiFunction.AsciiBasicControl;
import usualTool.AtCommonMath;
import usualTool.AtFileWriter;

public class test {

	public static void main(String[] args)
			throws IOException, InterruptedException, ParseException, OperationNotSupportedException {
		// TODO Auto-generated method stud
		String fileAdd = "E:\\LittleProject\\IotSensorComparision\\20190702 - 桃園\\floodResult\\";

		List<AsciiBasicControl> asciiList = new ArrayList<>();
		for (int index = 0; index <= 13; index++) {
			asciiList.add(new AsciiBasicControl(fileAdd + "dm1d" + String.format("%04d", index) + ".asc"));
		}

		AsciiBasicControl maxAscii = asciiList.get(0);
		for (int row = 0; row < Integer.parseInt(maxAscii.getProperty().get("row")); row++) {
			for (int column = 0; column < Integer.parseInt(maxAscii.getProperty().get("column")); column++) {

				String temptValue = maxAscii.getValue(column, row);
				if (!temptValue.equals(maxAscii.getNullValue())) {

					List<Double> temptList = new ArrayList<>();
					for (AsciiBasicControl ascii : asciiList) {
						temptList.add(Double.parseDouble(ascii.getValue(column, row)));
					}
					maxAscii.setValue(column, row, new AtCommonMath(temptList).getMax(3) + "");
				}
			}
		}

		new AtFileWriter(maxAscii.getAsciiFile(), fileAdd + "maxd0.asc").textWriter(" ");
	}

}
