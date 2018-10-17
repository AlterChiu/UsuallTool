package usualTool.MathEqualtion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AtCramerEqualtion {
	private Double[][] matrix;
	private int equaltions;

	// <=============================>
	// <THIS IS CONSTRUCTUR>
	// <=============================>
	public AtCramerEqualtion(Double[][] matrix) {
		this.matrix = matrix;
		this.equaltions = matrix.length;
		double variable = matrix[0].length;

		if (equaltions != (variable - 1)) {
			System.out.println("error in number of variables and equaltions");
			System.out.println("variable : " + (variable - 1));
			System.out.println("equaltions : " + equaltions);
		}

	}
	// <=============================>

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
					judgeList.add(new BigDecimal(coefficients[coefficientIndex] - temptCoefficients[coefficientIndex])
							.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
				}

				if (judgeList.size() == 1) {
					matrixList.remove(detect);
					detect = detect - 1;
					judgement = true;
				}
			}
		}
		
		this.matrix = matrixList.parallelStream().toArray(Double[][]::new);
		this.equaltions = matrix.length;
		
		return judgement;
	}

	public List<Double> getVariables() {
		List<Double> variableList = new ArrayList<Double>();

		Double[][] deltaMatrix = getCramerMatrix(this.equaltions);
		for (int variable = 0; variable < this.equaltions; variable++) {
			variableList.add(AtDeterminant.getValue(getCramerMatrix(variable)) / AtDeterminant.getValue(deltaMatrix));
		}
		return variableList;
	}

	private Double[][] getCramerMatrix(int index) {
		List<Double[]> outList = new ArrayList<Double[]>();

		for (int row = 0; row < equaltions; row++) {
			List<Double> temptRow = new ArrayList<Double>();
			for (int column = 0; column < equaltions; column++) {
				if (column == index) {
					temptRow.add(this.matrix[row][equaltions]);
				} else {
					temptRow.add(this.matrix[row][column]);
				}
			}
			outList.add(temptRow.parallelStream().toArray(Double[]::new));
		}
		return outList.parallelStream().toArray(Double[][]::new);
	}

}
