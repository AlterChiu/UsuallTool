package geo.common.Task.HyDEM.BankLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.IrregularNetBasicControl;
import geo.gdal.IrregularNetBasicControl.NodeClass;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import geo.gdal.vector.GDAL_VECTOR_Defensify;
import usualTool.AtCommonMath;
import usualTool.AtCommonMath.StaticsModel;

public class HyDEM_SplitPolygonBySplitLine {
	public static int dataDecimal = 4;

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		String testingWorkSpace = WorkSpace.testingWorkSpace;
		String hydemObjectWorkSpace = WorkSpace.hydemObjectWorkSpace;
		String mergedHydemPolygons = WorkSpace.mergedHydemPolygons;
		String userDefinSplitLine = WorkSpace.userDefineSplitLine;
		String splitHydemLines = WorkSpace.splitHydemLines;
		String splitHydemPolygons = WorkSpace.splitHydemPolygons;

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

		// <SELECT DEM FRAME>
		// <-------------------------------------------------------------------------------------------->
		// merge all shp in hydemObjectWorkSpace folder
		List<Geometry> geoList = new ArrayList<>();
		for (String fileName : new File(hydemObjectWorkSpace).list()) {
			if (fileName.contains(".shp")) {
				new SpatialReader(hydemObjectWorkSpace + "\\" + fileName).getGeometryList()
						.forEach(geo -> geoList.add(geo));
			}
		}

		Geometry mergedBankLine = GdalGlobal.GeometriesMerge(geoList);
		mergedBankLine = getDefensifyGeometry(mergedBankLine);
		new SpatialWriter().setGeoList(GdalGlobal.MultiPolyToSingle(mergedBankLine))
				.saveAsShp(testingWorkSpace + mergedHydemPolygons);
		geoList.clear();

		// <DIVISION SPLIT LINE>
		// <-------------------------------------------------------------------------------------------->
		// get boundary of mergedBankLine
		Geometry mergedBankLineBoundary = mergedBankLine.GetBoundary();

		// get split line from splitLinePairseBankPoints
		// ignore which intersection nodes under than 1
		List<Geometry> splitLineHyDEM = new ArrayList<>();
		List<Geometry> splitLines = new SpatialReader(testingWorkSpace + userDefinSplitLine).getGeometryList();
		int completedPersantage = 0;
		for (int splitLineIndex = 0; splitLineIndex < splitLines.size(); splitLineIndex++) {
			double currentPersantage = (int) ((splitLineIndex + 0.) * 100 / splitLines.size());
			if (currentPersantage > completedPersantage) {
				System.out.print((int) currentPersantage + "....");
				completedPersantage = (int) currentPersantage;
			}

			// skip null splitLine
			try {
				Geometry splitLine = splitLines.get(splitLineIndex);
				Geometry intersection = splitLine.Intersection(mergedBankLineBoundary);

				if (intersection.GetGeometryCount() == 2) {
					Geometry point1 = intersection.GetGeometryRef(0);
					Geometry point2 = intersection.GetGeometryRef(1);
					splitLineHyDEM.add(GdalGlobal.CreateLineString(point1.GetX(), point1.GetY(), point1.GetZ(),
							point2.GetX(), point2.GetY(), point2.GetZ()));
				}
			} catch (Exception e) {
			}
		}
		System.out.println("");

		// output splitLine in bankLine polygon
		new SpatialWriter().setGeoList(splitLineHyDEM).saveAsShp(testingWorkSpace + splitHydemLines);
		splitLines.clear();

		// <SPLIT BANKLINE POLYGON To SEVERAL PARTS>
		// <-------------------------------------------------------------------------------------------->
		// buffer splitLine
		List<Geometry> dissoveSplitLine = new ArrayList<>();
		splitLineHyDEM.forEach(splitLine -> dissoveSplitLine.add(splitLine.Buffer(Math.pow(0.1, dataDecimal + 4))));

		// split polygon
		List<Geometry> dissovedPolygons = GdalGlobal
				.MultiPolyToSingle(mergedBankLine.Difference(GdalGlobal.GeometriesMerge(dissoveSplitLine)));
		dissoveSplitLine.clear();

		// <RECREATE POLYGONS>
		// <-------------------------------------------------------------------------------------------->
		// make dissovedPolygons to java class
		IrregularNetBasicControl irregularNet = new IrregularNetBasicControl(dissovedPolygons);
		Map<String, NodeClass> irregularNetNodeMap = irregularNet.getNodeMap();

		// get poinySet in HyDEM splitLines
		Map<String, String> splitLinePointMap = new HashMap<>();
		splitLineHyDEM.forEach(line -> {
			String x1 = AtCommonMath.getDecimal_String(line.GetX(0), irregularNet.getDataDecimal());
			String y1 = AtCommonMath.getDecimal_String(line.GetY(0), irregularNet.getDataDecimal());
			String key1 = x1 + "_" + y1;

			String x2 = AtCommonMath.getDecimal_String(line.GetX(1), irregularNet.getDataDecimal());
			String y2 = AtCommonMath.getDecimal_String(line.GetY(1), irregularNet.getDataDecimal());
			String key2 = x2 + "_" + y2;

			splitLinePointMap.put(key1, key2);
			splitLinePointMap.put(key2, key1);
		});

		// replace z-value in interpolation which contain in splitLine
		irregularNet.getNodesList().forEach(node -> {
			String nodeKey = node.getKey();
			if (splitLinePointMap.containsKey(nodeKey)) {
				NodeClass currentNode = irregularNetNodeMap.get(nodeKey);

				List<NodeClass> temptNodeList = new ArrayList<>();
				List<NodeClass> linkedNodeList = node.getLinkedNode();
				for (NodeClass linkedNode : linkedNodeList) {
					if (!splitLinePointMap.containsKey(linkedNode.getKey())) {
						temptNodeList.add(linkedNode);
					}
				}

				// get level-Z
				try {
					currentNode.setZ(getZ(currentNode, temptNodeList));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		splitLinePointMap.clear();
		irregularNetNodeMap.clear();

		// <OUTPUT>
		// <-------------------------------------------------------------------------------------------->
		new SpatialWriter().setGeoList(
				irregularNet.getFaceList().parallelStream().map(face -> face.getGeo()).collect(Collectors.toList()))
				.saveAsShp(testingWorkSpace + splitHydemPolygons);
		System.out.println("create split polygon complete, " + splitHydemPolygons);
	}

	private static double getZ(NodeClass currentNode, List<NodeClass> nodeList) throws Exception {
		List<Double> ratioList = new ArrayList<>();
		List<Double> valueList = new ArrayList<>();

		double x = currentNode.getX();
		double y = currentNode.getY();
		nodeList.forEach(node -> {
			ratioList.add(1. / AtCommonMath.getLength(x, y, node.getX(), node.getY()));
			valueList.add(node.getZ());
		});

		double outZ = 0;
		double totalRatio = AtCommonMath.getListStatistic(ratioList, StaticsModel.getSum);
		for (int index = 0; index < valueList.size(); index++) {
			outZ = outZ + (ratioList.get(index) / totalRatio) * valueList.get(index);
		}

		return outZ;
	}

	private static Geometry getDefensifyGeometry(Geometry geo) throws IOException, InterruptedException {
		GDAL_VECTOR_Defensify defensify = new GDAL_VECTOR_Defensify(geo);
		defensify.setInterval(5.0);
		return defensify.getGeoList().get(0);
	}
}
