package testFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import usualTool.AtFileWriter;

public class testAtCommon {

	public static void main(String[] args) throws IOException, OperationNotSupportedException {
		// TODO Auto-generated method stub

		String fileAdd = "S:\\Users\\alter\\Desktop\\Output\\";
		ArrayList<String> paths = new ArrayList<String>();
		int line = 0 ;
		
		for(String folderName : new File(fileAdd).list()) {
			String years[] = new File(fileAdd + folderName).list();
			
			for(String year : years) {
				String events[] = new File(fileAdd + folderName + "\\" + year).list();
				
				for(String event : events) {
					paths.add("set paths[" + line + "] = \"" + fileAdd + folderName + "\\" + year + "\\" + event + "\\\"");
					line++;
				}
				
			}
		}
		new AtFileWriter(paths.parallelStream().toArray(String[]::new) , fileAdd + "paths.txt").textWriter("");
	}
}