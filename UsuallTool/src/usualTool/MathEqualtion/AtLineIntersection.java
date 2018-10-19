package usualTool.MathEqualtion;

import java.math.BigDecimal;

public class AtLineIntersection {
	/*
	 * a1(X) + b1(Y) + c1 = 0
	 * 
	 */

	private double a1 = 0;
	private double b1 = 0;
	private double c1 = 0;

	private double a2 = 0;
	private double b2 = 0;
	private double c2 = 0;

	/**
	 * 
	 * 
	 * @param equaltion1
	 * @param equaltion2
	 */
	// <==============================================================>
	// < Contructor >
	// <==============================================================>
	public AtLineIntersection(Double[] equaltion1, Double[] equaltion2) {
		this.a1 = equaltion1[0];
		this.b1 = equaltion1[1];
		this.c1 = equaltion1[2];

		this.a2 = equaltion2[0];
		this.b2 = equaltion2[1];
		this.c2 = equaltion2[2];
	}

	public AtLineIntersection(double coefficientA1, double coefficientB1, double coefficientC1, double coefficientA2,
			double coefficientB2, double coefficientC2) {
		this.a1 = coefficientA1;
		this.b1 = coefficientB1;
		this.c1 = coefficientC1;

		this.a2 = coefficientA2;
		this.b2 = coefficientB2;
		this.c2 = coefficientC2;
	}

	public AtLineIntersection(double slope1, double intercept1, double slope2, double intercept2) {
		this.a1 = slope1 * -1;
		this.c1 = intercept1 * -1;

		this.a2 = slope2 * -1;
		this.c2 = intercept2 * -1;
	}

	public AtLineIntersection(double coefficientA1, double coefficientB1, double coefficientC1, Double[] equaltion2) {
		this.a1 = coefficientA1;
		this.b1 = coefficientB1;
		this.c1 = coefficientC1 * -1;

		this.a2 = equaltion2[0];
		this.b2 = equaltion2[1];
		this.c2 = equaltion2[2];
	}

	public AtLineIntersection(double slope1, double intercept1, Double[] equaltion2) {
		this.a1 = slope1 * -1;
		this.c1 = intercept1 * -1;

		this.a2 = equaltion2[0];
		this.b2 = equaltion2[1];
		this.c2 = equaltion2[2];
	}

	public AtLineIntersection(double coefficientA1, double coefficientB1, double coefficientC1, double slope2,
			double intercept2) {
		this.a1 = coefficientA1;
		this.b1 = coefficientB1;
		this.c1 = coefficientC1;

		this.a2 = slope2 * -1;
		this.c2 = intercept2 * -1;
	}
	// <=======================================================================>

	public Boolean isIntersect() {
		double determinant = AtDeterminant.getValue(new Double[][] { { a1, b1 }, { a2, b2 } });
		if (Math.abs(determinant) < 0.00001) {
			return false;
		} else {
			return true;
		}
	}

	public double[] getIntersect() {
		if (isIntersect()) {
			Double[] variables = new AtCramerEqualtion(new Double[][] { { a1, b1, -1 * c1 }, { a2, b2, -1 * c2 } })
					.getVariables().parallelStream().toArray(Double[]::new);
			return new double[] { variables[0], variables[1] };
		} else {
			System.out.println("not intersect");
			return null;
		}
	}

}
