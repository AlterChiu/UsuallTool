package usualTool.TreeMap.dataTreeMap.Table;

import java.util.TreeMap;

import usualTool.TreeMap.dataTreeMap.CheckTree;


public class DoubleKeyTree {

	private int firstKey = 0;
	private int secondKey = 1;
	private int valueKey = 2;
	private String[] firstKeyList;

	private CheckTree checkTree = new CheckTree();
	private TreeMap<String,TreeMap<String,String>> tree = new TreeMap<String,TreeMap<String,String>>();
	

	public void setFirstKeyOrder(int firstKey) {
		this.firstKey = firstKey;
	}
	
	public void setsecondKeyOrder(int secondKey){
		this.secondKey = secondKey;
	}
	
	public void setKeyOrder(int firstKey , int secondKey , int valueKey){
		this.firstKey = firstKey;
		this.secondKey = secondKey;
		this.valueKey = valueKey;
	}
	
	public void setValueOrder(int valueKey){
		this.valueKey = valueKey;
	}
	
	public TreeMap<String,TreeMap<String,String>> getDataTreeMap( String content[][]){

		for(String line[] :content){
			try{
			checkTree.setFirstKey(line[this.firstKey]);
			checkTree.setSecondKey(line[this.secondKey]);
			checkTree.setValue(line[this.valueKey]);
			}catch(Exception e){
				System.out.println("missing value " + String.join(",", line));
			}
			tree = checkTree.CheckKeys(tree);
		}
		
		firstKeyList = this.tree.keySet().parallelStream().toArray(String[]::new);
	return this.tree;
	}
	
	public String[] getFirstKeyList(){
		return this.firstKeyList;
	}
	
	public String[] getSecondKeyList(String key){
		return this.tree.get(key).keySet().parallelStream().toArray(String[]::new);
	}
	
	public String[] getSecondKeyList(int keyOrder){ 
		return this.tree.get(firstKeyList[keyOrder]).keySet().parallelStream().toArray(String[]::new);
	}
	
}
