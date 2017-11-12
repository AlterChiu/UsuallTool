package asciiFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class AsciiMerge {
	private TreeMap<String, String> firstProperty;
	private TreeMap<String, String> secondProperty;

	private double cellSize;
	private String noData = "-999.00000";

	private double firstTopX;
	private double firstTopY;
	private double firstBottomX;
	private double firstBottomY;

	private double secondTopX;
	private double secondTopY;
	private double secondBottomX;
	private double secondBottomY;

	private String[][] firstAsciiContent;
	private String[][] secondAsciiContent;
	private String[][] outAsciiContent;

	private double outTopX;
	private double outTopY;
	private double outBottomX;
	private double outBottomY;

	public AsciiMerge(String[][] ascii1, String[][] ascii2) {
		this.firstAsciiContent = ascii1;
		this.secondAsciiContent = ascii2;

		this.firstProperty = new AsciiBasicControl(ascii1).getProperty();
		this.secondProperty = new AsciiBasicControl(ascii2).getProperty();
		setOutPutAsciiProperty();
		setOutPutAsciiContent();
		mergeAscii();
	}

	public ArrayList<String[]> getMergedAsciiArray() {
		TreeMap<String, String> temptTree = getMergedProperty();
		ArrayList<String[]> temptContent = new ArrayList<String[]>(Arrays.asList(this.outAsciiContent));

		temptContent.add(0, new String[] { "NODATA_value ", temptTree.get("noData") });
		temptContent.add(0, new String[] { "cellsize", temptTree.get("cellSize") });
		temptContent.add(0, new String[] { "yllcorner", temptTree.get("bottomY") });
		temptContent.add(0, new String[] { "xllcorner", temptTree.get("bottomX") });
		temptContent.add(0, new String[] { "nrows", temptTree.get("row") });
		temptContent.add(0, new String[] { "ncols", temptTree.get("column") });

		return temptContent;
	}

	public String[][] getMergedAscii() {
		return getMergedAsciiArray().parallelStream().toArray(String[][]::new);
	}

	public TreeMap<String, String> getMergedProperty() {
		TreeMap<String, String> temptTree = new TreeMap<String, String>();

		temptTree.put("column", this.outAsciiContent[0].length + "");
		temptTree.put("row", this.outAsciiContent.length + "");
		temptTree.put("bottomX",
				new BigDecimal(this.outBottomX).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());
		temptTree.put("bottomY",
				new BigDecimal(this.outBottomY).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());
		temptTree.put("cellSize",
				new BigDecimal(this.cellSize).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());
		temptTree.put("noData", this.noData);

		return temptTree;
	}

	private void mergeAscii() {
		// start from the left top point
		double temptRow = this.outTopY;
		double temptColumn = this.outBottomX;

		// the first time to begin the out ascii content
		for (int row = 0; row < this.outAsciiContent.length; row++) {
			// reset the read point location
			temptRow = this.outTopY - row * this.cellSize;

			for (int column = 0; column < this.outAsciiContent[row].length; column++) {
				temptColumn = this.outBottomX + column * this.cellSize;
				// line read the outAscii content
				if (this.outAsciiContent[row][column] == null) {
					if (Math.abs(temptColumn - this.firstBottomX) < 0.0001
							&& Math.abs(temptRow - this.firstTopY) < 0.0001) {
						// if the location right now is equals to the first
						// start point, insert the first ascii content
						for (int insertRow = 0; insertRow < this.firstAsciiContent.length; insertRow++) {
							for (int insertColumn = 0; insertColumn < this.firstAsciiContent[row].length; insertColumn++) {
								this.outAsciiContent[row + insertRow][column
										+ insertColumn] = this.firstAsciiContent[insertRow][insertColumn];
							}
						}
					} else {
						this.outAsciiContent[row][column] = this.noData;
					}
				}
			}
		}

		// insert the second ascii file to the out ascii content
		temptRow = this.outTopY;

		// the first time to begin the out ascii content
		for (int row = 0; row < this.outAsciiContent.length; row++) {
			// reset the read point location
			temptRow =  this.outTopY - row * this.cellSize;

			for (int column = 0; column < this.outAsciiContent[row].length; column++) {
				temptColumn = this.outBottomX + column * this.cellSize;
				// line read the outAscii content
				if (Math.abs(temptColumn - this.secondBottomX) < 0.0001
						&& Math.abs(temptRow - this.secondTopY) < 0.0001) {
					System.out.println(row + "\t" + column + "\t" + this.secondAsciiContent.length);
					// if the location right now is equals to the first start
					// point, insert the first ascii content
					for (int insertRow = 0; insertRow < this.secondAsciiContent.length; insertRow++) {
						for (int insertColumn = 0; insertColumn < this.secondAsciiContent[row].length; insertColumn++) {
							this.outAsciiContent[row + insertRow][column
									+ insertColumn] = this.secondAsciiContent[insertRow][insertColumn];
						}
					}
				}
			}
		}
	}

	private void setOutPutAsciiContent() {
		ArrayList<String[]> temptFirst = new ArrayList<String[]>(Arrays.asList(this.firstAsciiContent));
		ArrayList<String[]> temptSecond = new ArrayList<String[]>(Arrays.asList(this.secondAsciiContent));
		// getOff the property line
		for (int i = 0; i < 6; i++) {
			temptFirst.remove(0);
			temptSecond.remove(0);
		}

		this.secondAsciiContent = temptSecond.parallelStream().toArray(String[][]::new);
		this.firstAsciiContent = temptFirst.parallelStream().toArray(String[][]::new);

		int row = new BigDecimal((this.outTopY - this.outBottomY) / this.cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).intValue()+1;
		int column = new BigDecimal((this.outTopX - this.outBottomX) / this.cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).intValue()+1;

		this.outAsciiContent = new String[row][column];

	}

	private void setOutPutAsciiProperty() {
		this.cellSize = Double.parseDouble(this.firstProperty.get("cellSize"));
		System.out.println(this.cellSize);
		this.noData = this.firstProperty.get("noData");
		this.secondAsciiContent = new AsciiBasicControl(this.secondAsciiContent)
				.changeNoDataValue(this.firstProperty.get("noData")).getAsciiFile();

		// initial the corner point of the ascii file
		this.firstBottomX = Double.parseDouble(this.firstProperty.get("bottomX"));
		this.firstBottomY = Double.parseDouble(this.firstProperty.get("bottomY"));
		this.firstTopX = Double.parseDouble(this.firstProperty.get("topX"));
		this.firstTopY = Double.parseDouble(this.firstProperty.get("topY"));

		this.secondBottomX = Double.parseDouble(this.secondProperty.get("bottomX"));
		this.secondBottomY = Double.parseDouble(this.secondProperty.get("bottomY"));
		this.secondTopX = Double.parseDouble(this.secondProperty.get("topX"));
		this.secondTopY = Double.parseDouble(this.secondProperty.get("topY"));
		
		

		// TopX
		if (this.firstTopX >= this.secondTopX) {
			this.outTopX = this.firstTopX;
		} else {
			this.outTopX = this.secondTopX;
		}
		// TopY
		if (this.firstTopY >= this.secondTopY) {
			this.outTopY = this.firstTopY;
		} else {
			this.outTopY = this.secondTopY;
		}
		// BottomX
		if (this.firstBottomX <= this.secondBottomX) {
			this.outBottomX = this.firstBottomX;
		} else {
			this.outBottomX = this.secondBottomX;
		}
		// BottomY
		if (this.firstBottomY <= this.secondBottomY) {
			this.outBottomY = this.firstBottomY;
		} else {
			this.outBottomY = this.secondBottomY;
		}
		System.out.println("out ");
		System.out.println("Top: " + this.outTopX  + "    " + this.outTopY);
		System.out.println("Bot:  " + this.outBottomX + "  " + this.outBottomY);
	}

}
