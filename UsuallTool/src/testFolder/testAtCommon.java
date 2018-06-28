package testFolder;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;

import asciiFunction.AsciiBasicControl;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String fileAdd = new File(".").getAbsolutePath();
		double x = 0.;
		double y = 0.;
		try {
			fileAdd = args[0];
			x = Double.parseDouble(args[1]);
			y = Double.parseDouble(args[2]);
		}catch(Exception e) {
			System.out.println("error variable = asciiFile , x , y");
		}
		
		AsciiBasicControl ascii = new AsciiBasicControl(fileAdd);
		System.out.println(ascii.getValue(x, y));
		
	}
}