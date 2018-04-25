package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class AsciiFillIn {
	private AsciiBasicControl asciiControl;
	private double maxX;
	private double maxY;
	private double minX;
	private double minY;
	private Map<String,String> property ;
	
	
//	<=======================>
//	<                CONSTRUCTOR               >
//	<=======================>
	public AsciiFillIn(String fileAdd) throws IOException {
		this.asciiControl = new AsciiBasicControl(fileAdd);
		this.property = asciiControl.getProperty();
		
	}
	public AsciiFillIn(String[][] ascii) throws IOException {
		this.asciiControl = new AsciiBasicControl(ascii);
		this.property = asciiControl.getProperty();
	}
	public AsciiFillIn(AsciiBasicControl asciiControl) {
		this.asciiControl = asciiControl;
		this.property = asciiControl.getProperty();
	}
//<============================================>
	
	
	public AsciiFillIn setBondary(double maxX , double maxY , double minX , double minY) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.minX = minX;
		this.minY = minY;
		return this;
	}
	
	
	public String[][] getFillAscii(){
		ArrayList<String[]> outArray = new ArrayList<String[]>();
		double cellSize = Double.parseDouble(this.property.get("cellSize"));
		String noData = this.property.get("noData");
		int rows = new BigDecimal((this.maxY - this.minY)/cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int columns = new BigDecimal((this.maxX - this.minX)/cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		
		
		outArray.add(new String[] {"ncols" , columns + ""});
		outArray.add(new String[] {"nrows" , rows + ""});
		outArray.add(new String[] {"xllcenter" , this.minX + ""});
		outArray.add(new String[] {"yllcenter" , this.minY + ""});
		outArray.add(new String[] {"cellsize" , cellSize + ""});
		outArray.add(new String[] {"nodata_value" , noData});
		
		for(int row =0 ; row<rows ; row++) {
			double temptY = this.maxY - row*cellSize;
			ArrayList<String> temptRow = new ArrayList<String>();
			for(int column = 0 ; column<columns ; column++) {
				double temptX = this.minX + column*cellSize;
				temptRow.add(this.asciiControl.getValue(temptX, temptY));
			}
			outArray.add(temptRow.parallelStream().toArray(String[]::new));
		}
		return outArray.parallelStream().toArray(String[][]::new);
	}
	
}
