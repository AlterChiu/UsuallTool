package asciiFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class AsciiSplit {
	private String[][] asciiContent;
	private TreeMap<String,String> asciiProperty;
	private String splitModel = "straight";
	private int coveredGridNum = 2;
	
	public AsciiSplit(String[][] asciiContent){
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiContent);
		this.asciiProperty = temptAscii.getProperty();
		this.asciiContent = temptAscii.getAsciiGrid();
	}
	
	
	public ArrayList<String[][]> getSplitAsciiByCordinate(double[] point){
		ArrayList<String[][]> outArrayList = new ArrayList<String[][]>();
		for(int splitIndex=0 ; splitIndex<point.length;splitIndex++){
			
		}
		
		
		
	}
	public ArrayList<String[][]> getSplitAsciiByGridPosistion(){
		
	}
	
	
	
	//<======================>
	//<          start from the top left         >
	//<======================>
	private String[][] getSplitAsciiContent(int[] start , int[] end){
		
		
		
		
	}
	
	
	
	
	
	
	
//	<------------------------------->
//	<           BASIC   SETTING         >
//	<------------------------------->
	public AsciiSplit setCoveredGridNum(int coveredGridNum){
		this.coveredGridNum = coveredGridNum;
		return this;
	}
	public AsciiSplit straightSplit(){
		this.splitModel = "straight";
		return this;
	}
	public AsciiSplit horizontalSplit(){
		this.splitModel = "horizontal";
		return this;
	}
	
	
	

}
