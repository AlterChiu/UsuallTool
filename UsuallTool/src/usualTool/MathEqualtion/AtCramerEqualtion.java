package usualTool.MathEqualtion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import usualTool.AtCommonMath;

public class AtCramerEqualtion {
	private Double[][] matrix;
	private int equaltionsNumber;

	// ax + by = c the matrix will be
	// a1 b1 c1
	// a2 b2 c2

	// <=============================>
	// <THIS IS CONSTRUCTUR>
	// <=============================>
	public AtCramerEqualtion(Double[][] matrix) {
		this.matrix = matrix;
		this.equaltionsNumber = matrix.length;
		double variable = matrix[0].length;

		if (equaltionsNumber != (variable - 1)) {
			System.out.println("error in number of variables and equaltions");
			System.out.println("variable : " + (variable - 1));
			System.out.println("equaltions : " + equaltionsNumber);
		}

	}
	// <=============================>

	// check for, is there any equation that equals to other one
	// if x1 + y1 = c1 equals to x2 + y2 = c2 return true
	public Boolean isExistSameEqualtion() {
		Boolean judgement = false;
		List<Double[]> matrixList = new ArrayList<Double[]>(Arrays.asList(matrix));

		for (int index = 0; index < matrixList.size() - 1; index++) {
			Double[] coefficients = matrixList.get(index);

			for (int detect = index + 1; detect < matrixList.size(); detect++) {
				Double[] temptCoefficients = matrixList.get(detect);

				// get the magnification of the
				Set<Double> judgeList = new TreeSet<Double>();
				for (int coefficientIndex = 0; coefficientIndex < coefficients.length; coefficientIndex++) {
					judgeList.add(AtCommonMath.getDecimal_Double(
							coefficients[coefficientIndex] - temptCoefficients[coefficientIndex], 4));
				}

				if (judgeList.size() == 1) {
					matrixList.remove(detect);
					detect = detect - 1;
					judgement = true;
				}
			}
		}

		this.matrix = matrixList.parallelStream().toArray(Double[][]::new);
		this.equaltionsNumber = this.matrix.length;

		return judgement;
	}

	// get the answer of variables which return by input order
	public List<Double> getVariables() {
		List<Double> variableList = new ArrayList<Double>();

		Double[][] deltaMatrix = getCramerMatrix(this.equaltionsNumber);
		for (int variable = 0; variable < this.equaltionsNumber; variable++) {
			variableList.add(AtDeterminant.getValue(getCramerMatrix(variable)) / AtDeterminant.getValue(deltaMatrix));
		}
		return variableList;
	}

	// cramerMatrix => of matrix A(x,y,constant)
	// x = determinant(A), which x column switch by constant value
	// the input variable "index" mean, which variable are calculated now
	private Double[][] getCramerMatrix(int index) {
		List<Double[]> outList = new ArrayList<Double[]>();

		for (int row = 0; row < this.equaltionsNumber; row++) {
			List<Double> temptRow = new ArrayList<Double>();
			for (int column = 0; column < this.equaltionsNumber; column++) {
				if (column == index) {
					temptRow.add(this.matrix[row][this.equaltionsNumber]);
				} else {
					temptRow.add(this.matrix[row][column]);
				}
			}
			outList.add(temptRow.parallelStream().toArray(Double[]::new));
		}
		return outList.parallelStream().toArray(Double[][]::new);
	}

}
