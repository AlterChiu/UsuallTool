package asciiFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.omg.CORBA.DoubleSeqHelper;

import net.sf.javaml.distance.fastdtw.util.Arrays;

public class AsciiSplit {
	private String[][] asciiContent;
	private TreeMap<String, String> asciiProperty;
	private String splitModel = "horizontal";
	private int coveredGridNum = 2;

	// <===============>            <=============================== >
	// < this is the construtor >              <START         point               from                LeftTop>
	// <===============>            <================================>
	public AsciiSplit(String[][] asciiContent) {
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiContent);
		this.asciiProperty = temptAscii.getProperty();
		this.asciiContent = temptAscii.getAsciiGrid();
	}	


	
//	<==================================>
//	< split the asciiFile by the coordinate of the asciiFIle >
//	<==================================>
	public ArrayList<String[][]> getSplitAsciiByCordinate(double[] point) {
		// make the input split point in order
		ArrayList<Double> splitPoint = new ArrayList<Double>(
				DoubleStream.of(point).boxed().collect(Collectors.toList()));
		Collections.sort(splitPoint);

		// < these variances here are taken global>
		ArrayList<Integer> splitCell = new ArrayList<Integer>();

		
		
		// <===========================HORIZONTAL===============================>
		if (this.splitModel.equals("horizontal")) {
			double startPoint = Double.parseDouble(this.asciiProperty.get("bottomX"));
			// get the cell position in the ascii content
			for (Double temptCordinate : splitPoint) {
				splitCell.add(new BigDecimal(
						(temptCordinate - startPoint) / Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			}
			return getSplitAsciiByGridPosistion(splitCell.stream().mapToInt(i->i).toArray());

			// <===========================STARIGHT==================================>
		} else {
			// get the cell position in the ascii content
			double startPoint = Double.parseDouble(this.asciiProperty.get("TopY"));
			for (Double temptCordinate : splitPoint) {
				splitCell.add(new BigDecimal(
						(temptCordinate - startPoint) / Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			}
		return getSplitAsciiByGridPosistion(splitCell.stream().mapToInt(i->i).toArray());
		}
	}

	
	
	
//	<=============================>
//	< split the asciiFile to equal size respectively >
//	<=============================>
	public ArrayList<String[][]> getSplitAsciiByEqualCut(int cutLine){
		
		if(this.splitModel.equals("horizontal")){
			
		}else{
			
		}
		
	}
	
	
	
	
	
//	<=============================>
//	< split the asciiFile by the position of grid >
//	<=============================>
	
	public ArrayList<String[][]> getSplitAsciiByGridPosistion(int[] splitPoint) {
		ArrayList<String[][]> outAsciiList = new ArrayList<String[][]>();
		ArrayList<Integer> splitCell = new ArrayList<Integer>();
		for(int temptPoint : splitPoint){
			splitCell.add(temptPoint);
		}
		Collections.sort(splitCell);
		
		
		
//		<===========================HORIZONTAL=============================>
		if(this.splitModel.equals("horizontal")){
		
				ArrayList<String[]> temptAscii = new ArrayList<String[]>();

				// < get the split asciiFile content >
				// <--------------------------------------------------------------------------------------------->
				for (int line = 0; line < this.asciiContent.length; line++) {
					ArrayList<String> temptLine = new ArrayList<String>();
					for (int column = splitCell.get(0); column < splitCell.get(1); column++) {
						temptLine.add(this.asciiContent[line][column]);
					}
					temptAscii.add(temptLine.parallelStream().toArray(String[]::new));
				}
				
				// < get each split asciiFile property >
				// <---------------------------------------------------------------------------------------------->
				String ncols = new BigDecimal(splitCell.get(1) - splitCell.get(0))
						.setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
				String nrows = this.asciiProperty.get("row");
				String xllCorner = new BigDecimal(Double.parseDouble(this.asciiProperty.get("bottomX"))
						+ splitCell.get(0) * Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();
				String yllCorner = this.asciiProperty.get("bottomY");
				String cellSize = this.asciiProperty.get("cellSize");
				String nodata_value = this.asciiProperty.get("noData");

				temptAscii.add(0, new String[] { "nodata_value", nodata_value });
				temptAscii.add(0, new String[] { "cellSize", cellSize });
				temptAscii.add(0, new String[] { "yllCorner", yllCorner });
				temptAscii.add(0, new String[] { "xllCorner", xllCorner });
				temptAscii.add(0, new String[] { "nrows", nrows });
				temptAscii.add(0, new String[] { "ncols", ncols });

				// save the split asciiFile to Array
				outAsciiList.add(temptAscii.parallelStream().toArray(String[][]::new));
			
			
//			<============================STARTIGHT===========================>
		}else{
				ArrayList<String[]> temptAscii = new ArrayList<String[]>();

				// < get the split asciiFile content >
				// <--------------------------------------------------------------------------------------------->
				for (int line = splitCell.get(0); line < splitCell.get(1); line++) {
					ArrayList<String> temptLine = new ArrayList<String>();
					for (int column = 0; column < this.asciiContent[0].length; column++) {
						temptLine.add(this.asciiContent[line][column]);
					}
					temptAscii.add(temptLine.parallelStream().toArray(String[]::new));
				}

				// < get each split asciiFile property >
				// <---------------------------------------------------------------------------------------------->
				String ncols = this.asciiProperty.get("column");
				String nrows = new BigDecimal(splitCell.get(1) - splitCell.get(0))
						.setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
				String yllCorner = new BigDecimal(Double.parseDouble(this.asciiProperty.get("TopY"))
						- splitCell.get(0) * Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();
				String xllCorner = this.asciiProperty.get("bottomX");
				String cellSize = this.asciiProperty.get("cellSize");
				String nodata_value = this.asciiProperty.get("noData");

				temptAscii.add(0, new String[] { "nodata_value", nodata_value });
				temptAscii.add(0, new String[] { "cellSize", cellSize });
				temptAscii.add(0, new String[] { "yllCorner", yllCorner });
				temptAscii.add(0, new String[] { "xllCorner", xllCorner });
				temptAscii.add(0, new String[] { "nrows", nrows });
				temptAscii.add(0, new String[] { "ncols", ncols });

				
				// save the split asciiFile to Array
				outAsciiList.add(temptAscii.parallelStream().toArray(String[][]::new));
		}

		return outAsciiList;
	}

	
	
	
	
	
	
	
	
	
	
	// <------------------------------->
	// < BASIC SETTING >
	// <------------------------------->

	// setting the number of cell that cover the split asciiFile
	public AsciiSplit setCoveredGridNum(int coveredGridNum) {
		this.coveredGridNum = coveredGridNum;
		return this;
	}

	// output will be two asciiFile up and down
	public AsciiSplit straightSplit() {
		this.splitModel = "straight";
		return this;
	}

	// output will be two asciiFile left and right
	public AsciiSplit horizontalSplit() {
		this.splitModel = "horizontal";
		return this;
	}

}
