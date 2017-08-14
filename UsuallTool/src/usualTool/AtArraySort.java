package usualTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


public class AtArraySort<E> {
	private String[][] content;
	private int index;
	private double[] keyDouble;
	private TreeMap<E,ArrayList<String[]>> tree = new TreeMap<E,ArrayList<String[]>>();
	
	
	
	public AtArraySort(String[][] content,int index){
		this.content = content;
		this.index = index;
	}
	
	
	public AtArraySort<E> sortedDeleted(){
		TreeMap<E, ArrayList<String[]>> treeMap = new TreeMap<E,ArrayList<String[]>>();
		for(String line[] : this.content){
			treeMap = new AtTreeFunction<E,String[]>(treeMap).replace((E)line[this.index], line);
		}
		this.tree =  treeMap;
		return this;
	}
	
	public AtArraySort<E> sorted(){
		TreeMap<E,ArrayList<String[]>> treeMap = new TreeMap<E,ArrayList<String[]>>();
		for(String line[] : this.content){
			treeMap = new AtTreeFunction<E,String[]>(treeMap).checkTree((E)line[this.index], line);
		}
		this.tree =  treeMap;
		return this;
	}
	
	public String[][] getSorted(){
		String keys[] = (String[]) tree.keySet().parallelStream().toArray(String[]::new);
		ArrayList<String[]>out = new ArrayList<String[]>();
		for(int i=0;i<keys.length;i++){
			ArrayList<String[]> temp = this.tree.get(keys[i]);
			for(int k=0;k<temp.size();k++){
				out.add(temp.get(k));
			}
		}
		return out.parallelStream().toArray(String[][]::new);
		}


public String[][] getCounterSorted(){
	String keys[] = (String[]) tree.keySet().parallelStream().toArray(String[]::new);
	ArrayList<String[]>out = new ArrayList<String[]>();
	for(int i=keys.length-1;i>=0;i--){
		ArrayList<String[]>temp = this.tree.get(keys[i]);
		for(int k=0;k<temp.size();k++){
			out.add(temp.get(k));
		}
	}
	return out.parallelStream().toArray(String[][]::new);
	}
}
