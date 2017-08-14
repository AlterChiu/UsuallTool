package usualTool.TreeMap.dataTreeMap.outPut;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class OutDoubleTree {

	private String location;
	private String column1 = "key1";
	private String column2 = "key2";
	private String column3 = "key3";
	
	public OutDoubleTree(String location){
		this.location = location;
	}
	public void setColumn1(String text){
		this.column1 = text;
	}
	public void setColumn2(String text){
		this.column2 = text;
	}
	public void setColumn3(String text){
		this.column3 = text;
	}
	
	public void saveAsCSV(TreeMap<String,TreeMap<String,String>> tree) throws IOException{
		FileWriter fw = new FileWriter(this.location);
		String key1[] = tree.keySet().parallelStream().toArray(String[]::new);
		fw.write(column1 + "," +  column2  + "," +  column3 + "r\n");
		
		for(String d : key1){
			TreeMap<String,String>temp = tree.get(d);
			String key2[] = temp.keySet().parallelStream().toArray(String[]::new);
			
			for(String k : key2){
				fw.write(d);
				fw.write("," + k);
				fw.write(","+temp.get(k)+"\r\n");
			}
		}
	fw.close();	
		
	}
	
	public void saveAs(TreeMap<String,TreeMap<String,String>> tree ,String join) throws IOException{
		FileWriter fw = new FileWriter(this.location);
		String key1[] = tree.keySet().parallelStream().toArray(String[]::new);
		fw.write(column1 + join +  column2  + join +  column3 + "r\n");
		
		for(String d : key1){
			TreeMap<String,String>temp = tree.get(d);
			String key2[] = temp.keySet().parallelStream().toArray(String[]::new);
			
			for(String k : key2){
				fw.write(d);
				fw.write(join + k);
				fw.write(join+temp.get(k)+"\r\n");
			}
		}
	fw.close();	
		
	}
	
}
