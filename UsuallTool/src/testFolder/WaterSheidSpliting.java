package testFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
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

	private static String mainStreamFile = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\主流.shp";
	private static String mainStreamMergeFile = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\主流_merge.shp";
	private static String mainStreamBufferFile = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\主流_buffer.shp";

	private static String otherStreamFile = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_reach.shp";
	private static String otherStream_EndPointsInMainStream = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_Point_end.shp";
	private static String otherStream_IntersectPointInMainStream = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_point_intersect.shp";
	private static String otherStream_groupedByMainNodes = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_GroupedReach.shp";

	private static String waterSheidFile = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10.shp";

	private static int dataDecimale = IrregularReachBasicControl.dataDecimale;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

//		mainStreamMerge(mainStreamFile, mainStreamMergeFile);

//		mainStreamBuffer(mainStreamMergeFile, mainStreamBufferFile, 30);

//		getMainStreamNode(mainStreamBufferFile, otherStreamFile, otherSteam_IntersectPointInMainStream,
//				otherSteam_EndPointsInMainStream);

		getMainStreamGroup(otherStream_IntersectPointInMainStream, otherStreamFile, otherStream_groupedByMainNodes);
	}

	public static void getMainStreamGroup(String otherStream_IntersectPointInMainStream, String otherStreamFile,
			String groupedStreamShp) {

		/*
		 * initial class
		 */
		IrregularReachBasicControl otherStream = new IrregularReachBasicControl(otherStreamFile);
		Map<String, NodeClass> totalNodeMap = otherStream.getNodeMap();
		Map<String, EdgeClass> totalEdgeMap = otherStream.getEdgeMap();
		Map<String, NodeClass> mainStreamNodeMap = new HashMap<>();
		Map<String, EdgeClass> mainStreamEdgeMap = new HashMap<>();
		Map<String, EdgeClass> crossMainStreamEdgeMap = new HashMap<>();
		Map<String, EdgeClass> notMainStreamEdgeMap = new HashMap<>();

//		Map<String, NodeClass> remainNodeMap = new HashMap<>();
//		Map<String, EdgeClass> remainEdgeMap = new HashMap<>();

		List<Geometry> outGeometry = new ArrayList<>();

		// id will be x(4) + "_" + y(4)

		new SpatialReader(otherStream_IntersectPointInMainStream).getGeometryList().parallelStream().forEach(geo -> {
			String key = AtCommonMath.getDecimal_String(geo.GetX(), dataDecimale) + "_"
					+ AtCommonMath.getDecimal_String(geo.GetY(), dataDecimale);
			mainStreamNodeMap.put(key, totalNodeMap.get(key));
		});

		/*
		 * deploy edge into "in main stream" or "cross main stream"
		 * 
		 * (main stream buffer area)
		 */
		mainStreamNodeMap.keySet().forEach(key -> {
			NodeClass temptMainStreamNode = mainStreamNodeMap.get(key);
			temptMainStreamNode.getEdge().forEach(temptEdge -> {

				// check the other node of edge is in the main stream or not
				// if the other node is in mainStream
				// let's this edge to mainStreamEdgeMap
				// or make it to crossMainStreamEdgeMap
				try {

					// if in mainStream
					mainStreamNodeMap.get(temptEdge.getOtherNode(temptMainStreamNode)).getId();
					mainStreamEdgeMap.put(temptEdge.getId(), temptEdge);
				} catch (Exception e) {

					// if not in main stream
					crossMainStreamEdgeMap.put(temptEdge.getId(), temptEdge);
				}
			});
		});

		/*
		 * pick up remain edges that doesn't in "main stream" and "cross main stream"
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
		// get node from main stream, which is intersection
		crossMainStreamEdgeMap.keySet().forEach(crossMainStreamEdgeKey -> {
			EdgeClass crossMainStreamEdge = crossMainStreamEdgeMap.get(crossMainStreamEdgeKey);

			// get the node that not in main stream
			crossMainStreamEdge.getNode().forEach(temptNode -> {
				try {

					// if in mainStream, don't work
					mainStreamNodeMap.get(temptNode.getId()).getId();
				} catch (Exception e) {

					// if not in mainStream, starting grouping reaches
					List<Geometry> groupedGeometry = getGroupGeometry(temptNode, crossMainStreamEdge,
							notMainStreamEdgeMap);
					
					outGeometry.add(GdalGlobal.mergePolygons(groupedGeometry));
				}

			});
		});

		/*
		 * output shpfiles
		 */
		new SpatialWriter().setGeoList(outGeometry).saveAsShp(groupedStreamShp);
	}

	private static List<Geometry> getGroupGeometry(NodeClass startNode, EdgeClass directionEdge,
			Map<String, EdgeClass> remainEdges) {
		List<Geometry> outList = new ArrayList<>();

		NodeClass nextNode = directionEdge.getOtherNode(startNode);
		try {
			// try the node is exist or not
			remainEdges.get(directionEdge.getId()).getId();

			// if it isn't exist
			remainEdges.put(directionEdge.getId(), directionEdge);

			// if it's not in main stream
			// run grouping process
			if (nextNode.getEdge().size() != 1) {
				nextNode.getOtherEdges(directionEdge).forEach(otherEdge -> {
					getGroupGeometry(nextNode, otherEdge, remainEdges).forEach(geo -> outList.add(geo));
				});

			} else if (nextNode.getEdge().size() == 1) {
				outList.add(directionEdge.getGeo());

			} else {
				System.out.println(new Exception("node error , id : " + nextNode.getId()));
			}
		} catch (Exception e) {
		}

		return outList;
	}

	public static void getMainStreamNode(String mainStreamBuffer, String otherStream, String outptIntersectPointsShp,
			String outputEndPointShp) {
		Geometry bufferArea = new SpatialReader(mainStreamBuffer).getGeometryList().get(0);
		IrregularReachBasicControl reach = new IrregularReachBasicControl(otherStream);

		List<NodeClass> nodeList = reach.getNodeList();
		List<Geometry> endPoints = new ArrayList<>();
		List<Geometry> intersectPoints = new ArrayList<>();

		nodeList.forEach(node -> {
			System.out.println(node.getIndex());
			// for endPoints
			if (node.getEdge().size() == 1) {
				Geometry point = node.getGeo();
				if (bufferArea.Contains(point)) {
					endPoints.add(node.getGeo());
				}
			}

			// for intersectPoints
			if (node.getEdge().size() > 2) {
				Geometry point = node.getGeo();
				if (bufferArea.Contains(point)) {
					intersectPoints.add(node.getGeo());
				}
			}
		});

		new SpatialWriter().setCoordinateSystem(SpatialWriter.TWD97_121).setGeoList(endPoints)
				.saveAsShp(outputEndPointShp);
		new SpatialWriter().setCoordinateSystem(SpatialWriter.TWD97_121).setGeoList(intersectPoints)
				.saveAsShp(outptIntersectPointsShp);
	}

	public static void mainStreamBuffer(String inputShp, String outputShp, double bufferDistance) {
		List<Geometry> mainStreamList = new SpatialReader(inputShp).getGeometryList();
		mainStreamList = mainStreamList.parallelStream().map(geo -> geo.Buffer(bufferDistance))
				.collect(Collectors.toList());
		new SpatialWriter().setCoordinateSystem(SpatialWriter.TWD97_121).setGeoList(mainStreamList)
				.saveAsShp(outputShp);
	}

	public static void mainStreamMerge(String inputShp, String outputShp) {
		List<Geometry> mainStreamList = new SpatialReader(inputShp).getGeometryList();

		while (mainStreamList.size() != 1) {
			List<Geometry> temptList = new ArrayList<>();

			for (int index = 0; index < mainStreamList.size(); index = index + 2) {
				try {
					temptList.add(mainStreamList.get(index).Union((mainStreamList.get(index + 1))));
				} catch (Exception e) {
					temptList.add(mainStreamList.get(index));
				}
			}
			mainStreamList = temptList;
			System.out.println(mainStreamList.size());
		}

		new SpatialWriter().setCoordinateSystem(SpatialWriter.TWD97_121).setGeoList(mainStreamList)
				.saveAsShp(outputShp);
	}
}
