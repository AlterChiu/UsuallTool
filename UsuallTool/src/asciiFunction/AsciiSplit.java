package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AsciiSplit {
	private TreeMap<String, String> asciiProperty;
	private AsciiBasicControl asciiControl;
	private int splitNum;
	private List<Integer> xMaxList;
	private List<Integer> xMinList;
	private List<Integer> yMaxList;
	private List<Integer> yMinList;
	private List<String[][]> outList = new ArrayList<String[][]>();
	private String splitModel = "column";

	// <===============> <=============================== >
	// < this is the construtor > <START point from LeftTop>
	// <===============> <================================>
	public AsciiSplit(String[][] asciiContent) throws IOException {
		this.asciiControl = new AsciiBasicControl(asciiContent);
		this.asciiProperty = asciiControl.getProperty();
	}

	public AsciiSplit(String fileAdd) throws IOException {
		this.asciiControl = new AsciiBasicControl(fileAdd);
		this.asciiProperty = asciiControl.getProperty();
	}

	public AsciiSplit(AsciiBasicControl ascii) {
		this.asciiControl = ascii;
		this.asciiProperty = asciiControl.getProperty();
	}
	// <=============================>
	// < split the asciiFile to equal size respectively >
	// <=============================>

	private List<Integer[]> getSplitNode() {
		List<Integer[]> outList = new ArrayList<Integer[]>();
		int totalLength = Integer.parseInt(this.asciiProperty.get(this.splitModel))-1;
		int splitGridNum = new BigDecimal(totalLength / splitNum).setScale(0, BigDecimal.ROUND_UP).intValue();

		// <Get the node have to split>
		// _____________________________________________________________________________
		ArrayList<Integer> splitNode = new ArrayList<Integer>();
		for (int i = 0; i < splitNum; i++) {
			splitNode.add(i * splitGridNum);
		}
		splitNode.add(totalLength);

		// get the split node in equal size , and the last one is determine by the
		// boundary
		for (int index = 1; index < splitNode.size() - 1; index++) {
			outList.add(new Integer[] { splitNode.get(index - 1), splitNode.get(index) });
		}
		outList.add(new Integer[] { totalLength - splitGridNum  , totalLength});

		return outList;
	}

	//<==================================================>
	//<                                 Set the clip boundary from zero to max                         >
	//<==================================================>
	private void setBoundary() {
		xMaxList = new ArrayList<Integer>();
		xMinList = new ArrayList<Integer>();
		yMaxList = new ArrayList<Integer>();
		yMinList = new ArrayList<Integer>();

		for (int index = 0; index < this.splitNum; index++) {
			xMaxList.add(Integer.parseInt(this.asciiProperty.get("column"))-1);
			xMinList.add(0);
			yMaxList.add(Integer.parseInt(this.asciiProperty.get("row"))-1);
			yMinList.add(0);
		}
	}

	private void clear() {
		xMaxList.clear();
		xMinList.clear();
		yMaxList.clear();
		yMinList.clear();
	}
	
	//<======================================================>
	private void setStraightBoundary() {
		List<Integer[]> splitNode = getSplitNode();
		this.xMaxList.clear();
		this.xMinList.clear();

		for (Integer[] coulmnBoundary : splitNode) {
			this.xMinList.add(coulmnBoundary[0]);
			this.xMaxList.add(coulmnBoundary[1]);
		}
	}

	private void setHorizontalBoundary() {
		List<Integer[]> splitNode = getSplitNode();
		this.yMaxList.clear();
		this.yMinList.clear();

		for (Integer[] rowBoundary : splitNode) {
			this.yMinList.add(rowBoundary[0]);
			this.yMaxList.add(rowBoundary[1]);
		}
	}

	private void clipBoundary() {
		outList.clear();
		for (int index = 0; index < this.xMinList.size(); index++) {
			outList.add(this.asciiControl.getClipAsciiFile(this.xMinList.get(index), this.yMinList.get(index),
					this.xMaxList.get(index), this.yMaxList.get(index)));
		}
	}

	// <------------------------------->
	// < BASIC SETTING >
	// <------------------------------->

	// output will be two asciiFile up and down
	public List<String[][]> straightSplit(int splitNum) {
		this.splitModel = "column";
		this.splitNum = splitNum;
		setBoundary();
		setStraightBoundary();
		clipBoundary();
		clear();
		return this.outList;
	}

	// output will be two asciiFile left and right
	public List<String[][]> horizontalSplit(int splitNum) {
		this.splitModel = "row";
		this.splitNum = splitNum;
		setBoundary();
		setHorizontalBoundary();
		clipBoundary();
		clear();
		return this.outList;
		
	}

}
