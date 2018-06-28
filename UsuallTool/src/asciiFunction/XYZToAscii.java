package asciiFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class XYZToAscii {
	private TreeMap<String, String> property = new TreeMap<String, String>();
	private ArrayList<String[]> xyzContent;
	private ArrayList<Double> xList = new ArrayList<Double>();
	private ArrayList<Double> yList = new ArrayList<Double>();
	private TreeMap<String, String> valueList = new TreeMap<String, String>();
	private String[][] asciiGrid;
	private int row;
	private int column;
	private double cellSize = 1.0;
	private String noData = "-99";

	public XYZToAscii(String fileAdd) throws IOException {
		this.xyzContent = new ArrayList<String[]>(Arrays.asList(new AtFileReader(fileAdd).getStr()));
		this.setProperty();
		this.setGrid();
	}

	public TreeMap<String, String> getProperty() {
		return this.property;
	}

	public String[][] getAsciiGrid() {
		return this.asciiGrid;
	}
	
	public String[][] getAscii(){
		ArrayList<String[]> outArray = new ArrayList<String[]>();
		outArray.add(new String[] {"ncols" ,  this.property.get("column")});
		outArray.add(new String[] {"nrows" ,  this.property.get("row")});
		outArray.add(new String[] {"xllCenter" ,  this.property.get("bottomX")});
		outArray.add(new String[] {"yllCenter" ,  this.property.get("bottomY")});
		outArray.add(new String[] {"cellsize" ,  this.property.get("cellSize")});
		outArray.add(new String[] {"nodata_value" ,  this.property.get("noData")});
		
		Arrays.asList(this.asciiGrid).forEach(line -> outArray.add(line));
		return outArray.parallelStream().toArray(String[][]::new);
	}
	
	public void saveAscii(String fileAdd) throws IOException {
		new AtFileWriter(this.getAscii() , fileAdd).textWriter("    ");
	}

	private void setGrid() {
		System.out.println("setGrid");
		double startX = Double.parseDouble(this.property.get("bottomX"));
		double startY = Double.parseDouble(this.property.get("topY"));
		
		ArrayList<String[]> outArray = new ArrayList<String[]>();
		for (int row = 0; row < this.row; row++) {
			String temptY = new BigDecimal(startY - this.cellSize * row).setScale(3, BigDecimal.ROUND_HALF_UP)
					.toString();
			ArrayList<String> tempArray = new ArrayList<String>();
			for (int column = 0; column < this.column; column++) {
				String temptX = new BigDecimal(startX + this.cellSize * column).setScale(3, BigDecimal.ROUND_HALF_UP)
						.toString();
				String temptPosition = temptX + "_" + temptY;
				if (this.valueList.containsKey(temptPosition)) {
					tempArray.add(this.valueList.get(temptPosition));
				} else {
					tempArray.add(this.noData);
				}
			}
			outArray.add(tempArray.parallelStream().toArray(String[]::new));
		}
		this.asciiGrid = outArray.parallelStream().toArray(String[][]::new);
	}

	private void setProperty() {
		
		this.xyzContent.forEach(line -> {
			String x = new BigDecimal(line[0]).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
			String y = new BigDecimal(line[1]).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
			this.xList.add(Double.parseDouble(x));
			this.yList.add(Double.parseDouble(y));
			this.valueList.put(x + "_" + y, line[2]);
		});
		this.xyzContent.clear();
		

		System.out.println("set boundary");
		AtCommonMath xStastic = new AtCommonMath(this.xList);
		double bottomX = xStastic.getMin();
		double topX = xStastic.getMax();
		this.xList.clear();

		AtCommonMath yStastic = new AtCommonMath(this.yList);
		double bottomY = yStastic.getMin();
		double topY = yStastic.getMax();
		this.yList.clear();

		int row = new BigDecimal((topY - bottomY) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int column = new BigDecimal((topX - bottomX) / this.cellSize).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();

		System.out.println("setProperty");
		property.put("bottomX", bottomX + "");
		property.put("topX", topX + "");
		property.put("bottomY", bottomY + "");
		property.put("topY", topY + "");
		property.put("noData", noData);
		property.put("cellSize", new BigDecimal(this.cellSize).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
		property.put("column", topY + "");
		property.put("row", topY + "");
		property.put("column", column + "");
		property.put("row", row + "");

		this.row = row;
		this.column = column;
	}

	public XYZToAscii setCellSize(double cellSize) {
		this.cellSize = cellSize;
		return this;
	}
}
