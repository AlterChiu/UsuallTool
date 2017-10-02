package usualTool;

import java.util.ArrayList;
import java.util.TreeMap;

public class AtTreeFunction<E,V> {
	TreeMap<E,ArrayList<V>> tree;
	
	public AtTreeFunction(TreeMap<E,ArrayList<V>> tree ){
		this.tree = tree;
	}
	
	
	public TreeMap<E,ArrayList<V>> checkTree(E key ,V value){
		ArrayList<V> temp;
		if(this.tree.containsKey(key)){
			temp = this.tree.get(key);
			temp.add(value);
			tree.put(key,temp);
		}else{
			temp = new ArrayList<V>();
			temp.add(value);
			tree.put(key,temp);
		}
		return this.tree;
	}

	public TreeMap<E,ArrayList<V>> replace(E key ,V value){
		ArrayList<V> temp = new ArrayList<V>();
		temp.add(value);
		tree.put(key,temp);
		return this.tree;
	}
	
	
}
