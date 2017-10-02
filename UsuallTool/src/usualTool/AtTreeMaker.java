package usualTool;

import java.util.ArrayList;
import java.util.TreeMap;

public class AtTreeMaker <E>{
	String[][] content;
	int index;
	
	ArrayList<String> keys = new ArrayList<String>();
	ArrayList<Integer>columns = new ArrayList<Integer>();
	TreeMap<String,ArrayList<E>> temptTree = new TreeMap<String,ArrayList<E>>();
	
	public AtTreeMaker(String content[][] ,int index){
		this.content = content;
		this.index = index;
	}
	
	public   AtTreeMaker(ArrayList<String> tempt){
		
		for(String tt: tempt){
			temptTree.put(tt, new ArrayList<E>());
		}
	}
	
	public   AtTreeMaker(String[] tempt){
		for(String tt: tempt){
			temptTree.put(tt, new ArrayList<E>());
		}
	}
	
	public TreeMap<String,ArrayList<E>> getTreeMaker(){
		return this.temptTree;
	}
	
	public void add(String key , int index){
		keys.add(key);
		columns.add(index);
	}
	
	public TreeMap<String,TreeMap<String,String>> getMixTree (){
		TreeMap<String,TreeMap<String,String>> tree = new TreeMap<String,TreeMap<String,String>>();
		
		for(String line[] : content){
			TreeMap<String,String> temptTree = new TreeMap<String,String>();
			for(int i=0;i<keys.size();i++){
				temptTree.put(keys.get(i), line[columns.get(i)]);
			}
			tree.put(line[index], temptTree);
		}
		return tree;
	}
	
	public TreeMap<String,String> getSingleTree(int target){
		TreeMap<String,String> tree = new TreeMap<String,String>();
		for(String[] line : content){
			tree.put(line[this.index], line[target]);
		}
		return tree;
	}
	
	public TreeMap<String,String[]> getMutiplieTree(int[] target){
		TreeMap<String,String[]> tree = new TreeMap<String,String[]>();
		for(String[] line : content){
			String tempt[] = new String[target.length];
			for(int i=0;i<target.length;i++){
				tempt[i] = line[target[i]];
			}
			tree.put(line[this.index], tempt);
		}
		return tree;
	}
	

}
