package usualTool;

import java.util.ArrayList;
import java.util.List;

public class AtCramerEqualtion {
	private Double[][] matrix;
	private int equaltions;

	//<=============================>
	//<THIS IS CONSTRUCTUR>
	//<=============================>
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
	//<=============================>

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
