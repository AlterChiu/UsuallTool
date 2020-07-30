package geo.common.Task.HyDEM.BankLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class HyDEM_SplitPolygonBySplitLine {
	public static int dataDecimal = 4;

	// workSpace
	public static String workSpace = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\溢堤線更新\\";
	public static String sobekObjectWorkSpace = workSpace + "SOBEK物件\\shp-file\\";
	public static String hydemObjectWorkSpace = workSpace + "溢堤線\\第一期\\";
	public static String testingWorkSpace = workSpace + "testing\\";

	// creating fileName
	public static String pairseBankLine_Error = "SOBEK_BankLinepairesError.shp";
	public static String pairseBankLine = "SOBEK_BankLinepaires.shp";
	public static String pariseBankPointsError = "SOBEK_BankPointspairesError.shp";
	public static String pariseBankPoints = "SOBEK_BankPointspaires.shp";
	public static String reachNodesShp = "SOBEK_ReachNode.shp";
	public static String splitLinePairseBankPoints = "SOBEK_BankPointsLine.shp";

	public static String splitHydemPolygons = "HyDEM_SplitPolygons.shp";
	public static String splitHydemLines = "HyDEM_SplitLine.shp";
	public static String mergedHydemPolygons = "HyDEM_MergedBankLine.shp";
	public static String centerLineHydemPolygons = "HyDEM_CenterLine.shp";
	public static String bankLineHydem = "HyDEM_BankLine.shp";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String testingWorkSpace = HyDEM_SplitPolygonBySplitLine.testingWorkSpace;
		String hydemObjectWorkSpace = HyDEM_SplitPolygonBySplitLine.hydemObjectWorkSpace;
		String mergedHydemPolygons = HyDEM_SplitPolygonBySplitLine.mergedHydemPolygons;
		String splitLinePairseBankPoints = HyDEM_SplitPolygonBySplitLine.splitLinePairseBankPoints;
		String splitHydemLines = HyDEM_SplitPolygonBySplitLine.splitHydemLines;
		String splitHydemPolygons = HyDEM_SplitPolygonBySplitLine.splitHydemPolygons;
		
		
		
		// <================================================>
		// <======== Split HyDEM polygon by SplitLine==================>
		// <================================================>
		/*
		 * @ split HyDEM polygon by split line
		 * 
		 * @ input : splitLinePairseBankPoints, hydemObjectWorkSpace(folder address)
		 * 
		 * @ output : splitHydemPolygons
		 */

		// merge all shp in hydemObjectWorkSpace folder
		List<Geometry> geoList = new ArrayList<>();
		for (String fileName : new File(hydemObjectWorkSpace).list()) {
			if (fileName.contains(".shp")) {
				new SpatialReader(hydemObjectWorkSpace + "\\" + fileName).getGeometryList()
						.forEach(geo -> geoList.add(geo));
			}
		}

		Geometry mergedBankLine = GdalGlobal.mergePolygons(geoList);
		new SpatialWriter().setGeoList(GdalGlobal.MultiPolyToSingle(mergedBankLine))
				.saveAsShp(testingWorkSpace + mergedHydemPolygons);
		geoList.clear();

		// get boundary of mergedBankLine
		Geometry mergedBankLineBoundary = mergedBankLine.GetBoundary();

		// get split line from splitLinePairseBankPoints
		// ignore which intersection nodes under than 1
		List<Geometry> splitLineHyDEM = new ArrayList<>();
		List<Geometry> splitLines = new SpatialReader(testingWorkSpace + splitLinePairseBankPoints).getGeometryList();
		int completedPersantage = 0;
		for (int splitLineIndex = 0; splitLineIndex < splitLines.size(); splitLineIndex++) {
			double currentPersantage = (int) ((splitLineIndex + 0.) * 100 / splitLines.size());
			if (currentPersantage > completedPersantage) {
				System.out.print((int) currentPersantage + "....");
				completedPersantage = (int) currentPersantage;
			}

			Geometry splitLine = splitLines.get(splitLineIndex);
			Geometry intersection = splitLine.Intersection(mergedBankLineBoundary);

			if (intersection.GetGeometryCount() == 2) {
				Geometry point1 = intersection.GetGeometryRef(0);
				Geometry point2 = intersection.GetGeometryRef(1);
				splitLineHyDEM.add(GdalGlobal.CreateLine(point1.GetX(), point1.GetY(), point2.GetX(), point2.GetY()));

			}
		}
		System.out.println("");

		// output splitLine in bankLine polygon
		new SpatialWriter().setGeoList(splitLineHyDEM).saveAsShp(testingWorkSpace + splitHydemLines);
		splitLines.clear();

		// buffer splitLine
		Geometry dissoveSplitLine = GdalGlobal.CreateMultipolygon();
		splitLineHyDEM
				.forEach(splitLine -> dissoveSplitLine.AddGeometry(splitLine.Buffer(Math.pow(0.1, dataDecimal + 4))));

		// split mergedBankLine by dissoveSplitLine
		new SpatialWriter().setGeoList(GdalGlobal.MultiPolyToSingle(mergedBankLine.Difference(dissoveSplitLine)))
				.saveAsShp(testingWorkSpace + splitHydemPolygons);
		System.out.println("create split polygon complete, " + splitHydemPolygons);
	}

}