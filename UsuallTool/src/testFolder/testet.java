package testFolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import asciiFunction.AsciiBasicControl;
import usualTool.AtFileWriter;

public class testet {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileAdd = "E:/GraduatedPaper/Picture/tainan/splitMerge/0/roughDem_0.asc";
		String saveAdd = "E:/GraduatedPaper/Picture/tainan/splitMerge/0/roughDem_0_1.asc";

		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
		Map<String,Double> boundary = ascii.getBoundary();
		boundary.put("minX", 167564.5);
		boundary.put("maxX",168402.5);
		
		new AtFileWriter(ascii.getClipAsciiFile(boundary).getAsciiFile() , saveAdd).textWriter(" ");

	}

}
