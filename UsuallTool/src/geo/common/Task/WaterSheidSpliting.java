package geo.common.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.IrregularNetBasicControl;
import geo.gdal.IrregularNetBasicControl.FaceClass;
import geo.gdal.IrregularReachBasicControl;
import geo.gdal.IrregularReachBasicControl.EdgeClass;
import geo.gdal.IrregularReachBasicControl.NodeClass;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtCommonMath;

public class WaterSheidSpliting {
	private static int dataDecimale = 4;
	private static String mainStreamSHP = "";
	private static int mainStreamEPSG = 3826;
	private static double bufferRadius = 30;
	private static String bufferStreamShp = "";
	private static String waterSheidSHP = "";
	private static double waterSheidMinArea = 0;
	private static int waterSheidEPSG = 3826;
	private static String otherStreamSHP = "";
	private static int otherStreamEPSG = 3826;
	private static String groupedReachSHP = "";
	private static int groupedReachEPSG = 3826;
	private static String groupedNodeShp = "";
	private static int groupedNodeEPSG = 3826;
	private static String groupedPolygons = "";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// GUID & PROCESSING
		// ================================================
		/*
		 * This code is for grouping reaches
		 * 
		 * 1. buffer the main stream geometry, make it contain unGrouped reached
		 * 
		 * 2. get the intersect node, which only link to 1 or over 3 edges
		 * 
		 * 3. deploy the nodes from (2) into "in main stream", or "not in main stream"
		 * 
		 * 4. deploy all the edges into, "in main stream"(two nodes are all in main
		 * stream) , "cross main stream"(one of node is in main stream, the other one
		 * isn't), "not in main stream"(none of node is in mainStream)
		 * 
		 * 5. stared grouping from edge which is "cross main stream"
		 * 
		 * 7. merge polygon which area is under values(determine by user)
		 * 
		 * 8. grouping polygons which intersect by grouped reached
		 * 
		 * 9. output :
		 * 
		 * (a) start node of grouped reaches, which contains (id,X,Y). which id is
		 * matched to (b)
		 * 
		 * (b) grouped reaches, which contains (id , length). which id is matched to (a)
		 * 
		 * (c) grouped polygons
		 * 
		 */

		// USER DETERMINE
		// ================================================
		/*
		 * 1. -mainStreamSHP : the filePath of mainStream shapeFile
		 * 
		 * 2. -mainStreamEPSG : the EPSG of mainStream, default for 3826
		 * 
		 * 3. -bufferRadius : the buffer radius for mainStream, unit is base on EPSG,
		 * default is 30m
		 * 
		 * 4. - bufferStreamShp : the filePath of bufferStream for saving
		 * 
		 * 5. -waterSheidSHP : the filePath of waterSheid polygons
		 * 
		 * 6. -waterSheidMinArea : the minArea of polygon which need to by merged, the
		 * unit is base on EPSG, default is 0
		 * 
		 * 7. -waterSheidEPSG : the EPSG of waterSheid, default for 3826
		 * 
		 * 8. -otherStreamSHP : the filePath of unGrouped reaches shapeFile
		 * 
		 * 9. -otherStreamEPSG : the EPSG for unGrouped reached shapeFile
		 * 
		 * 10. -groupedReachSHP : the filePath of grouped reaches for saving
		 * 
		 * 11. -groupedReachEPSG : the EPSG for saving grouped reached, default for 3826
		 * 
		 * 12. -groupedNodeShp : the filePath of start node of grouped reaches shapeFile
		 * 
		 * 13. -groupedNodeEPSG : the EPSG for saving EPSG
		 * 
		 */

		// READ COMMAND LINE

		/*
		 * testing
		 */
		mainStreamSHP = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\主流.shp";
		bufferStreamShp = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\主流_buffer.shp";
		otherStreamSHP = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_reach.shp";
		groupedReachSHP = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedReach.shp";
		groupedNodeShp = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedNode.shp";
		waterSheidSHP = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10.shp";
		waterSheidMinArea = 100.;
		groupedPolygons = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedPolygons.shp";

