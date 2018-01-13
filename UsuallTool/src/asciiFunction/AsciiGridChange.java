package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import usualTool.AtCommonMath;

public class AsciiGridChange {

	private String[][] asciiGrid;
	private TreeMap<String, String> originalProperty;
	private TreeMap<String, String> targetProperty;

	public AsciiGridChange(String asciiFile) throws IOException {
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiFile);
		this.asciiGrid = temptAscii.cutFirstColumn().getAsciiGrid();
		this.originalProperty = temptAscii.getProperty();
	}

	public AsciiGridChange(String[][] asciiContent) {
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiContent);
		this.asciiGrid = temptAscii.getAsciiGrid();
		this.originalProperty = temptAscii.getProperty();
	}

	// <======================>
	// <get the target ascii file property >
	// <======================>
	public TreeMap<String, String> getChangedProperty(int gridSize) {
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

		String targetBottomY = new BigDecimal(targetRow * targetCellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();
		String targetTopX = new BigDecimal(targetColumn * targetCellSize)
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
	}
	
//	<=========================>
//	<get the target property by text array >
//	<=========================>
	public ArrayList<String[]> getTargetPropertyContent(int gridSize){
		TreeMap<String,String> temptProperty = getChangedProperty(gridSize);
		ArrayList<String[]> temptArray = new ArrayList<String[]>();
		
		temptArray.add(new String[] { "ncols", temptProperty.get("column") });
		temptArray.add(new String[] { "nrows", temptProperty.get("row")});
		temptArray.add(new String[] { "xllcorner", temptProperty.get("bottomX")});
		temptArray.add(new String[] { "yllcorner", temptProperty.get("bottomY")});
		temptArray.add(new String[] { "cellsize", temptProperty.get("cellSize")});
		temptArray.add(new String[] { "NODATA_value", temptProperty.get("NODATA_value")});
		
		return temptArray;
	}
	
	
	
	// <======================>
	// <get the target ascii file property >
	// <======================>
	public String[][] getChangedGrid(int gridSize) {
		int targetColumn = Integer.parseInt(this.originalProperty.get("column")) / gridSize;
		int targetRow = Integer.parseInt(this.originalProperty.get("row")) / gridSize;
		int limit = (gridSize * gridSize) / 2;
		ArrayList<String[]> targetGrid = new ArrayList<String[]>();
		
		// the original ascii grid
		for (int row = 0; row < targetRow; row = row + gridSize) {
			ArrayList<String> targetLine = new ArrayList<String>();
			for (int column = 0; column < targetColumn; column = column + gridSize) {
			
				// change the original grid to the target grid by mean value
				// if there is more than half of the each changed grid mount are equals to the
				// noData value
				// make the changed grid value to the noData value
				ArrayList<Double> temptGrid = new ArrayList<Double>();
				for (int gridRow = 0; gridRow < gridSize; gridRow++) {
					for (int gridColumn = 0; gridColumn < gridSize; gridColumn++) {
						if (!this.asciiGrid[row + gridRow][column + gridColumn]
								.equals(this.originalProperty.get("noData"))) {
							temptGrid.add(Double.parseDouble(this.asciiGrid[row + gridRow][column + gridColumn]));
						}
					}
				}
				if (temptGrid.size() > limit) {
					targetLine.add(new AtCommonMath(temptGrid).getMean() + "");
				} else {
					targetLine.add(this.originalProperty.get("noData"));
				}
			}
			targetGrid.add(targetLine.parallelStream().toArray(String[]::new));
		}
		return targetGrid.parallelStream().toArray(String[][]::new);
	}
	
	
//	<====================>
//	<get the changed ascii content>
//	<====================>
	public String[][] getChangedContent(int gridSize) {
		ArrayList<String[]> tempt = getTargetPropertyContent(gridSize);
		ArrayList<String[]> changedGrid  = new ArrayList<String[]>(Arrays.asList(getChangedGrid(gridSize)));
		changedGrid.forEach(line -> tempt.add(line));
		
		return tempt.parallelStream().toArray(String[][]::new);
	}

}
