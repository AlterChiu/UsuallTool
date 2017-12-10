package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;

public class AsciiBasicControl{
	private String[][] asciiContent = null;
	private String fileAdd = null;
	private TreeMap<String,String> property;
	private String[][] asciiGrid;

	// <==============>
	// < constructor function>
	// <==============>
	public AsciiBasicControl(String[][] asciiContent) {
		this.asciiContent = asciiContent;
		this.property = this.getProperty();
		this.asciiGrid = this.getAsciiGrid();
	}

	public AsciiBasicControl(String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.asciiContent = new AtFileReader(fileAdd).getStr();
		this.property = this.getProperty();
		this.asciiGrid = this.getAsciiGrid();
	}

	
	
	
	
	// <=========================>
	// < using while ascii file start by a space >
	// <=========================>
	public AsciiBasicControl cutFirstColumn() throws IOException {
		// function for the open file

		if (this.fileAdd != null) {
			ArrayList<String[]> temptArray = new ArrayList<String[]>();
			String[] temptContent = new AtFileReader(this.fileAdd).getContain();
			for (int line = 0; line < 6; line++) {
				temptArray.add(temptContent[line].split(" +"));
			}
			for (int line = 6; line < temptContent.length; line++) {
				temptArray.add(temptContent[line].trim().split(" +"));
			}
			this.asciiContent = temptArray.parallelStream().toArray(String[][]::new);

			// function for the reading array
		} else {
			ArrayList<String[]> asciiArray = new ArrayList<String[]>(Arrays.asList(this.asciiContent));
			ArrayList<String[]> temptArray = new ArrayList<String[]>();

			for (int line = 0; line < asciiArray.size(); line++) {
				if (line < 6) {
					temptArray.add(asciiArray.get(line));
				} else {
					ArrayList<String> temptLine = new ArrayList<String>(Arrays.asList(asciiArray.get(line)));
					temptLine.remove(0);
					temptArray.add(temptLine.parallelStream().toArray(String[]::new));
				}
			}
			this.asciiContent = temptArray.parallelStream().toArray(String[][]::new);
		}
		return this;
	}
	
	
	

	// <==================>
	// < get the read asciiFile property>
	// <==================>
	public TreeMap<String, String> getProperty() {
		TreeMap<String, String> temptTree = new TreeMap<String, String>();

		temptTree.put("column", this.asciiContent[0][1]);
		temptTree.put("row", this.asciiContent[1][1]);
		temptTree.put("bottomX", this.asciiContent[2][1]);
		temptTree.put("bottomY", this.asciiContent[3][1]);
		temptTree.put("cellSize", this.asciiContent[4][1]);
		temptTree.put("noData", this.asciiContent[5][1]);
		double cellSize = Double.parseDouble(new BigDecimal(this.asciiContent[4][1])
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());

		temptTree.put("topX",
				new BigDecimal(
						Double.parseDouble(this.asciiContent[2][1]) + cellSize * (this.asciiContent[6].length - 1))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());

		temptTree.put("topY",
				new BigDecimal(
						Double.parseDouble(this.asciiContent[3][1]) + cellSize * (this.asciiContent.length - 6 - 1))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString());

		return temptTree;
	}
	
	public String[][] getPropertyText() {
		ArrayList<String[]> temptTree = new ArrayList<String[]>();

		temptTree.add(new String[]{"ncols", this.asciiContent[0][1]});
		temptTree.add(new String[]{"nrows", this.asciiContent[1][1]});
		temptTree.add(new String[]{"xllcorner", this.asciiContent[2][1]});
		temptTree.add(new String[]{"yllcorner", this.asciiContent[3][1]});
		temptTree.add(new String[]{"cellsize", this.asciiContent[4][1]});
		temptTree.add(new String[]{"NODATA_value", this.asciiContent[5][1]});

		return temptTree.parallelStream().toArray(String[][]::new);
	}

	
	
	
	// <===========================>
	// < get the value by giving location of ascii >
	// <===========================>
	public String getValue(double x, double y) {
		
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		double cellSize = Double.parseDouble(this.property.get("cellSize"));

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		try {
			return this.asciiGrid[row][column];
		} catch (Exception e) {
			return "error location";
		}
	}

	
	
	
	
	// <===============================>
	// < get the position by giving coordinate of ascii >                            < x , y ><column , row>
	// <================================>
	public int[] getPosition(double x, double y) {
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		double cellSize = Double.parseDouble(this.property.get("cellSize"));

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		return new int[]{column,row};
	}
	
	public double[] getCoordinate(int column , int row){
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		
		double x =  new BigDecimal(startX + column*cellSize).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		double y = new BigDecimal(startY - row*cellSize).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		return new double[]{x,y};
	}
	
