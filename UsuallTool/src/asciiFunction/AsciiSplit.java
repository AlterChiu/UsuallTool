package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.omg.CORBA.DoubleSeqHelper;


public class AsciiSplit {
	private String[][] asciiContent;
	private TreeMap<String, String> asciiProperty;
	private String splitModel = "horizontal";
	

	// <===============>            <=============================== >
	// < this is the construtor >              <START         point               from                LeftTop>
	// <===============>            <================================>
	public AsciiSplit(String[][] asciiContent) throws IOException {
		AsciiBasicControl temptAscii = new AsciiBasicControl(asciiContent);
		this.asciiProperty = temptAscii.getProperty();
		this.asciiContent = temptAscii.getAsciiGrid();
	}	
	
	public AsciiSplit(String fileAdd) throws IOException {
		AsciiBasicControl temptAscii = new AsciiBasicControl(fileAdd);
		this.asciiProperty = temptAscii.getProperty();
		this.asciiContent = temptAscii.getAsciiGrid();
	}


	
	
	
	
	
//	<==================================>
//	< split the asciiFile by the coordinate of the asciiFIle >
//	<==================================>
	public String[][] getSplitAsciiByCordinate(double start , double end) {
		// < these variances here are taken global>
		int startIndex ;
		int endIndex;
		
		
		// <===========================STRAIGHT===============================>
		if (this.splitModel.equals("straight")) {
			double startPoint = Double.parseDouble(this.asciiProperty.get("bottomX"));
			// get the cell position in the ascii content
		
			startIndex = (new BigDecimal(
						(start - startPoint) / Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			endIndex =  (new BigDecimal(
					(end - startPoint) / Double.parseDouble(this.asciiProperty.get("cellSize")))
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			
			return getSplitAsciiByGridPosition(startIndex,endIndex);

			
			
			// <============================HORIZONTAL=================================>
		} else {
			// get the cell position in the ascii content
			double startPoint = Double.parseDouble(this.asciiProperty.get("topY"));
			
			startIndex = (new BigDecimal(
						(start - startPoint) / Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			endIndex = (new BigDecimal(
					(end - startPoint) / Double.parseDouble(this.asciiProperty.get("cellSize")))
							.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			
		return getSplitAsciiByGridPosition(startIndex,endIndex);
		}
	}

	
	
	
	
	
	
	
	
	
	
	
//	<=============================>
//	< split the asciiFile to equal size respectively >
//	<=============================>
	public ArrayList<String[][]> getSplitAsciiByEqualCut(int cutLine){
		ArrayList<String[][]> outList = new ArrayList<String[][]>();
		
		// <===========================STARIGHT===============================>
		if(this.splitModel.equals("straight")){
			int columnSize = Integer.parseInt(this.asciiProperty.get("column"));
			int splitGridNum = columnSize/cutLine;
			 
//			<Get the node have to split>
//			_____________________________________________________________________________
			ArrayList<Integer> splitNode = new ArrayList<Integer>();
			for(int i=0;i<cutLine;i++){
				splitNode.add(i*splitGridNum);
			}
			splitNode.add(columnSize);
			
			
//			<get the list of split asciiFile>
//			______________________________________________________________________________
			for(int i=1;i<splitNode.size();i++){
				outList.add(getSplitAsciiByGridPosition(splitNode.get(i-1) , splitNode.get(i)));
			}
			
//		 <===========================HORIZONTAL==================================>
			
		}else{
			int rowSize = Integer.parseInt(this.asciiProperty.get("row"));
			int splitGridNum = rowSize/cutLine;
			
//			<Get the node have to split>
//			_____________________________________________________________________________
			ArrayList<Integer> splitNode = new ArrayList<Integer>();
			for(int i=0;i<cutLine;i++){
				splitNode.add(i*splitGridNum);
			}
			splitNode.add(rowSize);
			
//			<get the list of split asciiFile>
//			______________________________________________________________________________
			for(int i=1;i<splitNode.size();i++){
				outList.add(getSplitAsciiByGridPosition(splitNode.get(i-1) , splitNode.get(i)));
			}
		}
		return outList;
	}
	
	
	
	
	
	
	
	
	
//	<=============================>
//	< split the asciiFile by the position of grid >
//	<=============================>
	
	public String[][] getSplitAsciiByGridPosition(int start , int end) {
		ArrayList<String[]> temptAscii = new ArrayList<String[]>();
		
		
//		<===========================STRAIGHT=============================>
		if(this.splitModel.equals("straight")){
				// < get the split asciiFile content >
				// _________________________________________________________________________________________________
				for (int line = 0; line < this.asciiContent.length; line++) {
					ArrayList<String> temptLine = new ArrayList<String>();
					for (int column = start; column < end; column++) {
						temptLine.add(this.asciiContent[line][column]);
					}
					temptAscii.add(temptLine.parallelStream().toArray(String[]::new));
				}
				
				
				// < get each split asciiFile property >
				// __________________________________________________________________________________________________
				String ncols = new BigDecimal(end - start)
						.setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
				String nrows = this.asciiProperty.get("row");
				String xllCorner = new BigDecimal(Double.parseDouble(this.asciiProperty.get("bottomX"))
						+ start * Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();
				String yllCorner = this.asciiProperty.get("bottomY");
				String cellSize = this.asciiProperty.get("cellSize");
				String nodata_value = this.asciiProperty.get("noData");

				temptAscii.add(0, new String[] { "nodata_value", nodata_value });
				temptAscii.add(0, new String[] { "cellsize", cellSize });
				temptAscii.add(0, new String[] { "yllcenter", yllCorner });
				temptAscii.add(0, new String[] { "xllcenter", xllCorner });
				temptAscii.add(0, new String[] { "nrows", nrows });
				temptAscii.add(0, new String[] { "ncols", ncols });
			
				
				
//			<=========================HORIZONTAL==============================>
		}else{
				// < get the split asciiFile content >
				// ____________________________________________________________________________________________________
				for (int line = start; line < end; line++) {
					ArrayList<String> temptLine = new ArrayList<String>();
					for (int column = 0; column < this.asciiContent[0].length; column++) {
						temptLine.add(this.asciiContent[line][column]);
					}
					temptAscii.add(temptLine.parallelStream().toArray(String[]::new));
				}

				// < get each split asciiFile property >
				// ____________________________________________________________________________________________________
				String ncols = this.asciiProperty.get("column");
				String nrows = new BigDecimal(end - start)
						.setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "";
				String yllCorner = new BigDecimal(Double.parseDouble(this.asciiProperty.get("topY"))
						- (end-1) * Double.parseDouble(this.asciiProperty.get("cellSize")))
								.setScale(globalAscii.scale, BigDecimal.ROUND_HALF_UP).toString();
				String xllCorner = this.asciiProperty.get("bottomX");
				String cellSize = this.asciiProperty.get("cellSize");
				String nodata_value = this.asciiProperty.get("noData");

				temptAscii.add(0, new String[] { "nodata_value", nodata_value });
				temptAscii.add(0, new String[] { "cellsize", cellSize });
				temptAscii.add(0, new String[] { "yllcenter", yllCorner });
				temptAscii.add(0, new String[] { "xllcenter", xllCorner });
				temptAscii.add(0, new String[] { "nrows", nrows });
				temptAscii.add(0, new String[] { "ncols", ncols });
		}

		return temptAscii.parallelStream().toArray(String[][]::new);
	}

	
	
	
	
	
	// <------------------------------->
	// < BASIC SETTING >
	// <------------------------------->

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
