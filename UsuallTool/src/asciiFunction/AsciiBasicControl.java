package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;

public class AsciiBasicControl {
	private String[][] asciiContent = null;
	private String fileAdd = null;

	
//	<==============>
//	< constructor function>
//	<==============>
	public AsciiBasicControl(String[][] asciiContent) {
		this.asciiContent = asciiContent;
	}

	public AsciiBasicControl(String fileAdd) throws IOException {
		this.fileAdd = fileAdd;
		this.asciiContent = new AtFileReader(fileAdd).getStr();
	}

	
	
	
//	<=========================>
//	< using while ascii file start by a space >
//	<=========================>
	public AsciiBasicControl cutFirstColumn() throws IOException {
		// function for the open file
		
		if(this.fileAdd!=null){
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
		}else{
			ArrayList<String[]> asciiArray = new  ArrayList<String[]>(Arrays.asList(this.asciiContent));
			ArrayList<String[]> temptArray = new ArrayList<String[]>();
			
			for(int line = 0 ; line<asciiArray.size();line++){
				if(line<6){
					temptArray.add(asciiArray.get(line));
				}else{
					ArrayList<String> temptLine = new ArrayList<String>(Arrays.asList(asciiArray.get(line)));
					temptLine.remove(0);
					temptArray.add(temptLine.parallelStream().toArray(String[]::new));
				}
			}
			this.asciiContent = temptArray.parallelStream().toArray(String[][]::new);
		}
		return this;
	}

	
	
	
	
	
//	<==================>
//	< get the read ascii property>
//	<==================>
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
	
	
	
	
	
	
	
//	<===========================>
//	< get the value by giving location of ascii >
//	<===========================>
	public String getLocation(double x , double y){
		double cornerX = Double.parseDouble(this.asciiContent[2][1]);
		double cornerY = Double.parseDouble(this.asciiContent[3][1]);
		double cellSize = Double.parseDouble(this.asciiContent[4][1]);
		
		int row = new BigDecimal((y-cornerY)/cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int column = new BigDecimal((x-cornerX)/cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		try{
			return this.asciiContent[row][column];
		}catch(Exception e){
			return "error location";
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
//	<---------------------------------------------getting the ascii content------------------------------------------------->
//	<==========================================================================>
	public String[][] getAsciiFile() {
		return this.asciiContent;
	}
	
//	<get the asciiFIle that value in range>
	public String[][] getAsciiFile(double base , double top){
		String noData = this.asciiContent[5][1];
		String[][] tempt = this.asciiContent;
		for(int line=6;line<this.asciiContent.length;line++){
			for(int column=0;column<this.asciiContent[line].length;column++){
				if(!this.asciiContent[line][column].equals(noData)){
					double value = Double.parseDouble(this.asciiContent[line][column]);
					if(value<base || value>top){
						tempt[line][column] = noData;
					}
				}
			}
		}
		return tempt;
	}
	
	public String[][] getAsciiGrid(){
		ArrayList<String[]> temptArray = new ArrayList<String[]>(Arrays.asList(this.asciiContent));
		for(int i=0;i<6;i++){
			temptArray.remove(0);
		}
		return temptArray.parallelStream().toArray(String[][]::new);
	}
//	<============================================================================>
	
	public double getMaxValue(){
		String noData = this.asciiContent[5][1];
		double max = -999;
		for(int line=6;line<this.asciiContent.length;line++){
			for(int column=0;column<this.asciiContent[line].length;column++){
				if(!this.asciiContent[line][column].equals(noData)){
					if(max<Double.parseDouble(this.asciiContent[line][column])){
						max = Double.parseDouble(this.asciiContent[line][column]);
					}
				}
			}
		}
		return new BigDecimal(max).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public double getMinValue(){
		String noData = this.asciiContent[5][1];
		double min = -999;
		for(int line=6;line<this.asciiContent.length;line++){
			for(int column=0;column<this.asciiContent[line].length;column++){
				if(!this.asciiContent[line][column].equals(noData)){
					if(min>Double.parseDouble(this.asciiContent[line][column])){
						min = Double.parseDouble(this.asciiContent[line][column]);
					}
				}
			}
		}
		return new BigDecimal(min).setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
//	<=================>
//	< replace the noData value>
//	<=================>
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
