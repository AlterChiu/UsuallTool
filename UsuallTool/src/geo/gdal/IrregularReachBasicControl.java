package geo.gdal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import usualTool.AtCommonMath;

public class IrregularReachBasicControl {

	public static int dataDecimale = 4;
	private List<Geometry> geoList = new ArrayList<>();

	// node key : x + "_" + y
	private List<String> nodesList = new ArrayList<>();
	private Map<String, NodeClass> nodeMap = new HashMap<>();

	// edge key : index(X) + "_" + index(Y)
	private Map<String, EdgeClass> edgeMap = new HashMap<>();

	/*
	 * <=============== Constructor ==========================>
	 */
	public IrregularReachBasicControl(String fileAdd) {
		this.geoList = new SpatialReader(fileAdd).getGeometryList();
		process();
	}

	public IrregularReachBasicControl(List<Geometry> geoList) {
		process();
	}

	public IrregularReachBasicControl(String fileAdd, Boolean regeneratGeometry) {
		if (regeneratGeometry)
			geoListRegenerate();
		this.geoList = new SpatialReader(fileAdd).getGeometryList();
		process();
	}

	public IrregularReachBasicControl(List<Geometry> geoList, Boolean regeneratGeometry) {
		if (regeneratGeometry)
			geoListRegenerate();
		process();
	}

	// make sure each reach is end in EndPoint
	// (point linked to only 1 reach or more than (>=) 3 );
	private void geoListRegenerate() {
		List<Geometry> outList = new ArrayList<>();

		// merge all geometry to multiStringLine
		Geometry mergeLine = GdalGlobal.mergePolygons(this.geoList);

		// split multiStringLine to several stringLine
		for (int index = 0; index < mergeLine.GetGeometryCount(); index++) {
			outList.add(mergeLine.GetGeometryRef(index));
		}

		this.geoList.clear();
		this.geoList = outList;
	}

	private void process() {

		// detect is multiLineString
		List<Geometry> temptGeoList = new ArrayList<>();
		this.geoList.forEach(geometry -> {

			if (geometry.GetGeometryName().toUpperCase().equals("LINESTRING")) {
				temptGeoList.add(geometry);

			} else if (geometry.GetGeometryName().toUpperCase().equals("MULTILINESTRING")) {
				for (int muliLineStringIndex = 0; muliLineStringIndex < geometry
						.GetGeometryCount(); muliLineStringIndex++) {
					temptGeoList.add(geometry.GetGeometryRef(muliLineStringIndex).Boundary());
				}
			}
		});
		this.geoList.clear();
		this.geoList = temptGeoList;

		// sorted end point and start point
		Set<String> pointsSet = new HashSet<>();
		this.geoList.forEach(geo -> {
			for (int index = 0; index < geo.GetPointCount(); index++) {
				String xString = AtCommonMath.getDecimal_String(geo.GetX(index), dataDecimale);
				String yString = AtCommonMath.getDecimal_String(geo.GetY(index), dataDecimale);
				pointsSet.add(xString + "_" + yString);
			}
		});

		// establish nodesList and nodesMap
		this.nodesList = new ArrayList<>(pointsSet);
		for (int index = 0; index < this.nodesList.size(); index++) {
			this.nodeMap.put(this.nodesList.get(index), new NodeClass());
			this.nodeMap.get(this.nodesList.get(index)).setIndex(index);
			this.nodeMap.get(this.nodesList.get(index)).setName(this.nodesList.get(index));
		}

		// link each points to reach
		for (int index = 0; index < this.geoList.size(); index++) {

			// get start point
			String startPoint = AtCommonMath.getDecimal_String(this.geoList.get(index).GetX(0), dataDecimale) + "_"
					+ AtCommonMath.getDecimal_String(this.geoList.get(index).GetY(0), dataDecimale);

			// loop for others point
			for (int pointIndex = 1; pointIndex < this.geoList.get(index).GetPointCount(); pointIndex++) {
				String nextPoint = AtCommonMath.getDecimal_String(this.geoList.get(index).GetX(pointIndex),
						dataDecimale) + "_"
						+ AtCommonMath.getDecimal_String(this.geoList.get(index).GetY(pointIndex), dataDecimale);

				// skip same point
				if (!startPoint.equals(nextPoint)) {

					// setting edgeName
					List<Integer> temptEdgePointIndex = new ArrayList<>(Arrays
							.asList(this.nodeMap.get(startPoint).getIndex(), this.nodeMap.get(nextPoint).getIndex()));
					Collections.sort(temptEdgePointIndex);
					String edgeName = String.join("_", temptEdgePointIndex.parallelStream().map(d -> String.valueOf(d))
							.collect(Collectors.toList()));

					// establish edge class
					this.edgeMap.put(edgeName, new EdgeClass());
					this.edgeMap.get(edgeName).setIndex(index);
					this.edgeMap.get(edgeName).setName(edgeName);
					this.edgeMap.get(edgeName).addNode(this.nodeMap.get(startPoint));
					this.edgeMap.get(edgeName).addNode(this.nodeMap.get(nextPoint));

					// link nodeClass in edgeClass
					this.nodeMap.get(startPoint).addEdge(this.edgeMap.get(edgeName));
					this.nodeMap.get(nextPoint).addEdge(this.edgeMap.get(edgeName));

					// next point
					startPoint = nextPoint;
				}
			}
		}

	}

