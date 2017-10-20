package usualTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;
import java.util.TreeMap;

public class AtFileReader {
	private  TreeMap<Integer, String> tr = new TreeMap<Integer, String>();

	
	public AtFileReader(String file_add ) throws IOException {

		BufferedReader Br = new BufferedReader(new InputStreamReader(new FileInputStream(file_add)));
		int i = 0;
		String tempt;

		while ((tempt = Br.readLine()) != null) {
			tr.put(i, tempt);
			i++;
		}
		Br.close();
	}
	
	public AtFileReader(InputStreamReader input) throws IOException{
		BufferedReader Br = new BufferedReader(input);
		int i = 0;
		String tempt;

		while ((tempt = Br.readLine()) != null) {
			tr.put(i, tempt);
			i++;
		}
		Br.close();
	}
	
	
	 public  AtFileReader(String  fileAdd ,String encode) throws IOException{          
	            FileInputStream fis = new FileInputStream(new File(fileAdd));  
	            byte[] lineb = new byte[500];  
	            
	            int readLine=0;
	            StringBuffer sb= new StringBuffer("");  
	            while(fis.read(lineb)> 0){  
	                String utf8 = new String(lineb,encode);
	                sb.append(utf8);
	            }
	            String content[] = sb.toString().split("\n");
	            for(int i=0;i<content.length-1;i++){
	            	tr.put(i, content[i]);
	            }
	            fis.close();  
	    }  

	public String[] getContain() {
		String[] content = new String[tr.size()];
		for (int i = 0; i < tr.size(); i++) {
			content[i] = tr.get(i);
		}
		return (content);
	}
	public String[] getContain(int start , int end){
		
		String[] content = new String[tr.size()-start-end];
		for (int i = start; i < tr.size()-end; i++) {
			content[i-start] = tr.get(i);
		}
		return (content);
	}

	public String[][] getCsv() {
		String[][] content = new String[tr.size()][];
		for (int i = 0; i < tr.size(); i++) {
			content[i] = tr.get(i).split(",");
		}
		return content;
	}
	public String[][] getCsv(int start , int end) {
		String[][] content = new String[tr.size()-start-end][];
		for (int i = start; i < tr.size()-end; i++) {
			content[i-start] = tr.get(i).split(",");
		}
		return content;
	}
	
	public String[][] getContent(String split) {
		String[][] content = new String[tr.size()][];
		for (int i = 0; i < tr.size(); i++) {
			content[i] = tr.get(i).split(split);
		}
		return content;
	}
	public String[][] getContent(String split , int start , int end) {
		String[][] content = new String[tr.size()-start-end][];
		for (int i = start; i < tr.size()-end; i++) {
			content[i-start] = tr.get(i).split(split);
		}
		return content;
	}

	public String[][] getStr() {
		String[][] content = new String[tr.size()][];
		for (int i = 0; i < tr.size(); i++) {
			content[i] = tr.get(i).split(" +");
		}
		return content;
	}
	
	public String[][] getStr(int start , int end) {
		String[][] content = new String[tr.size()-start-end][];
		for (int i = start; i < tr.size()-end; i++) {
			content[i-start] = tr.get(i).split(" +");
		}
		return content;
	}

	public String[] getContainWithOut(String text) {
		TreeMap<Integer,String> tempt  = new TreeMap<Integer,String>() ;		
	
		for(int i=0;i<tr.size();i++){
			if(!tr.get(i).contains(text)){
				tempt.put(i, tr.get(i));
			}
		}
		String[] content = new String[tempt.size()];
		Integer[] okok = tempt.keySet().toArray(new Integer[content.length]);
		
	for(int i=0;i<okok.length;i++){
		content[i] = tempt.get(okok[i]);
	}

		return content;
	}
	
	public String[] getContainWith(String text) {
		TreeMap<Integer,String> tempt  = new TreeMap<Integer,String>() ;		
	
		for(int i=0;i<tr.size();i++){
			if(tr.get(i).contains(text)){
				tempt.put(i, tr.get(i));
			}
		}
		String[] content = new String[tempt.size()];
		Integer[] okok = tempt.keySet().toArray(new Integer[content.length]);
		
	for(int i=0;i<okok.length;i++){
		content[i] = tempt.get(okok[i]);
	}

		return content;
	}
	
}
