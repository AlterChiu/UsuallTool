package usualTool;

import java.io.FileWriter;
import java.io.IOException;

public class AsciiReader {
	public AsciiReader(String fileAdd,int x,int y) throws IOException{
		String fileName="";
		String[] content = new AtFileReader(fileAdd).getContain();
		FileWriter fw = new FileWriter(fileAdd + "//export//"+fileName);
		
		
	}

}
