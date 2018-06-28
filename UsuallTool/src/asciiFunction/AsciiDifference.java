package asciiFunction;

import java.io.IOException;

public class AsciiDifference {
	private AsciiBasicControl originalAscii;
	private AsciiBasicControl targetAscii;
	private int intersect;
	private int combine;
	private double differenceTotal;
	
	public AsciiDifference(String[][] original , String[][] target) throws IOException{
		this.originalAscii = new AsciiBasicControl(original);
		this.targetAscii  = new AsciiBasicControl(target);
		
		getDifference();
	}
	
	public AsciiDifference(String original , String target) throws IOException{
		this.originalAscii = new AsciiBasicControl(original);
		this.targetAscii  = new AsciiBasicControl(target);
		
		getDifference();
	}
	
	private void getDifference(){
		String[][] originalGrid = this.originalAscii.getAsciiGrid();
		String[][] targetGrid = this.targetAscii.getAsciiGrid();
		
		int intersect = 0;
		int combine = 0;
		double differenceTotal = 0;
		
		
		for (int line = 0; line < originalGrid.length; line++) {
			
			for (int column = 0; column < originalGrid[0].length; column++) {
				if (!originalGrid[line][column].equals(this.originalAscii.getProperty().get("noData"))) {
					if(originalGrid[line][column].equals(targetGrid[line][column])){
						intersect++;
					}else{
						combine++;
						differenceTotal = differenceTotal + Double.parseDouble(originalGrid[line][column]) - Double.parseDouble(targetGrid[line][column]);
					}
				}
			}
		}
		this.combine = combine;
		this.intersect = intersect;
		this.differenceTotal = differenceTotal;
	}
	
	public int getCombine(){
		return this.combine;
	}
	public int getIntersect(){
		return this.intersect + this.combine;
	}
	public double getPersentage(){
		return ((double)this.intersect)/((double)this.getIntersect());
	}
	public double getDifferenceTotal(){
		return this.differenceTotal;
	}

}
