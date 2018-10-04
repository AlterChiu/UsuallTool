package asciiFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class AsciiBuffer {
	private AsciiBasicControl asciiControl;
	private ArrayList<Double[]> bufferCenter = new ArrayList<Double[]>();
	private TreeMap<String, String> property;
	private double cellSize;
	private String noData = "null";

	// <====================================>
	// < CONSTRUCTOR >
	// <====================================>
	public AsciiBuffer(String fileAdd) throws IOException {
		this.asciiControl = new AsciiBasicControl(fileAdd);
		this.property = this.asciiControl.getProperty();
		this.cellSize = Double.parseDouble(this.property.get("cellSize"));
		this.noData = this.property.get("noData");
	}

	public AsciiBuffer(String[][] ascii) throws IOException {
		this.asciiControl = new AsciiBasicControl(ascii);
		this.property = this.asciiControl.getProperty();
		this.cellSize = Double.parseDouble(this.property.get("cellSize"));
		this.noData = this.property.get("noData");
	}
	// =============================================

	// <======================>
	// < setting >
	// <======================>
	public AsciiBuffer setPoint(double x, double y) {
		this.bufferCenter.add(new Double[] { x, y });
		return this;
	}
	// ==============================================

	// <========================>
	// < FUNCTION >
	// <========================>
	public String[][] getSelectBufferAscii(double buffer) throws IOException {
		ArrayList<String[][]> selectedAscii = new ArrayList<String[][]>();
		for (Double[] position : bufferCenter) {
			double minX = position[0] - buffer;
			double maxX = position[0] + buffer;
			double maxY = position[1] + buffer;
			double minY = position[1] - buffer;

			String[][] temptAscii = this.asciiControl.getClipAsciiFile(minX, minY, maxX, maxY);
			AsciiBasicControl temptAsciiControl = new AsciiBasicControl(temptAscii);
			String temptGrid[][] = temptAsciiControl.getAsciiGrid();
			for (int row = 0; row < temptGrid.length; row++) {
				for (int column = 0; column < temptGrid[0].length; column++) {
					double dis = getDistance(temptAsciiControl.getCoordinate(column, row), position);
					if (dis > buffer) {
						temptAsciiControl.setValue(column, row, noData);
					}
				}
			}
			selectedAscii.add(temptAsciiControl.getAsciiFile());
		}
		String[][] temptOut = selectedAscii.get(0);
		for (int index = 1; index < selectedAscii.size(); index++) {
			temptOut = new AsciiMerge(temptOut, selectedAscii.get(index)).getMergeAsciiArray();
		}
		return temptOut;
	}

	private double getDistance(double[] point1, Double[] point2) {
		return Math.sqrt(Math.pow(point1[0] - point2[0], 2.) + Math.pow(point1[1] - point2[1], 2));
	}

	public String[][] getSelecBufferAscii(int bufferGrid) throws IOException {
		ArrayList<String[][]> selectedAscii = new ArrayList<String[][]>();
		for (Double[] position : bufferCenter) {
			double minX = position[0] - this.cellSize * bufferGrid;
			double maxX = position[0] + this.cellSize * bufferGrid;
			double maxY = position[1] + this.cellSize * bufferGrid;
			double minY = position[1] - this.cellSize * bufferGrid;

			selectedAscii.add(this.asciiControl.getClipAsciiFile(minX, minY, maxX, maxY));
		}
		String[][] temptOut = selectedAscii.get(0);
		for (int index = 1; index < selectedAscii.size(); index++) {
			temptOut = new AsciiMerge(temptOut, selectedAscii.get(index)).getMergeAsciiArray();
		}
		return temptOut;
	}

}
