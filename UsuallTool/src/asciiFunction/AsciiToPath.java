package asciiFunction;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonObject;

public class AsciiToPath {
	private AsciiBasicControl ascii;
	private String asciiContent[][];
	private Path2D asciiPath = new Path2D.Double();
	private int[][] moving;
	private int startX;
	private int startY;

	public AsciiToPath(String fileAdd) throws IOException {
		this.ascii = new AsciiBasicControl(fileAdd);
		this.asciiContent = this.ascii.getAsciiGrid();
		this.moving = this.setMoveingOrder();
		this.getPathStartPoint();
	}

	public AsciiToPath(AsciiBasicControl ascii) {
		this.ascii = ascii;
		this.asciiContent = this.ascii.getAsciiGrid();
		this.moving = this.setMoveingOrder();
		this.getPathStartPoint();
	}

	/**
	 * 
	 * 
	 * @param startX
	 * @param startY
	 */
	// <===========================================>
	// < set start point>
	// <============================================>
	public void setStartPoint(int startX, int startY) {
		this.startX = startX;
		this.startY = startY;
	}

	public void setStartPoint(double startX, double startY) {
		int[] position = this.ascii.getPosition(startX, startY);
		this.startX = position[0];
		this.startY = position[1];
	}

	private void getPathStartPoint() {
		// get startX
		xDetect: for (int column = 0; column < asciiContent[0].length; column++) {
			for (int row = 0; row < asciiContent.length; row++) {
				if (!this.ascii.getValue(column, row).equals(this.ascii.getProperty().get("noData"))) {
					this.startX = column;
					this.startY = row;
					break xDetect;
				}
			}
		}
	}
	// <=============================================>

	private int[][] setMoveingOrder() {
		int[][] moving = new int[8][2];
		moving[0] = new int[] { 0, 1 };
		moving[1] = new int[] { 1, 1 };
		moving[2] = new int[] { 1, 0 };
		moving[3] = new int[] { 1, -1 };
		moving[4] = new int[] { 0, -1 };
		moving[5] = new int[] { -1, -1 };
		moving[6] = new int[] { -1, 0 };
		moving[7] = new int[] { -1, 1 };

		return moving;
	}
	// <===============================================>

	/**
	 * 
	 * 
	 * @return
	 */
	// <=================================================>
	public Path2D getAsciiPath() {
		// draw startPoint
		this.asciiPath.reset();
		double[] coordinate = this.ascii.getCoordinate(this.startX, this.startY);
		this.asciiPath.moveTo(coordinate[0], coordinate[1]);

		// move to firstPoint
		int[] firstMovedPosition = this.getMovingPosition(this.startX, this.startY, 2);
		int temptX = firstMovedPosition[0];
		int temptY = firstMovedPosition[1];
		int lastMoving = firstMovedPosition[2];
		System.out.println(startX + "\t" + startY);
		System.out.println(temptX + "\t" + temptY);

		while (temptX != this.startX || temptY != this.startY) {
			// moving setting
			int[] moving = this.getMovingPosition(temptX, temptY, lastMoving);
			temptX = moving[0];
			temptY = moving[1];
			lastMoving = moving[2];
			System.out.println(temptX + "\t" + temptY);

			// draw path
			coordinate = this.ascii.getCoordinate(temptX, temptY);
			this.asciiPath.lineTo(coordinate[0], coordinate[1]);
		}

		return this.asciiPath;
	}

	/*
	 */
	// <======================================================>
	// < the common function>
	// <======================================================>
	private int[] getMovingPosition(int temptX, int temptY, int lastMoving) {
		Map<Integer, Boolean> avilablePath = getAvilablePath(temptX, temptY);
		int movingOrder = -1;

		for (int order = 1; order <= 8; order++) {
			int temptMoving = order + (lastMoving + 4);
			temptMoving = temptMoving % 8;

			if (avilablePath.get(temptMoving)) {
				movingOrder = temptMoving;
				break;
			}
		}

		return new int[] { temptX + this.moving[movingOrder][0], temptY + this.moving[movingOrder][1], movingOrder };
	}

	private Map<Integer, Boolean> getAvilablePath(int x, int y) {
		Map<Integer, Boolean> outMap = new TreeMap<Integer, Boolean>();
		String nullString = this.ascii.getProperty().get("noData");

		for (int index = 0; index < 8; index++) {
			int temptX = x + this.moving[index][0];
			int temptY = y + this.moving[index][1];
			try {
				if (!this.asciiContent[temptY][temptX].equals(nullString)) {
					outMap.put(index, true);
				} else {
					outMap.put(index, false);
				}
			} catch (Exception e) {
				outMap.put(index, false);
			}
		}

		return outMap;
	}
	// <===============================================================>
	// <===============================================================>

}
