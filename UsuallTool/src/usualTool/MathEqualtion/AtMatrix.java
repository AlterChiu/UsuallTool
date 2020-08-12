package usualTool.MathEqualtion;

import java.util.ArrayList;
import java.util.List;

import usualTool.AtCommonMath;

public class AtMatrix {
	private Double[][] startMatrix;

	public AtMatrix(Double[][] startMatrix) {
		this.startMatrix = startMatrix;
	}

	public AtMatrix(List<Double[]> startMatrix) {
		this.startMatrix = startMatrix.parallelStream().toArray(Double[][]::new);
	}

	public AtMatrix mult(List<Double[]> matrix) {
		this.mult(matrix.parallelStream().toArray(Double[][]::new));
		return this;
	}

	public AtMatrix mult(Double[][] matrix) {
		this.startMatrix = AtMatrix.mult(this.startMatrix, matrix);
		return this;
	}

	public AtMatrix add(List<Double[]> matrix) {
		this.add(matrix.parallelStream().toArray(Double[][]::new));
		return this;
	}

	public AtMatrix add(Double[][] matrix) {
		this.startMatrix = AtMatrix.add(this.startMatrix, matrix);
		return this;
	}

	public AtMatrix minus(List<Double[]> matrix) {
		this.add(matrix.parallelStream().toArray(Double[][]::new));
		return this;
	}

	public AtMatrix minus(Double[][] matrix) {
		this.startMatrix = AtMatrix.minus(this.startMatrix, matrix);
		return this;
	}

	public AtMatrix trans() {
		this.startMatrix = AtMatrix.trans(this.startMatrix);
		return this;
	}

	public AtMatrix inverse() {
		this.startMatrix = AtMatrix.inverse(this.startMatrix);
		return this;
	}

	public Double[][] getMatrix() {
		return this.startMatrix;
	}

	// return A^-1
	public static Double[][] inverse(Double[][] matrix) {

		if (matrix.length != matrix[0].length) {
			new Exception("matrix inverse not match");
			return null;

		} else if (matrix.length == 1) {
			return new Double[][] { { 1. / matrix[0][0] } };

		} else {
			List<Double[]> outputMatrix = new ArrayList<>();
			double constant = 1. / AtDeterminant.getValue(matrix);

			// for the outside matrix
			for (int row = 0; row < matrix.length; row++) {
				List<Double> rowValue = new ArrayList<>();
				for (int column = 0; column < matrix[0].length; column++) {

					// for the inside matrix
					List<Double[]> temptMatrix = new ArrayList<>();
					for (int scanRow = 0; scanRow < matrix.length; scanRow++) {
						List<Double> temptRow = new ArrayList<>();
						for (int scanColumn = 0; scanColumn < matrix[0].length; scanColumn++) {
							if (scanRow != row && scanColumn != column) {
								temptRow.add(matrix[scanRow][scanColumn]);
							}
						}
						if (temptRow.size() > 0) {
							temptMatrix.add(temptRow.parallelStream().toArray(Double[]::new));
						}
					}

					// for the outer matrix
					rowValue.add(
							constant * AtDeterminant.getValue(temptMatrix.parallelStream().toArray(Double[][]::new)));
				}
				outputMatrix.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			return outputMatrix.parallelStream().toArray(Double[][]::new);
		}
	}

	// return A^t
	public static Double[][] trans(Double[][] matrix) {
		List<Double[]> outputMatrix = new ArrayList<>();

		for (int column = 0; column < matrix[0].length; column++) {
			List<Double> rowValue = new ArrayList<>();

			for (int row = 0; row < matrix.length; row++) {
				rowValue.add(matrix[row][column]);
			}
			outputMatrix.add(rowValue.parallelStream().toArray(Double[]::new));
		}
		return outputMatrix.parallelStream().toArray(Double[][]::new);
	}

	// return A*B
	public static Double[][] mult(Double[][] matrixA, Double[][] matrixB) {

		if (matrixA[0].length != matrixB.length) {
			new Exception("matrix mlut not match");
			return null;
		} else {
			List<Double[]> matrixValue = new ArrayList<>();

			// base on start matrix
			for (int startMatrixRow = 0; startMatrixRow < matrixA.length; startMatrixRow++) {
				List<Double> rowValue = new ArrayList<>();

				for (int multMaxtrixColumn = 0; multMaxtrixColumn < matrixB[0].length; multMaxtrixColumn++) {
					List<Double> temptValue = new ArrayList<>();
					for (int multMaxtrixRow = 0; multMaxtrixRow < matrixB.length; multMaxtrixRow++) {
						temptValue.add(
								matrixA[startMatrixRow][multMaxtrixRow] * matrixB[multMaxtrixRow][multMaxtrixColumn]);
					}
					rowValue.add(new AtCommonMath(temptValue).getSum());
				}
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			return matrixValue.parallelStream().toArray(Double[][]::new);
		}
	}

	// return A+B
	public static Double[][] add(Double[][] matrixA, Double[][] matrixB) {
		if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
			new Exception("matrix add not match");
			return null;
		} else {
			List<Double[]> matrixValue = new ArrayList<>();

			for (int row = 0; row < matrixB.length; row++) {
				List<Double> rowValue = new ArrayList<>();
				for (int column = 0; column < matrixB[0].length; column++) {
					rowValue.add(matrixB[row][column] + matrixA[row][column]);
				}
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			return matrixValue.parallelStream().toArray(Double[][]::new);
		}
	}

	// return A-B
	public static Double[][] minus(Double[][] matrixA, Double[][] matrixB) {
		if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
			new Exception("matrix add not match");
			return null;
		} else {
			List<Double[]> matrixValue = new ArrayList<>();

			for (int row = 0; row < matrixB.length; row++) {
				List<Double> rowValue = new ArrayList<>();
				for (int column = 0; column < matrixB[0].length; column++) {
					rowValue.add(matrixA[row][column] - matrixB[row][column]);
				}
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			return matrixValue.parallelStream().toArray(Double[][]::new);
		}
	}

}
