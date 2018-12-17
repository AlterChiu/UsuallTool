package testFolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import asciiFunction.AsciiBasicControl;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.MathEqualtion.Distribution.AtChiSquareChecking;
import usualTool.MathEqualtion.Distribution.AtDistribution;
import usualTool.MathEqualtion.Distribution.AtGammaDistribution;
import usualTool.MathEqualtion.Distribution.AtGumbelDistribution;
import usualTool.MathEqualtion.Distribution.AtLogGammaDistribution;
import usualTool.MathEqualtion.Distribution.AtLogNormalDistribuition;
import usualTool.MathEqualtion.Distribution.AtNormalDistribution;

public class testAtCommon {
	public static Map<String, String> idMapping;
	public static String[][] stationList;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method

		AtDistribution distribution = new AtGumbelDistribution(1, 1);

		List<Double> valueList = setValeList();
//		List<Double> valueList = new ArrayList<Double>();
//		for (int index = 0; index < 10; index++) {
//			valueList.add(distribution.getDoubleRandom());
//		}
//		valueList.forEach(e -> System.out.println(e));
		System.out
				.println(new AtCommonMath(valueList.parallelStream().mapToDouble(e -> Math.log(e)).toArray()).getStd());

		AtChiSquareChecking chiSquare = new AtChiSquareChecking(valueList);
		chiSquare.groupListInitialize(5);

		AtGumbelDistribution gumble = new AtGumbelDistribution(valueList);
		System.out.println("gumble : \t" + chiSquare.getchiSquareValue(gumble));

		AtGammaDistribution Gamma = new AtGammaDistribution(valueList);
		System.out.println("Gamma : \t" + chiSquare.getchiSquareValue(Gamma));

		AtNormalDistribution normal = new AtNormalDistribution(valueList);
		System.out.println("normal : \t" + chiSquare.getchiSquareValue(normal));

		AtLogGammaDistribution logGamma = new AtLogGammaDistribution(valueList);
		System.out.println("logGamma : \t" + chiSquare.getchiSquareValue(logGamma));

		AtLogNormalDistribuition logNormal = new AtLogNormalDistribuition(valueList);
		System.out.println("logNormal : \t" + chiSquare.getchiSquareValue(logNormal));

	}

	private static List<Double> setValeList() {
		List<Double> valueList = new ArrayList<Double>();
		valueList.add(1.337);
		valueList.add(5.35);
		valueList.add(3.053);
		valueList.add(3.604);
		valueList.add(1.177);
		valueList.add(2.213);
		valueList.add(0.139);
		valueList.add(7.233);
		valueList.add(-0.161);
		valueList.add(0.731);
		return valueList;
	}
}