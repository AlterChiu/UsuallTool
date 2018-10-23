package usualTool.TreeMap.dataTreeMap;

import java.util.TreeMap;

public class MergeTreeMap {
	
		public TreeMap<String,String> MergeOne(TreeMap<String,String>tree1 , TreeMap<String,String>tree2){
			String key2[] = tree2.keySet().parallelStream().toArray(String[]::new);
			for(String d:key2){
				tree1.put(d, tree2.get(d));
			}
			return tree1;
		}
		
		public TreeMap<String,TreeMap<String,String>> MergeDouble(TreeMap<String,TreeMap<String,String>> tree1 , TreeMap<String,TreeMap<String,String>> tree2){
			String key2[] = tree2.keySet().parallelStream().toArray(String[]::new);
			
			for(int i=0;i<key2.length;i++){
				if(tree1.containsKey(key2[i])){
					TreeMap<String,String> temp1 = tree1.get(key2[i]);
					TreeMap<String,String> temp2 = tree2.get(key2[i]);
					tree1.put(key2[i], this.MergeOne(temp1, temp2));
				}else{
					tree1.put(key2[i], tree2.get(key2[i]));
				}
			}
			return tree1;
		}

}
