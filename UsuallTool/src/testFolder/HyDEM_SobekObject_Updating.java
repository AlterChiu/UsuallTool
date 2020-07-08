package testFolder;

import java.io.File;
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
import geo.gdal.IrregularReachBasicControl.EdgeClass;
import geo.gdal.IrregularReachBasicControl.NodeClass;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import testFolder.SOBEK_OBJECT.SobekBankLine;
import testFolder.SOBEK_OBJECT.SobekBankPoint;
import usualTool.AtCommonMath;

public class HyDEM_SobekObject_Updating {

	// workSpace
	public static String workSpace = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\溢堤線更新\\";
	public static String sobekObjectWorkSpace = workSpace + "SOBEK物件\\shp-file\\";
	public static String testingWorkSpace = workSpace + "testing\\";

	// creating fileName
	public static String pairseBankLine_Error = "SobekBankLine_pairesError.shp";
	public static String pairseBankLine = "SobekBankLine_paires.shp";
	public static String pariseBankPointsError = "SobekBankPoints_pairesError.shp";
	public static String pariseBankPoints = "SobekBankPoints_paires.shp";
	public static String reachNodesShp = "SobekReachNode.shp";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		// pairs bank line
//		pairsBankLine();

		// pairs bankPoints
//		parisCrossSection();
		
		
	}

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
	public static void pairsBankLine() {
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
	public static void parisCrossSection() {

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
		String crossSectionPoint = sobekObjectWorkSpace + "Sbk_LConn_n.shp";
		SpatialReader crossSectionShp = new SpatialReader(crossSectionPoint);
		List<Geometry> crossSectionGeoList = crossSectionShp.getGeometryList();
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

			for (String corssSectionKey : crossSectionGeoMap.keySet()) {
				if (!bankPointRecord.contains(corssSectionKey)) {
					if (temptBankLine.isNodeContain(corssSectionKey) >= 0) {
						temptBankLine.addBankPoint(crossSectionGeoMap.get(corssSectionKey));
						bankPointRecord.add(corssSectionKey);
					}
				}
			}
		});

		// create reachPoint
		List<Geometry> reachPoints = new ArrayList<>();
		crossSectionGeoMap.keySet().forEach(crossSectionKey -> {
			if (!bankPointRecord.contains(crossSectionKey))
				reachPoints.add(crossSectionGeoMap.get(crossSectionKey));
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
		} else {
			System.out.println("bankPoint pairs complete, create file " + pariseBankPoints);
			bankPointsPairs.saveAsShp(testingWorkSpace + pariseBankPoints);
			new SpatialWriter().setGeoList(reachPoints).saveAsShp(testingWorkSpace + reachNodesShp);
		}
	}
}
