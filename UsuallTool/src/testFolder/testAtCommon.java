package testFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LPT3;
import usualTool.FileFunction;
import usualTool.MathEqualtion.AtMathFunction;
import usualTool.MathEqualtion.Distribution.AtNormalDistribution;

public class testAtCommon {
	private static int threadNum = 4;
	public static String fileAdd = "E:\\QpesumsAnalysis\\RainfallData\\catchment\\";
	public static FileFunction ff = new FileFunction();
	public static String[] fileList = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Double> random = new ArrayList<Double>();
		AtNormalDistribution distribution = new AtNormalDistribution(0,100);
		for(int index = 0 ; index< 500 ; index++) {
			double tempt = distribution.getDoubleRandom();
			if(tempt>0) {
				random.add(tempt);
			}
		}
			
		ReturnPeriod_LPT3 lpt3 = new ReturnPeriod_LPT3(random);
		System.out.println(lpt3.getPeriodRainfall(200));
		
		}
}