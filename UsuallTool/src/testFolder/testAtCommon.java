package testFolder;

import java.awt.geom.Path2D;
import java.io.FileWriter;

import Microsoft.Office.Excel.Chart.ExcelChart;
import Microsoft.Office.PowerPoint.PPTBasicControl;

public class testAtCommon {
	public static void main(String[] args) throws Exception {

		String fileAdd = "C:\\Users\\alter\\Downloads\\ExcelTemplate000.pptx";
		PPTBasicControl ppt = new PPTBasicControl(fileAdd);
		
		Path2D path = new Path2D.Double();
		path.moveTo(0, 0);
		path.lineTo(10, 0);
		path.lineTo(10, 10);
		path.lineTo(0, 10);
		path.closePath();
		
		ppt.createShape(path, 0);
		ppt.close();
		
		
	}
}