	/*
	 * <=============== UserFunction ==========================>
	 */

	public List<Geometry> getGeometry() {
		return this.geoList;
	}

	public List<Geometry> getReLinkedEdge() {
		List<Geometry> outList = new ArrayList<>();

		// node control
		Map<String, Integer> nodeUsedCount = new HashMap<>(); // key=nodeName , value = usedCount
		this.nodeMap.keySet().forEach(nodeID -> {
			if (this.nodeMap.get(nodeID).isEndPoint())
				nodeUsedCount.put(nodeID, 0);
		});

		this.nodesList.forEach(nodeID -> {
			NodeClass node = this.nodeMap.get(nodeID);

			// if is endPoint, and not used, start detecting
			if (node.isEndPoint() && nodeUsedCount.get(node.getId()) < node.getEdgeSize()) {

				// check node used count
				node.getEdge().forEach(linkedEdge -> {

					// get start point
					List<Double[]> groupedLineString = new ArrayList<>();
					groupedLineString.add(new Double[] { node.getX(), node.getY() });

					// detecting other node
					EdgeClass currentEdge = linkedEdge;
					NodeClass currentNode = node;
					NodeClass otherNode = currentEdge.getOtherNode(currentNode);

					while (!otherNode.isEndPoint()) {
						groupedLineString.add(new Double[] { otherNode.getX(), otherNode.getY() });
						currentEdge = otherNode.getOtherEdges(currentEdge).get(0);
						currentNode = otherNode;
						otherNode = currentEdge.getOtherNode(otherNode);
					}
					groupedLineString.add(new Double[] { otherNode.getX(), otherNode.getY() });
					outList.add(GdalGlobal.CreateLine(groupedLineString));

					// end, add node used count to map
					nodeUsedCount.put(otherNode.getId(), nodeUsedCount.get(otherNode.getId()) + 1);
					nodeUsedCount.put(node.getId(), nodeUsedCount.get(node.getId()) + 1);
				});
			}
		});
		return outList;
	}

	public List<NodeClass> getNodeList() {
		return this.nodeMap.keySet().parallelStream().map(key -> this.nodeMap.get(key)).collect(Collectors.toList());
	}

	public List<EdgeClass> getEdgeList() {
		return this.edgeMap.keySet().parallelStream().map(key -> this.edgeMap.get(key)).collect(Collectors.toList());
	}

	public Map<String, NodeClass> getNodeMap() {
		return this.nodeMap;
	}

	public Map<String, EdgeClass> getEdgeMap() {
		return this.edgeMap;
	}

	/*
	 * <=============== private class ==========================>
	 */
	public class NodeClass {
		private Set<EdgeClass> edgeList = new HashSet<>();
		private String id = "";
		private int index = -1;
		private int groupIndex = -1;
		private double x = -999;
		private double y = -999;

