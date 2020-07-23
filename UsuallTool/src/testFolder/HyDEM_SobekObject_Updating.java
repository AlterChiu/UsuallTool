package testFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.IrregularReachBasicControl;
import geo.gdal.IrregularReachBasicControl.NodeClass;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import geo.gdal.vector.GDAL_VECTOR_CenterLine;
import geo.gdal.vector.GDAL_VECTOR_SplitByLine;
import testFolder.SOBEK_OBJECT.SobekBankLine;
import testFolder.SOBEK_OBJECT.SobekBankPoint;
import usualTool.AtCommonMath;

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
	public static String splitHydemLines = "HyDEM_SpliyLine.shp";
	public static String mergedHydemPolygons = "HyDEM_MergedBankLine.shp";
	public static String centerLineHydemPolygons = "HyDEM_CenterLine.shp";
	public static String bankLineHydem = "HyDEM_BankLine.shp";

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

//		// pairs bank line
//		SOBEK_pairsBankLine(); 

		// pairs bankPoints
//		SOBEK_parisCrossSection();

		// Create SlpitLine
//		SOBEK_CreateSplitLine();

		// Split polygon by SplitLine
		HyDEM_SplitPolygonBySplitLine();

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
	// <======== Center Line=== =============================>
	// <================================================>
	/*
	 * @divide HyDEM polygon to "updating" part and "newReach" part
	 * 
	 * @ input : sbk_
	 * 
	 */

	// <================================================>
	// <======== pairs bank line =============================>
	// <================================================>

	/*
	 * @ check bankLine in sobek object to find out is there any bankLine not paired
	 * 
	 * @input : Sbk_Pipe_l.shp
	 * 
	 * @output : SobekBankLine_pairesError.shp or SobekBanLine_pairse.shp
	 * 
	 */
	public static void SOBEK_pairsBankLine() {
		String sobekBankLineFile = sobekObjectWorkSpace + "Sbk_Pipe_l.shp";
		IrregularReachBasicControl bankLine = new IrregularReachBasicControl(sobekBankLineFile);

		// initial data
		List<Geometry> bankGeoLine = bankLine.getReLinkedEdge();
		List<SobekBankLine> unPariesBankLine = new ArrayList<>();
		for (int index = 0; index < bankGeoLine.size(); index++) {
			SobekBankLine temptBankLine = new SobekBankLine(bankGeoLine.get(index));
			temptBankLine.setID(index);
			unPariesBankLine.add(temptBankLine);
		}

		// detect closest bankLine
		for (int index = 0; index < unPariesBankLine.size() - 1; index++) {
			for (int detectIndex = index + 1; detectIndex < unPariesBankLine.size(); detectIndex++) {
				unPariesBankLine.get(index).getDistance(unPariesBankLine.get(detectIndex));
			}
		}

		// check detection
		List<Geometry> errorGeoList = new ArrayList<>();
		for (int index = 0; index < unPariesBankLine.size() - 1; index++) {
			int linkedIndex = unPariesBankLine.get(index).getLinkedPointID();
			if (linkedIndex < 0) {
				System.out.println("bankLine index " + index + " not pairs");
				errorGeoList.add(unPariesBankLine.get(index).getGeo());
			} else {
				if (unPariesBankLine.get(linkedIndex).getLinkedPointID() != index) {
					System.out.println("bankLine index " + index + " error\t" + index + "\t" + linkedIndex + "\t"
							+ unPariesBankLine.get(linkedIndex).getDistance());
					errorGeoList.add(unPariesBankLine.get(linkedIndex).getGeo());
					errorGeoList.add(unPariesBankLine.get(index).getGeo());
				}
			}
		}
		if (errorGeoList.size() > 0) {
			new SpatialWriter().setGeoList(errorGeoList).saveAsShp(testingWorkSpace + pairseBankLine_Error);
			System.out.println("bankLine paireError, create file " + pairseBankLine_Error);
			System.out.println("Please check SOBEK objedt file, Sbk_Pipe_l.shp");

			// if no error, create pairse bankLine
		} else {
			System.out.println("bankLine pairse complete, create file " + pairseBankLine);

			SpatialWriter sobekBankLine = new SpatialWriter();
			sobekBankLine.addFieldType("ID", "Integer");
			sobekBankLine.addFieldType("LinkedID", "Integer");
			sobekBankLine.addFieldType("Direction", "Integer");

			for (int index = 0; index < unPariesBankLine.size(); index++) {
				Map<String, Object> attrTable = new HashMap<>();
				attrTable.put("ID", unPariesBankLine.get(index).getID());
				attrTable.put("LinkedID", unPariesBankLine.get(index).getLinkedPointID());
				attrTable.put("Direction", unPariesBankLine.get(index).getLinkedDirection());
				sobekBankLine.addFeature(unPariesBankLine.get(index).getGeo(), attrTable);
			}
			sobekBankLine.saveAsShp(testingWorkSpace + pairseBankLine);
		}

	}

	// <================================================>
	// <======== pairs bankPoints ===========================>
	// <================================================>

	/*
	 * @ check bankPoints in SobekObject to find out is there any bankPoints not
	 * paired
	 * 
	 * 
	 * @input SobekObject: Sbk_Pipe_l.shp、Sbk_LConn_n.shp
	 * 
	 * @input newObject: pairseBankLine.shp
	 * 
	 * @output :
	 * 
	 * @ bankPoints => pariseBankPointsError or pariseBankPoints
	 * 
	 * @ reachPoint => reachNodesShp
	 * 
	 * 
	 */
	public static void SOBEK_parisCrossSection() {

		// get bank line
		String pariseBaneLine = testingWorkSpace + pairseBankLine;
		SpatialReader bankShp = new SpatialReader(pariseBaneLine);
		List<Geometry> bankGeoList = bankShp.getGeometryList();
		List<Map<String, Object>> bankAttrList = bankShp.getAttributeTable();

		// create bankLine object
		Map<Integer, SobekBankLine> sobekBankLineMap = new HashMap<>();
		for (int index = 0; index < bankGeoList.size(); index++) {
			Geometry bankLineGeo = bankGeoList.get(index);
			int bankLineID = (int) bankAttrList.get(index).get("ID");
			int linkedID = (int) bankAttrList.get(index).get("LinkedID");
			int linkedDirection = (int) bankAttrList.get(index).get("Direction");

			sobekBankLineMap.put(bankLineID, new SobekBankLine(bankLineGeo));
			sobekBankLineMap.get(bankLineID).setID(bankLineID);
			sobekBankLineMap.get(bankLineID).setLinkedPointID(linkedID);
			sobekBankLineMap.get(bankLineID).setLinkedDirection(linkedDirection);
		}

		// get crossSection points
		// crossSection
		String crossSectionPoint = sobekObjectWorkSpace + "Sbk_LConn_n.shp";
		SpatialReader crossSectionShp = new SpatialReader(crossSectionPoint);
		List<Geometry> crossSectionGeoList = crossSectionShp.getGeometryList();

		// crossSection start point
		String crossSectionStartPoint = sobekObjectWorkSpace + "Sbk_C&LR_n.shp";
		new SpatialReader(crossSectionStartPoint).getGeometryList().forEach(geo -> crossSectionGeoList.add(geo));

		Map<String, Geometry> crossSectionGeoMap = new HashMap<>();
		for (int index = 0; index < crossSectionGeoList.size(); index++) {
			String xString = AtCommonMath.getDecimal_String(crossSectionGeoList.get(index).GetX(),
					IrregularReachBasicControl.dataDecimale);
			String yString = AtCommonMath.getDecimal_String(crossSectionGeoList.get(index).GetY(),
					IrregularReachBasicControl.dataDecimale);
			String key = xString + "_" + yString;
			crossSectionGeoMap.put(key, crossSectionGeoList.get(index));
		}

		// division point to bankPoint, and reach point
		Set<String> bankPointRecord = new HashSet<>();
		sobekBankLineMap.keySet().forEach(bankLineKey -> {
			SobekBankLine temptBankLine = sobekBankLineMap.get(bankLineKey);
			List<String> temptNodeList = temptBankLine.getPointKeys();

			for (String temptNodeKey : temptNodeList) {
				if (crossSectionGeoMap.containsKey(temptNodeKey)) {
					temptBankLine.addBankPoint(crossSectionGeoMap.get(temptNodeKey));
					bankPointRecord.add(temptNodeKey);
				}
			}
		});

		// pairs bankPoints in bankLine
		SpatialWriter bankPointsPairs = new SpatialWriter();
		bankPointsPairs.addFieldType("ID", "String");
		bankPointsPairs.addFieldType("LinkedID", "String");
		bankPointsPairs.addFieldType("BankLineID", "Integer");

		List<Geometry> errorBnakPoints = new ArrayList<>();
		Set<Integer> bankLineRecord = new HashSet<>();
		for (Integer currentBankLineID : sobekBankLineMap.keySet()) {

			if (!bankLineRecord.contains(currentBankLineID)) {
				SobekBankLine currentBankLine = sobekBankLineMap.get(currentBankLineID);
				List<SobekBankPoint> currentBankPoints = currentBankLine.getBankPoints();

				SobekBankLine linkedBankLine = sobekBankLineMap.get(currentBankLine.getLinkedPointID());
				List<SobekBankPoint> linkedBankPoints = linkedBankLine.getBankPoints();

				// 0 for HeadToHead , 1 for HeadToEnd
				if (currentBankLine.getLinkedDirection() == 1) {
					Collections.reverse(currentBankPoints);
				}

				// check for points number is correct or not
				if (currentBankPoints.size() != linkedBankPoints.size()) {
					currentBankPoints.forEach(bnakPoint -> errorBnakPoints.add(bnakPoint.getGeo()));
					linkedBankPoints.forEach(bnakPoint -> errorBnakPoints.add(bnakPoint.getGeo()));
				}

				// if no error start pairs
				else {
					for (int index = 0; index < currentBankPoints.size(); index++) {
						SobekBankPoint temptCurrentBankPoint = currentBankPoints.get(index);
						SobekBankPoint temptLinkedBankPoints = linkedBankPoints.get(index);

						// current feature
						Map<String, Object> currentAttr = new HashMap<>();
						currentAttr.put("ID", temptCurrentBankPoint.getID());
						currentAttr.put("LinkedID", temptLinkedBankPoints.getID());
						currentAttr.put("BankLineID", temptCurrentBankPoint.getBelongBankLineID());
						bankPointsPairs.addFeature(temptCurrentBankPoint.getGeo(), currentAttr);

						// linked feature
						Map<String, Object> linkedtAttr = new HashMap<>();
						linkedtAttr.put("ID", temptLinkedBankPoints.getID());
						linkedtAttr.put("LinkedID", temptCurrentBankPoint.getID());
						linkedtAttr.put("BankLineID", temptLinkedBankPoints.getBelongBankLineID());
						bankPointsPairs.addFeature(temptLinkedBankPoints.getGeo(), linkedtAttr);
					}

					// remove bankPoint id from
					bankLineRecord.add(currentBankLine.getID());
					bankLineRecord.add(linkedBankLine.getID());
				}
			}
		}

		// create reachPoint and bnakPoint files
		if (errorBnakPoints.size() != 0) {
			// throw error message
			System.out
					.println("bankPoint pairs error, bankPoint amount not match, create file " + pariseBankPointsError);
			new SpatialWriter().setGeoList(errorBnakPoints).saveAsShp(testingWorkSpace + pariseBankPointsError);
			System.out.println("please checkout files, Sbk_LConn_n.shp and Sbk_C&LR_n.shp");

		} else {
			System.out.println("bankPoint pairs complete, create file " + pariseBankPoints);

			// create bankPoints
			bankPointsPairs.saveAsShp(testingWorkSpace + pariseBankPoints);

			// create reachPoint
			List<Geometry> reachPoints = new ArrayList<>();
			crossSectionGeoMap.keySet().forEach(crossSectionKey -> {
				if (!bankPointRecord.contains(crossSectionKey))
					reachPoints.add(crossSectionGeoMap.get(crossSectionKey));
			});
			new SpatialWriter().setGeoList(reachPoints).saveAsShp(testingWorkSpace + reachNodesShp);
		}
	}

	// <================================================>
	// <======== Create SlpitLine =============================>
	// <================================================>
	/*
	 * @ Create splitLine from pairs bankPoints, which double distance of
	 * pairseBankPoints
	 * 
	 * @input : pariseBankPoints
	 * 
	 * @output :
	 * 
	 * @ SplitLine => splitLinePairseBankPoints
	 * 
	 */
	public static void SOBEK_CreateSplitLine() {

		// read pairs point properties
		SpatialReader parisBankPoints = new SpatialReader(
				testingWorkSpace + HyDEM_SobekObject_Updating.pariseBankPoints);
		List<Map<String, Object>> pointsAttr = parisBankPoints.getAttributeTable();
		Map<String, List<Geometry>> geoMap = parisBankPoints.getGeoListMap("ID");

		// create pairs line
		Set<String> usedID = new HashSet<>();
		List<Geometry> outList = new ArrayList<>();
		for (int index = 0; index < pointsAttr.size(); index++) {
			String currentID = (String) pointsAttr.get(index).get("ID");
			String linkedID = (String) pointsAttr.get(index).get("LinkedID");

			if (!usedID.contains(currentID)) {
				Geometry currentGeometry = geoMap.get(currentID).get(0);
				double currentX = currentGeometry.GetX();
				double currentY = currentGeometry.GetY();

				Geometry linkedGeometry = geoMap.get(linkedID).get(0);
				double linkedX = linkedGeometry.GetX();
				double linkedY = linkedGeometry.GetY();

				double outX1 = currentX + 0.5 * (currentX - linkedX) / 2;
				double outY1 = currentY + 0.5 * (currentY - linkedY) / 2;
				double outX2 = linkedX + 0.5 * (linkedX - currentX) / 2;
				double outY2 = linkedY + 0.5 * (linkedY - currentY) / 2;

				outList.add(GdalGlobal.CreateLine(outX1, outY1, outX2, outY2));
			}

			usedID.add(currentID);
			usedID.add(linkedID);
		}

		new SpatialWriter().setGeoList(outList)
				.saveAsShp(testingWorkSpace + HyDEM_SobekObject_Updating.splitLinePairseBankPoints);
		System.out.println("create split line complete, " + HyDEM_SobekObject_Updating.splitLinePairseBankPoints);
	}

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

	public static void HyDEM_SplitPolygonBySplitLine() throws IOException, InterruptedException {
		// merge all shp in hydemObjectWorkSpace folder
		List<Geometry> geoList = new ArrayList<>();
		for (String fileName : new File(hydemObjectWorkSpace).list()) {
			if (fileName.contains(".shp")) {
				new SpatialReader(hydemObjectWorkSpace + "\\" + fileName).getGeometryList()
						.forEach(geo -> geoList.add(geo));
			}
		}

		Geometry mergedBankLine = GdalGlobal.mergePolygons(geoList);
		System.out.println(mergedBankLine.GetGeometryName());
		new SpatialWriter().setGeoList(GdalGlobal.MultiPolyToPolies(mergedBankLine))
				.saveAsShp(testingWorkSpace + mergedHydemPolygons);
		geoList.clear();

		// get boundary of mergedBankLine
		Geometry mergedBankLineBoundary = mergedBankLine.GetBoundary();
		System.out.println(mergedBankLine.GetGeometryName());

		// get split line from splitLinePairseBankPoints
		// ignore which intersection not equals to 2
		List<Geometry> splitLineHyDEM = new ArrayList<>();
		List<Geometry> splitLines = new SpatialReader(testingWorkSpace + splitLinePairseBankPoints).getGeometryList();
		for (Geometry splitLine : splitLines) {
			Geometry intersection = splitLine.Intersection(mergedBankLineBoundary);
			if (intersection.GetGeometryCount() == 2) {
				Geometry point1 = intersection.GetGeometryRef(0);
				Geometry point2 = intersection.GetGeometryRef(1);

				splitLineHyDEM.add(GdalGlobal.CreateLine(point1.GetX(), point1.GetY(), point2.GetX(), point2.GetY()));
			}
		}

		// output splitLine in bankLine polygon
		new SpatialWriter().setGeoList(splitLineHyDEM).saveAsShp(testingWorkSpace + splitHydemLines);
		splitLines.clear();

		// buffer splitLine
		Geometry dissoveSplitLine = GdalGlobal.CreateMultipolygon();
		splitLineHyDEM
				.forEach(splitLine -> dissoveSplitLine.AddGeometry(splitLine.Buffer(Math.pow(0.1, dataDecimal + 2))));

		// split mergedBankLine by dissoveSplitLine
		new SpatialWriter().setGeoList(GdalGlobal.MultiPolyToPolies(mergedBankLine.Difference(dissoveSplitLine)))
				.saveAsShp(testingWorkSpace + splitHydemPolygons);
		System.out.println("create split polygon complete, " + splitHydemPolygons);
	}

	// <================================================>
	// <====== ReCraete pairs bankLine from splitHyDEM polygon ========>
	// <================================================>
	/*
	 * @ translate polygons to paired bankLine
	 * 
	 * @ input : splitHydemPolygons
	 * 
	 * @ output : bankLineHydem
	 */

//	public 

}
