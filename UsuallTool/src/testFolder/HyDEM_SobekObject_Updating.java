package testFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.IrregularNetBasicControl;
import geo.gdal.IrregularNetBasicControl.EdgeClass;
import geo.gdal.IrregularNetBasicControl.FaceClass;
import geo.gdal.IrregularReachBasicControl;
import geo.gdal.IrregularReachBasicControl.NodeClass;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import geo.gdal.vector.GDAL_VECTOR_CenterLine;
import testFolder.SOBEK_OBJECT.SobekBankLine;
import testFolder.SOBEK_OBJECT.SobekBankPoint;
import usualTool.AtCommonMath;
import usualTool.AtFileWriter;

public class HyDEM_SobekObject_Updating {
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
	public static String bankLineHydem_Leveling = "HyDEM_BankLineLevling.csv";

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

//		// pairs bank line
//		SOBEK_pairsBankLine(); 

		// pairs bankPoints
//		SOBEK_parisCrossSection();

		// Create SlpitLine
//		SOBEK_CreateSplitLine();

		// Split polygon by SplitLine
//		HyDEM_SplitPolygonBySplitLine();
//		HyDEM_ReLinkedBankLine();

		HyDEM_CheckLevelContinue();

	}

	// <================================================>
	// <======== Center Line=== =============================>
	// <================================================>
	/*
	 * @ create center line from HyDEM bankLine polygons
	 * 
	 * @ input : mergedHydemPolygons
	 * 
	 * @ output : centerLine of HyDEM polygons
	 * 
	 * @ variables :
	 * 
	 * @setVerticeDensitive(m) : the densitive of vertices in HyDEM polygons
	 * 
	 * @ remove length(m) : remove the edge which shorter than length
	 */

	public static void HyDEM_CenterLine() throws IOException, InterruptedException {

		// create centerLine
		System.out.println("create centerLine.....");
		GDAL_VECTOR_CenterLine centerLineAlgorithm = new GDAL_VECTOR_CenterLine(testingWorkSpace + mergedHydemPolygons);
		centerLineAlgorithm.setVerticeDensitive(1.0);
		List<Geometry> centerLineGeoList = centerLineAlgorithm.getGeoList();

		// remove error
		System.out.println("remove error.....");
		List<Geometry> outList = new ArrayList<>();
		centerLineGeoList = new IrregularReachBasicControl(centerLineGeoList).getReLinkedEdge();

		IrregularReachBasicControl reLinkedCenterLine = new IrregularReachBasicControl(centerLineGeoList);
		reLinkedCenterLine.getEdgeList().forEach(edge -> {
			if (edge.getGeo().Length() >= 10) {
				outList.add(edge.getGeo());
			} else {

				List<NodeClass> nodeList = edge.getNode();
				NodeClass node1 = nodeList.get(0);
				NodeClass node2 = nodeList.get(nodeList.size() - 1);

				if (node1.getEdge().size() != 1 && node2.getEdge().size() != 1) {
					outList.add(edge.getGeo());
				}
			}
		});
		new SpatialWriter().setGeoList(outList).saveAsShp(testingWorkSpace + centerLineHydemPolygons);
		System.out.print("end");
	}

	// <================================================>
	// <====== Create CenterLine from HyDEM BankLine===============>
	// <================================================>

