package testFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import Hydro.Rainfall.ReturnPeriod.ReturnTest;
import usualTool.AtCommonMath;
import usualTool.MathEqualtion.Distribution.AtDistribution;
import usualTool.MathEqualtion.Distribution.AtNormalDistribution;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method

		AtDistribution di = new AtNormalDistribution(0,1);
		System.out.println(di.getCumulative(0.24793052673339844));
	}
}