package usualTool.MathEqualtion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AtLeastSquareMatrix {

	// V(error) = A(coefficient) * X(variables) - L(constant)
	// N = At * W * A (w = weight)
	// U = At * W * L(w = weight)
	// X = N^-1 * U

	private List<Double[]> matrixA = new ArrayList<>();
	private List<Double> equaltionWeight = new ArrayList<>();
	private List<Double> eqaulttionL = new ArrayList<>();
	private int variableNumber;

	public AtLeastSquareMatrix(int variableNumber) {
		this.variableNumber = variableNumber;
	}

	public void addEqualtion(List<Double> A, double L) {
		addEqualtion(A, L, 1);
	}

	public void addEqualtion(List<Double> A, double L, double W) {
		if (this.variableNumber != A.size()) {
			new Exception("variableNumber not match skip this equaltion");
		} else {
			this.matrixA.add(A.parallelStream().toArray(Double[]::new));
			this.eqaulttionL.add(L);
			this.equaltionWeight.add(W);
		}
	}

	public void addEqualtion(Double[] A, double L, double W) {
		addEqualtion(new ArrayList<>(Arrays.asList(A)), L, W);
	}

	public void addEqualtion(Double[] A, double L) {
		addEqualtion(new ArrayList<>(Arrays.asList(A)), L, 1);
	}

	public List<Double> getVariablesValue() {

		if (this.matrixA.size() < this.variableNumber) {
			new Exception("no enough equaltions, return null");
			return null;
		} else {

			// create matrix A
			Double[][] matrixA = this.matrixA.parallelStream().toArray(Double[][]::new);

			// create matrix A^t
			Double[][] matrixAt = AtMatrix.trans(matrixA);

			// create matrix W
			Double[][] matrixW = new Double[this.equaltionWeight.size()][this.equaltionWeight.size()];
			for (int row = 0; row < this.equaltionWeight.size(); row++) {
				for (int column = 0; column < this.equaltionWeight.size(); column++) {
					if (row == column) {
						matrixW[row][column] = this.equaltionWeight.get(row);
					} else {
						matrixW[row][column] = 0.;
					}
				}
			}

			// create matrix L
			Double[][] matrixL = new Double[this.eqaulttionL.size()][1];
			for (int row = 0; row < this.eqaulttionL.size(); row++) {
				matrixL[row][0] = this.eqaulttionL.get(row);
			}

			// create matrix N^-1
			Double[][] matrixNInverse = AtMatrix.inverse(AtMatrix.mult(AtMatrix.mult(matrixAt, matrixW), matrixA));

			// create matrix U
			Double[][] matrixU = AtMatrix.mult(AtMatrix.mult(matrixAt, matrixW), matrixL);

			// create matrix X
			Double[][] matrixX = AtMatrix.mult(matrixNInverse, matrixU);

			// return variables
			List<Double> variables = new ArrayList<>();
			for (Double[] x : matrixX) {
				variables.add(x[0]);
			}
			return variables;
		}
	}

}