		public NodeClass() {

		}

		public NodeClass(double x, double y) {
			String id1 = AtCommonMath.getDecimal_String(x, dataDecimale) + "_"
					+ AtCommonMath.getDecimal_String(y, dataDecimale);

			this.id = id1;
			this.x = x;
			this.y = y;
		}

		public void addEdge(EdgeClass edge) {
			this.edgeList.add(edge);
		}

		public void setName(String name) {
			this.id = name;
			String[] split = name.split("_");
			this.x = Double.parseDouble(split[0]);
			this.y = Double.parseDouble(split[1]);
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setGroupIndex(int index) {
			this.groupIndex = index;
		}

		public List<EdgeClass> getEdge() {
			return new ArrayList<>(edgeList);
		}

		public String getId() {
			return this.id;
		}

		public int getIndex() {
			return this.index;
		}

		public int getGroudIndex() {
			return this.groupIndex;
		}

		public double getX() {
			return this.x;
		}

		public double getY() {
			return this.y;
		}

		public Boolean isEndPoint() {
			if (this.edgeList.size() != 0) {
				if (this.edgeList.size() != 2) {
					return true;
				} else {
					return false;
				}
			} else {
				new Exception("none edge to detect");
				return false;
			}
		}

		public int getEdgeSize() {
			return this.edgeList.size();
		}

		public Geometry getGeo() {
			return GdalGlobal.pointToGeometry(new Double[] { this.x, this.y });
		}

		public List<EdgeClass> getOtherEdges(EdgeClass edge) {
			List<EdgeClass> outList = new ArrayList<>();
			this.edgeList.forEach(temptEdge -> {
				if (edge != temptEdge) {
					outList.add(temptEdge);
				}
			});
			return outList;
		}

		public List<Geometry> getGroupGeomtry(NodeClass startNode, EdgeClass directionEdge) {
			return getGroupGeometry_cooperate(startNode, directionEdge);
		}
	}

	public class EdgeClass {
		private Set<NodeClass> nodeList = new HashSet<>();
		private String id = "";
		private int index = -1;
		private int groupIndex = -1;
		private Geometry geo = null;

		public void addNode(NodeClass node) {
			this.nodeList.add(node);
		}

		public void setName(String name) {
			this.id = name;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setGroupIndex(int index) {
			this.groupIndex = index;
		}

		public List<NodeClass> getNode() {
			return new ArrayList<>(nodeList);
		}

		public String getId() {
			return this.id;
		}

		public int getIndex() {
			return this.index;
		}

		public int getGroupIndex() {
			return this.groupIndex;
		}

		public Geometry getGeo() {
			if (this.geo == null) {
				List<Double[]> points = new ArrayList<>();
				this.nodeList.forEach(node -> {
					points.add(new Double[] { node.getX(), node.getY() });
				});
				this.geo = GdalGlobal.CreateLine(points);
			}

			return this.geo;
		}

		public int getNodeSize() {
			return this.nodeList.size();
		}

		public NodeClass getOtherNode(NodeClass node) {
			List<NodeClass> temptList = new ArrayList<>(this.nodeList);
			if (temptList.get(0) == node) {
				return temptList.get(1);
			} else {
				return temptList.get(0);
			}
		}

		public double getLength() {
			return this.getGeo().Length();
		}

		public List<Geometry> getGroupGeomtry(NodeClass startNode, EdgeClass directionEdge) {
			return getGroupGeometry_cooperate(startNode, directionEdge);
		}
	}

	protected List<Geometry> getGroupGeometry_cooperate(NodeClass startNode, EdgeClass directionEdge) {
		List<Geometry> outList = new ArrayList<>();

		NodeClass nextNode = directionEdge.getOtherNode(startNode);

		// if this node isn't the end point
		if (nextNode.getEdge().size() != 1) {
			nextNode.getOtherEdges(directionEdge).forEach(otherEdge -> {
				getGroupGeometry_cooperate(nextNode, otherEdge).forEach(geo -> outList.add(geo));
			});

		} else {
			new Exception("node " + nextNode.getId() + " is the end point");
		}

		return outList;
	}
}
