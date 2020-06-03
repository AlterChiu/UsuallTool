package usualTool.MathEqualtion;

import java.math.BigDecimal;
import java.util.Random;

public class RandomMaker {
	private Random ran = new Random();
	private long timeSeed;

	public RandomMaker() {
		timeSeed = System.currentTimeMillis();
		ran.setSeed(timeSeed);
	}

	public RandomMaker newSeed() {
		timeSeed = System.currentTimeMillis();
		ran.setSeed(timeSeed);
		return this;
	}

	public int RandomInt(int start, int end) {
		int temptI = new BigDecimal(start + (end + 1 - start) * ran.nextDouble()).setScale(0, BigDecimal.ROUND_DOWN)
				.intValue();
		return temptI;
	}

	public String RandomDoubleFormate(double start, double end, int pre) {
		// (##.##)
		double temptI = start + (end - start) * ran.nextDouble();
		return new BigDecimal(temptI + "").setScale(pre).toString();
	}

	public double RandomDouble(double start, double end) {
		double temptI = start + (end - start) * ran.nextDouble();
		return temptI;
	}
}
