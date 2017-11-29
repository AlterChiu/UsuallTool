package asciiFunction;

import java.io.IOException;
import java.util.TreeMap;

public class AsciiClip {
	private AsciiBasicControl ascii;
	private TreeMap<String,String>property;
	private String[][] asciiGrid;
	
	public AsciiClip(String fileAdd) throws IOException{
		this.ascii = new AsciiBasicControl(fileAdd);
		this.property = ascii.getProperty();
		this.asciiGrid = ascii.getAsciiGrid();
	}
	
	public AsciiClip(String[][] asciiFile) throws IOException{
		this.ascii = new AsciiBasicControl(asciiFile);
		this.property = ascii.getProperty();
		this.asciiGrid = ascii.getAsciiGrid();
	}
	
	
	
	
//	<=========================>
//	<start from the left top of the asciiGrid>
//	<=========================>
	public void clip(double startX , double startY , double endX , double endY){
		
	}
}
