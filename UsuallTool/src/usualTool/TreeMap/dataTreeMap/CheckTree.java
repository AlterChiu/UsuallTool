package usualTool.TreeMap.dataTreeMap;
import java.util.TreeMap;

public class CheckTree {
	private String firstKey;
	private String value;
	private String secondKey;
	
	public void setFirstKey(String firstKey){
		this.firstKey = firstKey;
	}
	public void setValue(String value){
		this.value = value;
	}
	public void setSecondKey(String secondKey){
		this.secondKey = secondKey;
	}
	
	public TreeMap<String,TreeMap<String,String>> CheckKeys(TreeMap<String,TreeMap<String,String>> tree ){
		if(tree.containsKey(this.firstKey)){
			TreeMap<String,String> temp = tree.get(this.firstKey);
			temp.put(this.secondKey,this.value);
			tree.put(this.firstKey, temp);
		}else{
			TreeMap<String,String> temp = new TreeMap<String,String>();
			temp.put(this.secondKey, this.value);
			tree.put(this.firstKey, temp);
		}
		return tree;
	}
	


}
