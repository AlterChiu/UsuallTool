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
		if (startMatrix[0].length != matrix.length) {
			System.out.println("matrix mlut not match");
		} else {
			List<Double[]> matrixValue = new ArrayList<>();

			// base on start matrix
			for (int startMatrixRow = 0; startMatrixRow < matrix.length; startMatrixRow++) {
				List<Double> rowValue = new ArrayList<>();

				for (int multMaxtrixColumn = 0; multMaxtrixColumn < startMatrix.length; multMaxtrixColumn++) {
					List<Double> temptValue = new ArrayList<>();
					for (int multMaxtrixRow = 0; multMaxtrixRow < matrix.length; multMaxtrixRow++) {
						temptValue.add(startMatrix[startMatrixRow][multMaxtrixRow]
								* matrix[multMaxtrixRow][multMaxtrixColumn]);
					}
					rowValue.add(new AtCommonMath(temptValue).getSum());
				}
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			this.startMatrix = matrixValue.parallelStream().toArray(Double[][]::new);
		}
		return this;
	}
	
	public AtMatrix add(List<Double[]> matrix) {
		this.add(matrix.parallelStream().toArray(Double[][]::new));
		return this;
	}

	public AtMatrix add(Double[][] matrix) {
		if (this.startMatrix.length != matrix.length || this.startMatrix[0].length != matrix[0].length) {
			System.out.println("matrix add not match");
		} else {
			List<Double[]> matrixValue = new ArrayList<>();

			for (int row = 0; row < matrix.length; row++) {
				List<Double> rowValue = new ArrayList<>();
				for (int column = 0; column < matrix[0].length; column++) {
					rowValue.add(matrix[row][column] + this.startMatrix[row][column]);
				}
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			this.startMatrix = matrixValue.parallelStream().toArray(Double[][]::new);
		}
		return this;
	}
	
	public AtMatrix minus(List<Double[]> matrix) {
		this.add(matrix.parallelStream().toArray(Double[][]::new));
		return this;
	}

	public AtMatrix minus(Double[][] matrix) {
		if (this.startMatrix.length != matrix.length || this.startMatrix[0].length != matrix[0].length) {
			System.out.println("matrix minus not match");
		} else {
			List<Double[]> matrixValue = new ArrayList<>();

			for (int row = 0; row < matrix.length; row++) {
				List<Double> rowValue = new ArrayList<>();
				for (int column = 0; column < matrix[0].length; column++) {
					rowValue.add(matrix[row][column] - this.startMatrix[row][column]);
				}
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			this.startMatrix = matrixValue.parallelStream().toArray(Double[][]::new);
		}
		return this;
	}


	public AtMatrix trans() {
		List<Double[]> matrixValue = new ArrayList<>();

		for (int column = 0; column < startMatrix[0].length; column++) {
			List<Double> rowValue = new ArrayList<>();

			for (int row = 0; row < startMatrix.length; row++) {
				rowValue.add(startMatrix[row][column]);
			}
			matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
		}
		this.startMatrix = matrixValue.parallelStream().toArray(Double[][]::new);
		return this;
	}

	public AtMatrix inverse() {
		if (this.startMatrix.length < 2 || this.startMatrix.length != this.startMatrix[0].length) {
			System.out.println("matrix inverse not match");
		} else {
			List<Double[]> matrixValue = new ArrayList<>();
			double constant = 1. / AtDeterminant.getValue(this.startMatrix);

			// for the outside matrix
			for (int row = 0; row < startMatrix.length; row++) {
				List<Double> rowValue = new ArrayList<>();
				for (int column = 0; column < startMatrix[0].length; column++) {

					// for the inside matrix
					List<Double[]> temptMatrix = new ArrayList<>();
					for (int scanRow = 0; scanRow < this.startMatrix.length; scanRow++) {
						List<Double> temptRow = new ArrayList<>();
						for (int scanColumn = 0; scanColumn < this.startMatrix[0].length; scanColumn++) {
							if (scanRow != row && scanColumn != column) {
								temptRow.add(this.startMatrix[scanRow][scanColumn]);
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
				matrixValue.add(rowValue.parallelStream().toArray(Double[]::new));
			}
			this.startMatrix = matrixValue.parallelStream().toArray(Double[][]::new);
		}
		return this;
	}

	public Double[][] getMatrix() {
		return this.startMatrix;
	}

}
