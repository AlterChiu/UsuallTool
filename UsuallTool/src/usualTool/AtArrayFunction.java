package usualTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class AtArrayFunction {
	private String[][] content;
	private String[] column;

	public AtArrayFunction(String[][] content){
		this.content = content;
	}
	/**
	 * 
	 * @param order : which column you selected
	 * @return	this column you selected
	 */
	
	public String[] getColumnByOrder(int order){
		String[] temp = new String[this.content.length];
		for(int i=0;i<temp.length;i++){
			temp[i] = this.content[i][order];
		}
		return temp;
	}
	/**
	 * 
	 * @param order : which column be base
	 * @return	a double[]
	 */
	public double[] getColumnByOrderInDouble(int order){
		this.column = new AtArrayFunction(this.content).getColumnByOrder(order);
		return Arrays.stream(this.column).mapToDouble(Double :: parseDouble).toArray();
	}
	/**
	 * 
	 * @param order : which column be base
	 * @return	a String[]
	 */
	
	public String[][]  sortArryaByOrder(int order){
		this.content = new MergeSort(this.content,order).getSortedArray();
		return  this.content;
	}
	
	/**
	 * 
	 * @param order : base on which column
	 * @param spaceNum : how many space between max and min value
	 * @return key: the order the value is ; value : the value in space
	 */
	
	public TreeMap<Integer,TreeMap<String,Double>> getSpacing(int order , int spaceNum){
		TreeMap<Integer,TreeMap<String,Double>> temp = new TreeMap<Integer,TreeMap<String,Double>>();
		this.content = new MergeSort(this.content,order).getSortedArray();
		double maxValue = Double.parseDouble(this.content[content.length-1][order]);
		double minValue = Double.parseDouble(this.content[0][order]);
		double totalDis = maxValue-minValue;
		double space =totalDis /( spaceNum-1);
		
		for(int i=0;i<spaceNum;i++){
			TreeMap<String,Double> tempArray = new TreeMap<String,Double>();
			double base = minValue -  (i+0.5) *space ;
			double top = base + space;
			tempArray.put("base", base);
			tempArray.put("top",top);
			temp.put(i, tempArray);
		}
		return temp;
	}

}
