package usualTool.MathEqualtion;

public class AtMathFunction {
	static public double StandardDeviation(double posible) {
		double C0 = 2.515517;
		double C1 = 0.802853;
		double C2 = 0.010328;
		double D1 = 1.432788;
		double D2 = 0.189269;
		double D3 = 0.001308;
		double w = 0;
		double t = 0;

		if (posible < 0.5 && posible > 0) {
			w = Math.sqrt(-2 * Math.log(posible));
			t = w - (C0 + C1 * w + C2 * w * w) / (1 + D1 * w + D2 * w * w + D3 * w * w * w);
		} else if (posible >= 0.5) {
			w = Math.sqrt(-2 * Math.log(1 - posible));
			t = -1 * w + (C0 + C1 * w + C2 * w * w) / (1 + D1 * w + D2 * w * w + D3 * w * w * w);
		} else {
			System.out.println("error while common StandarDeviation ,posible under 0");
		}
		return t;
	}

}
