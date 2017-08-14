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
	
}
