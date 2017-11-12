package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;

public class AsciiBasicControl {
	private String[][] asciiContent;
	private String fileAdd;

	public AsciiBasicControl(String[][] asciiContent) {
		this.asciiContent = asciiContent;
	}

	public AsciiBasicControl(String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.asciiContent = new AtFileReader(fileAdd).getStr();
	}

	public AsciiBasicControl cutFirstColumn() throws IOException {
		ArrayList<String[]> temptArray = new ArrayList<String[]>();
		String[] temptContent = new AtFileReader(this.fileAdd).getContain();
		for (int line = 0; line < 6; line++) {
			temptArray.add(temptContent[line].split(" +"));
		}
		for (int line = 6; line < temptContent.length; line++) {
			temptArray.add(temptContent[line].trim().split(" +"));
		}
		this.asciiContent = temptArray.parallelStream().toArray(String[][]::new);
		return this;
	}

	public TreeMap<String, String> getProperty() {
		TreeMap<String, String> temptTree = new TreeMap<String, String>();

		temptTree.put("column", this.asciiContent[0][1]);
		temptTree.put("row", this.asciiContent[1][1]);
		temptTree.put("bottomX", this.asciiContent[2][1]);
		temptTree.put("bottomY", this.asciiContent[3][1]);
		temptTree.put("cellSize", this.asciiContent[4][1]);
		temptTree.put("noData", this.asciiContent[5][1]);
		double cellSize = Double
				.parseDouble(new BigDecimal(this.asciiContent[4][1]).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());

		temptTree.put("topX",
				new BigDecimal(Double
						.parseDouble(this.asciiContent[2][1]) + cellSize * (this.asciiContent[6].length-1))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());
		
		temptTree.put("topY",
				new BigDecimal(Double
						.parseDouble(this.asciiContent[3][1]) + cellSize * (this.asciiContent.length-6-1))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());
		
		
		return temptTree;
	}

	public String[][] getAsciiFile() {
		return this.asciiContent;
	}

	public AsciiBasicControl changeNoDataValue(String nan) {
		String noData = this.asciiContent[5][1];
		for (int line = 0; line < this.asciiContent.length; line++) {
			for (int column = 0; column < this.asciiContent[line].length; column++) {
				if (this.asciiContent[line][column].equals(noData)) {
					this.asciiContent[line][column] = nan;
				}
			}
		}
		return this;
	}

//	public AsciiBasicControl changeCellSize(int size) {
//		// make the column ,row and size to multiply the size
//		this.asciiContent[0][1] = (int) (Integer.parseInt(this.asciiContent[0][1]) * size) + "";
//		this.asciiContent[1][1] = (int) (Integer.parseInt(this.asciiContent[1][1]) * size) + "";
//		this.asciiContent[4][1] = (int) (Integer.parseInt(this.asciiContent[4][1]) * size) + "";
//
//		for (int row = 6; row < this.asciiContent.length; row++) {
//
//		}
//
//	}

//	private String getAvergeValue(int row, int column) {
//		ArrayList<Double> temptArray = new ArrayList<Double>();
//		try {
//
//			temptArray.add(Double.parseDouble(this.asciiContent[row][column]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row][column + 1]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row][column - 1]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row + 1][column]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row + 1][column + 1]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row + 1][column - 1]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row - 1][column]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row - 1][column + 1]));
//		} catch (Exception e) {
//		}
//		try {
//			temptArray.add(Double.parseDouble(this.asciiContent[row - 1][column - 1]));
//		} catch (Exception e) {
//		}
//
//		return new AtCommonMath(temptArray.stream().mapToDouble(Double::doubleValue).toArray()).getMean() + "";
//	}

}
