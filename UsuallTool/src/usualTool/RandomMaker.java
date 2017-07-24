package usualTool;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

public class RandomMaker {
	private Random ran = new Random();
	private static long timeSeed = System.currentTimeMillis();
	
	
	public RandomMaker(){
		ran.setSeed(timeSeed);
	}

	public int RandomInt(int start, int end) {
		int temptI = start + (end - start) * ran.nextInt();
		return temptI;
	}

	public String RandomDoubleFormate(double start, int end, int pre) {
		// (##.##)
		double temptI = start + (end - start) * ran.nextDouble();
		return new BigDecimal(temptI+"").setScale(pre).toString();
	}

	public double RandomDouble(double start, int end) {	
		double temptI = start + (end - start) * ran.nextDouble();
		return temptI;
	}
}