//	public static void HyDEM_CreateCenterLine() {
//
//		String bankLineFileAdd = testingWorkSpace + bankLineHydem;
//		List<>
//		
//		
//		
//	}

	// <================================================>
	// <====== Check Leveling Continue of HyDEM BankLine ============>
	// <================================================>
	public static void HyDEM_CheckLevelContinue() throws IOException {
		String bankLineShpAdd = testingWorkSpace + bankLineHydem;

		SpatialReader banLineShp = new SpatialReader(bankLineShpAdd);
		List<Geometry> bankLineGeoList = banLineShp.getGeometryList();
		List<Map<String, Object>> bankLineAttrList = banLineShp.getAttributeTable();

		Map<String, BankLineLevelingClass> bankLineLeveling = new TreeMap<>();

		for (int index = 0; index < bankLineGeoList.size(); index++) {
			int id = (int) bankLineAttrList.get(index).get("ID");
			Geometry temptGeo = bankLineGeoList.get(index);
			double length = temptGeo.Length();

			List<Double[]> outList = new ArrayList<>();// 0: length persantage, which 0-100 ; 1: z-value

			// get points
			List<Double[]> temptValueList = new ArrayList<>();
			for (double[] point : temptGeo.GetPoints()) {
				temptValueList.add(new Double[] { point[0], point[1], point[2] });
			}

			// check level, from low to high
			if (temptValueList.get(0)[2] > temptValueList.get(temptValueList.size() - 1)[2]) {
				Collections.reverse(temptValueList);
			}

			// translate point to , length-Z plot, which length from 0% - 100%
			double currentLength = 0;
			double maxSlope = Double.NEGATIVE_INFINITY;
			Double[] currentPoint = temptValueList.get(0);
			outList.add(new Double[] { 0., currentPoint[2] });

			for (int pointIndex = 1; pointIndex < temptValueList.size(); pointIndex++) {
				Double[] nextPoint = temptValueList.get(pointIndex);
				double nextLength = AtCommonMath.getLength(currentPoint[0], currentPoint[1], nextPoint[0],
						nextPoint[1]);

				// check slope
				double slope = Math.abs((currentPoint[2] - nextPoint[2]) / nextLength) * 100;
				if (slope > maxSlope) {
					maxSlope = slope;
				}

				// summary length
				currentLength = currentLength + nextLength;

				// add to valueList
				outList.add(new Double[] { currentLength * 100 / length, temptValueList.get(pointIndex)[2] });
				currentPoint = temptValueList.get(pointIndex);
			}

			BankLineLevelingClass temptStoreClass = new BankLineLevelingClass();
			temptStoreClass.setID(id + "");
			temptStoreClass.setTotalLength(length);
			temptStoreClass.setValueList(outList);
			temptStoreClass.setSlope(maxSlope);
			temptStoreClass.setLinkedID((int) bankLineAttrList.get(index).get("LinkedID") + "");
			bankLineLeveling.put(id + "", temptStoreClass);

		}

		// output leveling
		List<String[]> outContent = new ArrayList<>();
		outContent.add(new String[] { "ID", "LinkedID", "MaxSlope(%)", "totalLength(m)" });
		bankLineLeveling.keySet().forEach(key -> {

			BankLineLevelingClass temptLeveling = bankLineLeveling.get(key);
			List<String> temptProperties = new ArrayList<>();
			temptProperties.add(temptLeveling.getID());
			temptProperties.add(temptLeveling.getLinkedID());
			temptProperties.add(temptLeveling.getSlope() + "");
			temptProperties.add(temptLeveling.getLength() + "");

			List<String> temptXList = new ArrayList<>();
			temptXList.add("Length(%)");
			List<String> temptZList = new ArrayList<>();
			temptZList.add("Z-Leveling(m)");
			temptLeveling.getValueList().forEach(value -> {
				temptXList.add(value[0] + "");
				temptZList.add(value[1] + "");
			});
			outContent.add(temptProperties.parallelStream().toArray(String[]::new));
			outContent.add(temptXList.parallelStream().toArray(String[]::new));
			outContent.add(temptZList.parallelStream().toArray(String[]::new));
		});

		new AtFileWriter(outContent.parallelStream().toArray(String[][]::new),
				testingWorkSpace + bankLineHydem_Leveling).csvWriter();
	}

	private static class BankLineLevelingClass {
		private List<Double[]> lengthZList = new ArrayList<>();
		private String id = "";
		private double totalLength = 0;
		private double maxSlope = Double.NEGATIVE_INFINITY;
		private String linkedID = "";

		public void setLinkedID(String linkedID) {
			this.linkedID = linkedID;
		}

		public String getLinkedID() {
			return this.linkedID;
		}

		public void setSlope(double slope) {
			this.maxSlope = slope;
		}

		public double getSlope() {
			return this.maxSlope;
		}

		public void setValueList(List<Double[]> valueList) {
			this.lengthZList = valueList;
		}

		public void setID(String id) {
			this.id = id;
		}

		public void setTotalLength(double length) {
			this.totalLength = length;
		}

		public List<Double[]> getValueList() {
			return this.lengthZList;
		}

		public String getID() {
			return this.id;
		}

		public double getLength() {
			return this.totalLength;
		}

	}

}
