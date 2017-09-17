package usualTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class AtArrayFunction<E> {
	private String[][] content;
	private String[] column;
	private E[] singleContent;
	

	public AtArrayFunction(String[][] content){
		this.content = content;
	}
	
	public AtArrayFunction(E[] content){
		this.singleContent = content;
	}
	public String[] getSingleString(){
		ArrayList<String> tempt = new ArrayList<String>();
		for(E e: singleContent){
			tempt.add(String.valueOf(e));
		}
		return tempt.parallelStream().toArray(String[]::new);
	}

	public double[] getSingleDouble(){
		ArrayList<Double> tempt = new ArrayList<Double>();
		for(E e: singleContent){
			tempt.add(Double.parseDouble(e+""));
		}
		return tempt.stream().mapToDouble(Double::doubleValue).toArray();
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
	
	public List<E> getListByOrder(int order){
		List<E> temp = new ArrayList<E>();
		for(int i=0;i<content.length;i++){
			temp.add((E)content[i][order]);
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
	
	public String[][] getColumnWithoutOrder(int order){
		ArrayList<String[]> out = new ArrayList<String[]>();
		for(String line[] :content){
			ArrayList<String> tempt = new ArrayList<String>(Arrays.asList(line));
			tempt.remove(order);
			out.add(tempt.parallelStream().toArray(String[]::new));
		}
		return out.parallelStream().toArray(String[][]::new);
	}
	
}
