package testFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import usualTool.AtFileReader;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String model = "20181201_Pintung_FEWS_12_3_plis1.5";
		String delicateAsciiAdd = "E:\\mapReduce\\OriginalDEM\\PintongZone2\\highResolutionDem.asc";
		String roughAsciiAsdd = "E:\\mapReduce\\OriginalDEM\\PintongZone2\\lowResolutionDem.asc";

		JsonObject property = new AtFileReader("E:\\mapReduce\\報告用-別刪\\" + model + "\\property.json").getJsonObject();

		AsciiBasicControl delicate = new AsciiBasicControl(delicateAsciiAdd);
		AsciiBasicControl rough = new AsciiBasicControl(roughAsciiAsdd);

		// <+++++++++++++++++++++++++++++++++++++++++++++>
		// <+++++++++++++++++++++ TOTAL +++++++++++++++++++>
		// <+++++++++++++++++++++++++++++++++++++++++++++>
		int delicateGrids = getActiveCell(delicateAsciiAdd);
		int delicateCellSize = (int) Double.parseDouble(delicate.getProperty().get("cellSize"));
		int delicateArea = delicateGrids * delicateCellSize * delicateCellSize;

		int roughGrids = getActiveCell(roughAsciiAsdd);
		int roughCellSize = (int) Double.parseDouble(rough.getProperty().get("cellSize"));
		int roughArea = roughGrids * roughCellSize * roughCellSize;

		System.out.println("delicateTotal\t" + property.get("SpendTime_delicateTotal").getAsString() + "\t"
				+ delicateCellSize + "\t" + delicateGrids + "\t" + delicateArea);
		System.out.println("roughTotal\t" + property.get("SpendTime_roughTotal").getAsString() + "\t" + roughCellSize
				+ "\t" + roughGrids + "\t" + roughArea);

		// <+++++++++++++++++++++++++++++++++++++++++++++>
		// <+++++++++++++++++++ delicateSplit +++++++++++++++++>
		// <+++++++++++++++++++++++++++++++++++++++++++++>
		for (int index = 0; index < 2; index++) {
			System.out.println();

			JsonObject temptJson = property.get("mergeSplit_" + index).getAsJsonObject();
			JsonObject temptDelicate = temptJson.get("DelicateBoundary").getAsJsonObject();
			JsonArray temptRoughArray = temptJson.get("RoughBoundary").getAsJsonArray();
			int delicateAcitiveGrids = getActiveCell(
					"E:\\mapReduce\\報告用-別刪\\" + model + "\\split\\" + index + "\\delicateDem.asc");

			System.out.println("delicate\t" + temptDelicate.get("spendTime").getAsString() + "\t\t\t" + delicateCellSize
					+ "\t" + delicateAcitiveGrids + "\t" + delicateCellSize * delicateCellSize * delicateAcitiveGrids
					+ "\t" + temptDelicate.get("minX").getAsString() + "\t" + temptDelicate.get("maxX").getAsString()
					+ "\t" + temptDelicate.get("minY").getAsString() + "\t" + temptDelicate.get("maxX").getAsString()
					+ "\t");

			for (int arrayNo = 0; arrayNo < temptRoughArray.size(); arrayNo++) {
				int roughAcitiveGrids = getActiveCell("E:\\mapReduce\\報告用-別刪\\" + model + "\\convergence\\" + index
						+ "\\" + arrayNo + "\\roughDem.asc");

				JsonObject temptRough = temptRoughArray.get(arrayNo).getAsJsonObject();
				System.out.println(" \t" + temptRough.get("spendTime").getAsString() + "\t"
						+ temptRough.get("FloodTimesError").getAsString() + "\t"
						+ temptRough.get("FloodDepthError").getAsString() + "\t" + roughCellSize + "\t"
						+ roughAcitiveGrids + "\t" + roughCellSize * roughCellSize * roughAcitiveGrids + "\t"
						+ temptRough.get("minX").getAsString() + "\t" + temptRough.get("maxX").getAsString() + "\t"
						+ temptRough.get("minY").getAsString() + "\t" + temptRough.get("maxX").getAsString());
			}
		}
	}

	private static int getActiveCell(String fileAdd) throws IOException {
		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
		String nullValue = ascii.getProperty().get("noData");
		int count = 0;

		for (String[] line : ascii.getAsciiGrid()) {
			for (String content : line) {
				if (!content.equals(nullValue)) {
					count++;
				}
			}
		}
		return count;
	}

}
