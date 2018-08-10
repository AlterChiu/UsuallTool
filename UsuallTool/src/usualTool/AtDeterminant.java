package usualTool;

import java.util.ArrayList;
import java.util.List;

public class AtDeterminant {
	public static double getValue(Double[][] matrix) {
		if (matrix.length == 0 || matrix.length != matrix[0].length) {
			System.out.println("error dimention of this matrix");
			return 0;
		} else if (matrix.length == 1) {
			System.out.println("one dimention matrix");
			return matrix[0][0];
		} else {
			return checkMatrix(matrix);
		}
	}

	private static double checkMatrix(Double[][] matrix) {
		double outValue = 0;
		if (matrix.length > 2) {
			for (int down = 0; down < matrix.length; down++) {

				// low down the matrix
				// <===============================>

				// get the low down variable
				double temptValue = matrix[down][0];
				if (down % 2 != 0) {
					temptValue = temptValue * -1;
				}

				// get the low down matrix
				List<Double[]> downMatrix = new ArrayList<Double[]>();
				for (int row = 0; row < matrix.length; row++) {
					List<Double> rowValue = new ArrayList<Double>();
					if (row != down) {
						for (int column = 1; column < matrix.length; column++) {
							rowValue.add(matrix[row][column]);
						}
						downMatrix.add(rowValue.parallelStream().toArray(Double[]::new));
					}
				}
				outValue = outValue + temptValue * checkMatrix(downMatrix.parallelStream().toArray(Double[][]::new));
			}
		} else {
			outValue = getDeterminant(matrix);
		}
		return outValue;
	}

	private static double getDeterminant(Double[][] matrix) {
		return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
	}
}
