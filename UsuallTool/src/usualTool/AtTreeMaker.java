package usualTool;

import java.util.ArrayList;
import java.util.TreeMap;

public class AtTreeMaker {
	String[][] content;
	int index;
	
	ArrayList<String> keys = new ArrayList<String>();
	ArrayList<Integer>columns = new ArrayList<Integer>();
	
	public AtTreeMaker(String content[][] ,int index){
		this.content = content;
		this.index = index;
	}
	
	public void add(String key , int index){
		keys.add(key);
		columns.add(index);
	}
	
	public TreeMap<String,TreeMap<String,String>> getTree (){
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

}
