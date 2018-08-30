package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import usualTool.AtCommonMath;

public class AsciiGridChange {

	private String[][] asciiGrid;
	private String[][] asciiContent;
	private TreeMap<String, String> originalProperty;

	public AsciiGridChange(String asciiFile) throws IOException {
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiFile);
		this.asciiContent = temptAscii.getAsciiFile();
		this.asciiGrid = temptAscii.getAsciiGrid();
		this.originalProperty = temptAscii.getProperty();
	}

	public AsciiGridChange(String[][] asciiContent) throws IOException {
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiContent);
		this.asciiContent = temptAscii.getAsciiFile();
		this.asciiGrid = temptAscii.getAsciiGrid();
		this.originalProperty = temptAscii.getProperty();
	}
	
	public AsciiGridChange(AsciiBasicControl temptAscii) {
		this.asciiContent = temptAscii.getAsciiFile();
		this.asciiGrid = temptAscii.getAsciiGrid();
		this.originalProperty = temptAscii.getProperty();
	}

	// <======================>
	// <get the target ascii file property >
	// <======================>
	public TreeMap<String, String> getChangedProperty(int gridSize) {
		if (gridSize >= 2) {
			TreeMap<String, String> temptTreeMap = new TreeMap<String, String>();

			String targetBottomX = new BigDecimal(Double.parseDouble(this.originalProperty.get("bottomX"))
					+ (gridSize - 1) * 0.5 * Double.parseDouble(this.originalProperty.get("cellSize")))
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();

			String targetTopY = new BigDecimal(Double.parseDouble(this.originalProperty.get("topY"))
					- (gridSize - 1) * 0.5 * Double.parseDouble(this.originalProperty.get("cellSize")))
							.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();

			int targetColumn = Integer.parseInt(this.originalProperty.get("column")) / gridSize;
			int targetRow = Integer.parseInt(this.originalProperty.get("row")) / gridSize;
			double targetCellSize = new BigDecimal(Double.parseDouble(this.originalProperty.get("cellSize")) * gridSize)
					.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();

			String targetBottomY = new BigDecimal(Double.parseDouble(targetTopY) - (targetRow - 1) * targetCellSize)
					.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();
			String targetTopX = new BigDecimal(Double.parseDouble(targetBottomX) + (targetColumn - 1) * targetCellSize)
					.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();

			temptTreeMap.put("column", targetColumn + "");
			temptTreeMap.put("row", targetRow + "");
			temptTreeMap.put("cellSize",
					new BigDecimal(targetCellSize).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());
			temptTreeMap.put("bottomX", targetBottomX);
			temptTreeMap.put("bottomY", targetBottomY);
			temptTreeMap.put("topX", targetTopX);
			temptTreeMap.put("topY", targetTopY);
			temptTreeMap.put("noData", this.originalProperty.get("noData"));

			return temptTreeMap;
		} else {
			return this.originalProperty;
		}

	}

	// <=========================>
	// <get the target property by text array >
	// <=========================>
	public ArrayList<String[]> getTargetPropertyContent(int gridSize) {
		if (gridSize >= 2) {
			TreeMap<String, String> temptProperty = getChangedProperty(gridSize);
			ArrayList<String[]> temptArray = new ArrayList<String[]>();

			temptArray.add(new String[] { "ncols", temptProperty.get("column") });
			temptArray.add(new String[] { "nrows", temptProperty.get("row") });
			temptArray.add(new String[] { "xllcenter", temptProperty.get("bottomX") });
			temptArray.add(new String[] { "yllcenter", temptProperty.get("bottomY") });
			temptArray.add(new String[] { "cellsize", temptProperty.get("cellSize") });
			temptArray.add(new String[] { "NODATA_value", temptProperty.get("noData") });

			return temptArray;
		} else {
			return null;
		}
	}

	// <======================>
	// <get the target ascii file property >
	// <======================>
	public String[][] getChangedGrid(int gridSize) {
		if (gridSize >= 2) {
			int targetColumn = Integer.parseInt(this.originalProperty.get("column")) / gridSize;
			int targetRow = Integer.parseInt(this.originalProperty.get("row")) / gridSize;
			ArrayList<String[]> targetGrid = new ArrayList<String[]>();

			// the original ascii grid
			for (int row = 0; row < targetRow * gridSize; row = row + gridSize) {
				ArrayList<String> targetLine = new ArrayList<String>();
				for (int column = 0; column < targetColumn * gridSize; column = column + gridSize) {

					// change the original grid to the target grid by mean value
					// if there is more than half of the each changed grid mount are equals to the
					// noData value
					// make the changed grid value to the noData value
					ArrayList<Double> temptGrid = new ArrayList<Double>();
					for (int gridRow = 0; gridRow < gridSize; gridRow++) {
						for (int gridColumn = 0; gridColumn < gridSize; gridColumn++) {
							if (!this.asciiGrid[row + gridRow][column + gridColumn]
									.equals(this.originalProperty.get("noData"))
									&& !this.asciiGrid[row + gridRow][column + gridColumn].equals("-99")) {
								temptGrid.add(Double.parseDouble(this.asciiGrid[row + gridRow][column + gridColumn]));
							}
						}
					}
					// if (temptGrid.size() > limit) {
					// targetLine.add(new AtCommonMath(temptGrid).getMean() + "");
					// } else {
					// targetLine.add(this.originalProperty.get("noData"));
					// }
					if (temptGrid.size() != gridSize * gridSize) {
						targetLine.add(this.originalProperty.get("noData"));
					} else {
						targetLine.add(new AtCommonMath(temptGrid).getMean() + "");
					}

				}
				targetGrid.add(targetLine.parallelStream().toArray(String[]::new));

			}
			return targetGrid.parallelStream().toArray(String[][]::new);
		} else {
			return this.asciiGrid;
		}
	}

	// <====================>
	// <get the changed ascii content>
	// <====================>
	public String[][] getChangedContent(int gridSize) {
		if (gridSize >= 2) {
			ArrayList<String[]> tempt = getTargetPropertyContent(gridSize);
			ArrayList<String[]> changedGrid = new ArrayList<String[]>(Arrays.asList(getChangedGrid(gridSize)));
			changedGrid.forEach(line -> tempt.add(line));

			return tempt.parallelStream().toArray(String[][]::new);
		} else {
			return this.asciiContent;
		}
	}

}
