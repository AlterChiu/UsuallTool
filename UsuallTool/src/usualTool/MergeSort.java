package usualTool;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;


public class MergeSort {
	private String[][] content;
	private int index;
	private double[] keyDouble;
	private TreeMap<Double, ArrayList<String[]>> treemap = new TreeMap<Double, ArrayList<String[]>>();
	
	public MergeSort(String[][] content,int index){
		this.content = content;
		this.index = index;
		this.keyDouble = TreeKeySetter();
		this.keyDouble = TreeKeyMaker();
		ArraySorter();
	}
	
	
	
	
private double[] TreeKeySetter(){
	this.keyDouble = new double[content.length];
	for(int i=0;i<content.length;i++){
		this.keyDouble[i] =Double.parseDouble( content[i][index]);
	}
	return this.keyDouble;
}

private double[] ListToArray(Set content){
	double[] temp = new double[content.size()];
	Object object[] = content.toArray();
	for(int i=0;i<temp.length;i++){
		temp[i] = (double) object[i];
	}
	return temp;
}
	

	public double[]  TreeKeyMaker() {
		// Create TreeMap
		for (int i = 0; i <this. keyDouble.length; i++) {
			ArrayList<String[]> temp = new ArrayList<String[]>();
			if (treemap.containsKey(this.keyDouble[i])) {
				temp = treemap.get(this.keyDouble[i]);
			}
			temp.add(this.content[i]);
			treemap.put(this.keyDouble[i], temp);
		}
		// Get KeySet Of SortTreeMap
		Set<Double> keySet = treemap.keySet();
		
		return ListToArray(keySet);
	}
	
	
	private void    ArraySorter(){
		ArrayList<String[]> content = new ArrayList<String[]>();
		for(int i=0;i<treemap.size();i++){
			ArrayList<String[]> temp = treemap.get(keyDouble[i]);
			for(int j=0;j<temp.size();j++){
				content.add(temp.get(j));
			}
		}
		this.content = content.parallelStream().toArray(String[][]::new);
	}
	
	public String[][] getSortedArray(){
		return this.content;
	}
	
	public double getMaxValue(){
		return this.keyDouble[this.keyDouble.length-1];
	}

	public double getMinValue(){
		return this.keyDouble[0];
	}
	
}