	public double[] getClosestCoordinate(double x,double y){
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		double cellSize = Double.parseDouble(this.property.get("cellSize"));

		int row = new BigDecimal((startY - y) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int column = new BigDecimal((x - startX) / cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		
		return new double[]{startX + column*cellSize , startY - row*cellSize};
	}
	
	
	
	
	
	
	
	// <===========================>
	// < get the ASCII GRID >
	// <===========================>

	// <-----------------getting the asciiContent---------------------------->
	// <===============================================================>
	public String[][] getAsciiFile() {
		return this.asciiContent;
	}

	// <get the asciiFIle that value in range>
	public String[][] getAsciiFile(double base, double top) {
		String noData = this.asciiContent[5][1];
		String[][] tempt = this.asciiContent;
		for (int line = 6; line < this.asciiContent.length; line++) {
			for (int column = 0; column < this.asciiContent[line].length; column++) {
				if (!this.asciiContent[line][column].equals(noData)) {
					double value = Double.parseDouble(this.asciiContent[line][column]);
					if (value < base || value > top) {
						tempt[line][column] = noData;
					}
				}
			}
		}
		return tempt;
	}
	
	public String[][] getAsciiGrid() {
		ArrayList<String[]> temptArray = new ArrayList<String[]>(Arrays.asList(this.asciiContent));
		for (int i = 0; i < 6; i++) {
			temptArray.remove(0);
		}
		return temptArray.parallelStream().toArray(String[][]::new);
	}

	
//	<getting the asciiGrid by setting the coordinate>
//	<______________________________________________________________________________________________>
	public String[][] getAsciiGrid(double minX, double minY, double maxX, double maxY) {
		ArrayList<String[]> asciiGrid = new ArrayList<String[]>();
		TreeMap<String, String> temptProperty = this.getProperty();
		double cellSize = Double.parseDouble(temptProperty.get("cellSize"));

		int startLine = new BigDecimal((Double.parseDouble(temptProperty.get("topY")) - maxY) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).intValue() + 6;

		int endLine = new BigDecimal((Double.parseDouble(temptProperty.get("topY")) - minY) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).intValue() + 6;

		int startColumn = new BigDecimal(minX - (Double.parseDouble(temptProperty.get("bottomX"))) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).intValue();

		int endColumn = new BigDecimal(maxX - (Double.parseDouble(temptProperty.get("bottomX"))) / cellSize)
				.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).intValue();

		for (int line = startLine; line <= endLine; line++) {
			ArrayList<String> temptLine = new ArrayList<String>();
			for (int column = startColumn; column <= endColumn; column++) {
				temptLine.add(this.asciiContent[line][column]);
			}
			asciiGrid.add(temptLine.parallelStream().toArray(String[]::new));
		}

		return asciiGrid.parallelStream().toArray(String[][]::new);
	}

	
//	<get asciiGrid by setting the position>
//	<___________________________________________________________________________>
	public String[][] getAsciiGrid(int minX, int minY, int maxX, int maxY) {
		ArrayList<String[]> asciiGrid = new ArrayList<String[]>();

		int startLine = maxY + 6;

		int endLine = minY + 6;

		int startColumn = minX;

		int endColumn = maxX;

		for (int line = startLine; line <= endLine; line++) {
			ArrayList<String> temptLine = new ArrayList<String>();
			for (int column = startColumn; column <= endColumn; column++) {
				temptLine.add(this.asciiContent[line][column]);
			}
			asciiGrid.add(temptLine.parallelStream().toArray(String[]::new));
		}

		return asciiGrid.parallelStream().toArray(String[][]::new);
	}
	// <============================================================================>
	
	
	
	
	
	
	
//	<=========================>
//	<getting the specifics value in asciiFile>
//	<===========================>
	

//	<get the max value in asciiFile>
//	<__________________________________________________________________>
	public double getMaxValue() {
		String noData = this.asciiContent[5][1];
		double max = -999;
		for (int line = 6; line < this.asciiContent.length; line++) {
			for (int column = 0; column < this.asciiContent[line].length; column++) {
				if (!this.asciiContent[line][column].equals(noData)) {
					if (max < Double.parseDouble(this.asciiContent[line][column])) {
						max = Double.parseDouble(this.asciiContent[line][column]);
					}
				}
			}
		}
		return new BigDecimal(max).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getMinValue() {
		String noData = this.asciiContent[5][1];
		double min = 999;
		for (int line = 6; line < this.asciiContent.length; line++) {
			for (int column = 0; column < this.asciiContent[line].length; column++) {
				if (!this.asciiContent[line][column].equals(noData)) {
					if (min > Double.parseDouble(this.asciiContent[line][column])) {
						min = Double.parseDouble(this.asciiContent[line][column]);
					}
				}
			}
		}
		return new BigDecimal(min).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	
	
	
	
	
	
	// <=================>
	// < replace the noData value>
	// <=================>
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

}