		args = new String[] { "-mainStreamSHP", "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\mainStream.shp",
				"-mainStreamEPSG", "3826", "-bufferRadius", "30", "-bufferStreamShp",
				"E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\mainStream-Buffer.shp", "-waterSheidSHP",
				"E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10.shp", "-waterSheidMinArea", "100",
				"-waterSheidEPSG", "3826", "-otherStreamSHP",
				"E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_reach.shp", "-otherStreamEPSG", "3826",
				"-groupedReachSHP", "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedReach.shp",
				"-groupedReachEPSG", "3826", "-groupedNodeShp",
				"E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedNode.shp", "-groupedNodeEPSG",
				"3826", "-groupedPolygons",
				"E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedPolygons.shp" };

		getCommand(args);

		System.out.println("START buffer main stream....");
		mainStreamBuffer(mainStreamSHP, bufferStreamShp, bufferRadius);
		System.out.println("END");

//		System.out.println("START grouping reaches....");
//		getMainStreamGroup(bufferStreamShp, otherStreamSHP, groupedReachSHP, groupedNodeShp);
//		System.out.println("END");
//
//		System.out.println("START merge small polygons");
//		mergeSmallPolygons(waterSheidSHP, waterSheidMinArea);
//		System.out.println("END");
//
//		System.out.println("START grouping polygons");
//		groupPolygons(waterSheidSHP, groupedReachSHP, groupedPolygons);
//		System.out.println("END");

	}

	public static void getMainStreamGroup(String mainStreamBufferShp, String otherStreamFile, String groupedStreamShp,
			String groupedNodeShp) {

		/*
		 * check coordination
		 */
		System.out.print("check coordination.......");
		if (mainStreamEPSG != otherStreamEPSG) {
			String temptOtherStringfile = otherStreamFile.split(".")[0] + "-tempt.shp";
			new SpatialReader(otherStreamFile).saveAsShp(temptOtherStringfile, otherStreamEPSG, mainStreamEPSG);
			otherStreamFile = temptOtherStringfile;
		}

		/*
		 * initial class
		 */
		System.out.print("initialize....");
		IrregularReachBasicControl otherStream = new IrregularReachBasicControl(otherStreamFile);
		Map<String, NodeClass> totalNodeMap = otherStream.getNodeMap();
		Map<String, EdgeClass> totalEdgeMap = otherStream.getEdgeMap();
		Map<String, NodeClass> mainStreamNodeMap = new HashMap<>();
		Map<String, EdgeClass> mainStreamEdgeMap = new HashMap<>();
		Map<String, EdgeClass> crossMainStreamEdgeMap = new HashMap<>();
		Map<String, EdgeClass> notMainStreamEdgeMap = new HashMap<>();

		// output for grouped reaches
		Map<String, Geometry> outGeometry_GroupedReached = new HashMap<>();
		Map<String, String> outGeometry_GroupedReached_TableType = new HashMap<>();
		List<Map<String, Object>> outGeometry_GroupedReached_Attr = new ArrayList<>();

		outGeometry_GroupedReached_TableType.put("ID", "String");

		// output for grouped start node
		List<Geometry> outGeometry_GroupedNode = new ArrayList<>();
		Map<String, String> outGeometry_GroupedNode_TableType = new HashMap<>();
		List<Map<String, Object>> outGeometry_GroupedNode_Attr = new ArrayList<>();

		outGeometry_GroupedNode_TableType.put("ID", "String");
		outGeometry_GroupedNode_TableType.put("X", "String");
		outGeometry_GroupedNode_TableType.put("Y", "String");

		// get node in mainStream buffer area
		System.out.print("depoly node.....");
		Geometry mainStreamBufferArea = new SpatialReader(mainStreamBufferShp).getGeometryList().get(0);

		// merge all points to multiPoints
		System.out.print("merge node.....");
		Geometry otherStream_allNodes = GdalGlobal.mergePolygons(
				otherStream.getNodeList().parallelStream().map(node -> node.getGeo()).collect(Collectors.toList()));

		// using intersect to get nodes in mainStream
		System.out.print("intersect node.....");
		List<Geometry> otherStream_nodeInMainStream = GdalGlobal
				.splitPolygons(otherStream_allNodes.Intersection(mainStreamBufferArea));

		// make point to nodeClass
		otherStream_nodeInMainStream.forEach(geo -> {
			String key = AtCommonMath.getDecimal_String(geo.GetX(), dataDecimale) + "_"
					+ AtCommonMath.getDecimal_String(geo.GetY(), dataDecimale);
			mainStreamNodeMap.put(key, totalNodeMap.get(key));
		});

		/*
		 * deploy edge into "in main stream" or "cross main stream"
		 * 
		 * (main stream buffer area)
		 */
		System.out.print("depoly reach.....");
		mainStreamNodeMap.keySet().forEach(key -> {
			NodeClass temptMainStreamNode = mainStreamNodeMap.get(key);
			temptMainStreamNode.getEdge().forEach(temptEdge -> {

				// check the other node of edge is in the main stream or not
				// if the other node is in mainStream
				// let's this edge to mainStreamEdgeMap
				// or make it to crossMainStreamEdgeMap
				try {

					// if in mainStream
					mainStreamNodeMap.get(temptEdge.getOtherNode(temptMainStreamNode).getId()).getId();
					mainStreamEdgeMap.put(temptEdge.getId(), temptEdge);
				} catch (Exception e) {

					// if not in main stream
					crossMainStreamEdgeMap.put(temptEdge.getId(), temptEdge);
				}
			});
		});

		/*
		 * pick up remain edges that doesn't in "main stream" and "cross main stream"s
		 * either
		 * 
		 * (main stream buffer area)
		 */
		totalEdgeMap.keySet().forEach(edgeMapKey -> {
			EdgeClass temptEdge = totalEdgeMap.get(edgeMapKey);

			try {
				mainStreamEdgeMap.get(temptEdge.getId()).getId();
			} catch (Exception e1) {
				try {
					crossMainStreamEdgeMap.get(temptEdge.getId()).getId();
				} catch (Exception e2) {
					notMainStreamEdgeMap.put(temptEdge.getId(), temptEdge);
				}
			}
		});

		/*
		 * starting grouping
		 */
		System.out.print("grouping.....");
		// get node from main stream, which is intersection
		crossMainStreamEdgeMap.keySet().forEach(crossMainStreamEdgeKey -> {
			EdgeClass crossMainStreamEdge = crossMainStreamEdgeMap.get(crossMainStreamEdgeKey);

			// get the node that not in main stream
			crossMainStreamEdge.getNode().forEach(temptNode -> {
				try {

					// if in mainStream, starting grouping reaches
					mainStreamNodeMap.get(temptNode.getId()).getId();

					/*
					 * for grouped nodes
					 */
					Geometry nodeGeometry = temptNode.getGeo();
					outGeometry_GroupedNode.add(nodeGeometry);

					// feature
					Map<String, Object> featureAttr_Node = new HashMap<>();
					featureAttr_Node.put("ID", temptNode.getId());
					featureAttr_Node.put("X", temptNode.getX());
					featureAttr_Node.put("Y", temptNode.getY());
					outGeometry_GroupedNode_Attr.add(featureAttr_Node);

					/*
					 * for grouped reaches
					 */
					List<Geometry> groupedGeometries = getGroupReaches(temptNode, crossMainStreamEdge,
							notMainStreamEdgeMap);
					Geometry groupedGeometry = GdalGlobal.mergePolygons(groupedGeometries);
					outGeometry_GroupedReached.put(temptNode.getId(), groupedGeometry);

					// add a new feature to node map
					Map<String, Object> featureAttr_Reach = new HashMap<>();
					featureAttr_Reach.put("ID", temptNode.getId());
					outGeometry_GroupedReached_Attr.add(featureAttr_Reach);

				} catch (Exception e) {
				}
			});
		});

		/*
		 * output shpfiles
		 */
		// node for grouped reaches

		// grouped reaches
		new SpatialWriter().setFieldType(outGeometry_GroupedReached_TableType)
				.setAttribute(outGeometry_GroupedReached_Attr)
				.setGeoList(outGeometry_GroupedReached.keySet().parallelStream()
						.map(key -> outGeometry_GroupedReached.get(key)).collect(Collectors.toList()))
				.saveAsShp(groupedStreamShp);
		if (mainStreamEPSG != groupedReachEPSG) {
			new SpatialReader(groupedStreamShp).saveAsShp(groupedStreamShp, mainStreamEPSG, groupedReachEPSG);
		}

		new SpatialWriter().setFieldType(outGeometry_GroupedNode_TableType).setAttribute(outGeometry_GroupedNode_Attr)
				.setGeoList(outGeometry_GroupedNode).saveAsShp(groupedNodeShp);
		if (mainStreamEPSG != groupedNodeEPSG) {
			new SpatialReader(groupedNodeShp).saveAsShp(groupedNodeShp, mainStreamEPSG, groupedNodeEPSG);
		}
	}

	private static List<Geometry> getGroupReaches(NodeClass startNode, EdgeClass directionEdge,
			Map<String, EdgeClass> remainEdges) {

		Set<Geometry> outList = new HashSet<>();
		outList.add(directionEdge.getGeo());
		remainEdges.remove(directionEdge.getId());

		NodeClass nextNode = directionEdge.getOtherNode(startNode);
		List<EdgeClass> nextEdgeList = nextNode.getEdge();
		nextEdgeList.remove(directionEdge);

		for (EdgeClass temptEdge : nextEdgeList) {
			try {
				// try the node is exist or not
				remainEdges.get(temptEdge.getId()).getId();
				remainEdges.remove(temptEdge.getId());
				outList.add(temptEdge.getGeo());

				// if exit
				// run grouping process
				if (temptEdge.getOtherNode(nextNode).getEdge().size() != 1) {
					getGroupReaches(nextNode, temptEdge, remainEdges).forEach(geo -> outList.add(geo));

				} else if (temptEdge.getOtherNode(nextNode).getEdge().size() == 1) {
					outList.add(directionEdge.getGeo());

				} else {
					System.out.println(new Exception("node error , id : " + nextNode.getId()));
				}
			} catch (Exception e) {
			}

		}

		return new ArrayList<Geometry>(outList);
	}

	public static void mainStreamBuffer(String inputShp, String outputShp, double bufferDistance) {
		List<Geometry> mainStreamList = new SpatialReader(inputShp).getGeometryList();

		System.out.print("merge.....");
		Geometry mergedMainStream = GdalGlobal.mergePolygons(mainStreamList, true);

		System.out.print("buffer.....");
		new SpatialWriter().setGeoList(mergedMainStream.Buffer(bufferDistance)).saveAsShp(outputShp);
	}

	public static void mergeSmallPolygons(String waterSheidShp, double minArea) {
		if (Math.abs(minArea - 0) > 0.001) {

			System.out.print("initialize....");
			List<Geometry> geoList = new ArrayList<>();
			IrregularNetBasicControl irregularNets = new IrregularNetBasicControl(new SpatialReader(waterSheidShp), 1);
			List<FaceClass> totalGeoList = irregularNets.getFaceList();

			// deploy
			Map<String, FaceClass> geoUnderArea = new HashMap<>();
			Map<String, FaceClass> geoOverArea = new HashMap<>();
			for (FaceClass temptFace : totalGeoList) {
				if (temptFace.getArea() > minArea + AtCommonMath.getDecimal_Double(1, dataDecimale)) {
					geoOverArea.put(temptFace.getFaceKey(), temptFace);
				} else {
					geoUnderArea.put(temptFace.getFaceKey(), temptFace);
				}
			}

			// detect
			System.out.print("detecting....");
			for (String faceOverAreaKey : geoOverArea.keySet()) {
				FaceClass faceOverArea = geoOverArea.get(faceOverAreaKey);

				List<Geometry> temptGeoList = new ArrayList<>();
				temptGeoList.add(faceOverArea.getGeo());

				// if any one of linked face is under area
				// starting to detect how many linked faces are under area
				// merge the overArea face and all the other linked face which underArea
				for (FaceClass temptFace : faceOverArea.getLinkedFace()) {
					if (geoUnderArea.containsKey(temptFace.getFaceKey())) {
						temptGeoList.add(temptFace.getGeo());
						geoUnderArea.remove(temptFace.getFaceKey());
					}
				}

				// merge all the geometries in temptGeoList
				geoList.add(GdalGlobal.mergePolygons(temptGeoList));

			}

			// output merged polygons
			String savingAdd = waterSheidShp.substring(0, waterSheidShp.lastIndexOf(".")) + "-merged.shp";
			new SpatialWriter().setGeoList(geoList).saveAsShp(savingAdd);
			WaterSheidSpliting.waterSheidSHP = savingAdd;
		}
	}

	public static void groupPolygons(String waterSheidSHP, String groupedReachSHP, String groupedWaterSheidSHP) {
		// check coordination
		System.out.print("checking coordinateion....");
		if (mainStreamEPSG != waterSheidEPSG) {
			String temptWaterSheidSHP = waterSheidSHP.split(".")[0] + "shp";
			new SpatialReader(waterSheidSHP).saveAsShp(temptWaterSheidSHP, waterSheidEPSG, mainStreamEPSG);
			waterSheidSHP = temptWaterSheidSHP;
		}

		if (mainStreamEPSG != groupedReachEPSG) {
			String temptReachesSHP = groupedReachSHP.split(".")[0] + "shp";
			new SpatialReader(waterSheidSHP).saveAsShp(temptReachesSHP, waterSheidEPSG, mainStreamEPSG);
			groupedReachSHP = temptReachesSHP;
		}

		// read all the polygons of water sheids
		System.out.print("initialize....");
		List<Geometry> totalPolygons = new SpatialReader(waterSheidSHP).getGeometryList();
		Map<String, Geometry> totalPolygonsMap = new HashMap<>();
		totalPolygons.forEach(geo -> {
			Geometry cnetroid = geo.Centroid();
			String key = AtCommonMath.getDecimal_String(cnetroid.GetX(), dataDecimale) + "_"
					+ AtCommonMath.getDecimal_String(cnetroid.GetY(), dataDecimale);
			totalPolygonsMap.put(key, geo);
		});

		// read grouped reaches
		SpatialReader groupedReaches = new SpatialReader(groupedReachSHP);
		List<Geometry> groupedReachesGeoList = groupedReaches.getGeometryList();
		List<Map<String, String>> groupedReachesAttr = groupedReaches.getAttributeTable();

		// for output grouped polygons
		List<Geometry> groupedPolygons = new ArrayList<>();
		Map<String, String> groupedPolygonsType = new HashMap<>();
		groupedPolygonsType.put("ID", "String");
		List<Map<String, Object>> groupedPolygonsAttr = new ArrayList<>();

		/*
		 * detecting for polygons which intersect with grouped reaches
		 */
		System.out.print("detecting....");
		for (int index = 0; index < groupedReachesGeoList.size(); index++) {

			// detecting polygons
			List<Geometry> temptGeoList = new ArrayList<>();
			List<String> selectedPolygons = new ArrayList<>();
			for (String polygonKey : totalPolygonsMap.keySet()) {
				if (totalPolygonsMap.get(polygonKey).Intersect(groupedReachesGeoList.get(index))) {
					temptGeoList.add(totalPolygonsMap.get(polygonKey));
					selectedPolygons.add(polygonKey);
				}
			}

			// remove selected polygons
			selectedPolygons.forEach(key -> totalPolygonsMap.remove(key));

			// setting output geometry
			groupedPolygons.add(GdalGlobal.mergePolygons(temptGeoList));

			// setting output attribute
			Map<String, Object> temptMap = new HashMap<>();
			temptMap.put("ID", groupedReachesAttr.get(index).get("ID"));
			groupedPolygonsAttr.add(temptMap);
		}

		/*
		 * merge all the other polygons which not intersect
		 */
		System.out.print("unGrouped.....");
		for (String polygonKey : totalPolygonsMap.keySet()) {
			groupedPolygons.add(totalPolygonsMap.get(polygonKey));

			Map<String, Object> temptMap = new HashMap<>();
			temptMap.put("ID", "UnGrouped");
			groupedPolygonsAttr.add(temptMap);
		}

		/*
		 * output grouped polygons
		 */
		new SpatialWriter().setAttribute(groupedPolygonsAttr).setFieldType(groupedPolygonsType)
				.setGeoList(groupedPolygons).saveAsShp(groupedWaterSheidSHP);
		if (mainStreamEPSG != waterSheidEPSG) {
			new SpatialReader(groupedWaterSheidSHP).saveAsShp(groupedWaterSheidSHP, mainStreamEPSG, waterSheidEPSG);
		}
	}

	private static void getCommand(String[] args) {
		Map<String, String> commandLine = new TreeMap<>();

		// setting default values
		commandLine.put("-bufferRadius", bufferRadius + "");
		commandLine.put("-mainStreamEPSG", mainStreamEPSG + "");
		commandLine.put("-waterSheidEPSG", waterSheidEPSG + "");
		commandLine.put("-groupedNodeEPSG", groupedNodeEPSG + "");

		// input user input
		for (int index = 0; index < args.length; index = index + 2) {
			commandLine.put(args[index], args[index + 1]);
		}

		// check
		if (!new File(commandLine.get("-mainStreamSHP")).exists())
			System.out.println("-mainStreamSHP file not exist");

		if (!new File(commandLine.get("-waterSheidSHP")).exists())
			System.out.println("-waterSheidSHP file not exist");

		if (!new File(commandLine.get("-otherStreamSHP")).exists())
			System.out.println("-mainStreamSHP file not exist");

		System.out.println("-mainStreamSHP : " + commandLine.get("-mainStreamSHP"));
		mainStreamSHP = commandLine.get("-mainStreamSHP");

		System.out.println("-bufferStreamShp : " + commandLine.get("-bufferStreamShp"));
		bufferStreamShp = commandLine.get("-bufferStreamShp");

		System.out.println("-waterSheidSHP : " + commandLine.get("-waterSheidSHP"));
		waterSheidSHP = commandLine.get("-waterSheidSHP");

		System.out.println("-otherStreamSHP : " + commandLine.get("-otherStreamSHP"));
		otherStreamSHP = commandLine.get("-otherStreamSHP");

		System.out.println("-groupedReachSHP : " + commandLine.get("-groupedReachSHP"));
		groupedReachSHP = commandLine.get("-groupedReachSHP");

		System.out.println("-groupedNodeShp : " + commandLine.get("-groupedNodeShp"));
		groupedNodeShp = commandLine.get("-groupedNodeShp");

		System.out.println("-groupedPolygonsShp : " + commandLine.get("-groupedNodeShp"));
		groupedPolygons = commandLine.get("-groupedPolygonsShp");

		System.out.println("-otherStreamEPSG : " + commandLine.get("-otherStreamEPSG"));
		otherStreamEPSG = Integer.parseInt(commandLine.get("-otherStreamEPSG"));

		System.out.println("-mainStreamEPSG : " + commandLine.get("-mainStreamEPSG"));
		mainStreamEPSG = Integer.parseInt(commandLine.get("-mainStreamEPSG"));

		System.out.println("-waterSheidEPSG : " + commandLine.get("-waterSheidEPSG"));
		waterSheidEPSG = Integer.parseInt(commandLine.get("-waterSheidEPSG"));

		System.out.println("-groupedReachEPSG : " + commandLine.get("-groupedReachEPSG"));
		groupedReachEPSG = Integer.parseInt(commandLine.get("-groupedReachEPSG"));

		System.out.println("-groupedNodeEPSG : " + commandLine.get("-groupedNodeEPSG"));
		groupedNodeEPSG = Integer.parseInt(commandLine.get("-groupedNodeEPSG"));

		System.out.println("-bufferRadius : " + commandLine.get("-bufferRadius"));
		bufferRadius = Double.parseDouble(commandLine.get("-bufferRadius"));

		System.out.println("-waterSheidMinArea : " + commandLine.get("-waterSheidMinArea"));
		waterSheidMinArea = Double.parseDouble(commandLine.get("-waterSheidMinArea"));

	}
}
