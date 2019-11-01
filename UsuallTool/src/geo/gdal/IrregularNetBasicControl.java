package geo.gdal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;

import usualTool.AtCommonMath;

public class IrregularNetBasicControl {

	private List<Geometry> geoList = new ArrayList<>();
	private int dataDecimal = 4;

	// key : Point index , from small to large
	private Map<String, FaceClass> faceMap = new TreeMap<>();

	// key : point index , from small to large
	private Map<String, EdgeClass> edgeMap = new TreeMap<>();

	// key : coordinateX + "_" + coodinateY
	private Map<String, NodeClass> nodeMap = new LinkedHashMap<>();
	private List<String> nodeList = new ArrayList<>();

	public IrregularNetBasicControl(List<Geometry> geoList) {
		this.geoList = geoList;
		this.processing();
	}

	public IrregularNetBasicControl(SpatialReader spatialFile) {
		this.geoList = spatialFile.getGeometryList();
		this.processing();
	}

	public IrregularNetBasicControl(List<Geometry> geoList, int dataDecimal) {
		this.dataDecimal = dataDecimal;
		this.geoList = geoList;
		this.processing();
	}

	public IrregularNetBasicControl(SpatialReader spatialFile, int dataDecimal) {
		this.dataDecimal = dataDecimal;
		this.geoList = spatialFile.getGeometryList();
		this.processing();
	}

	// <=====================================================>
	// preCheck
	// <=====================================================>
	private void preCheck() {
		// check is there multipolygon
		preCehck_MultiPolygon();
	}

	private void preCehck_MultiPolygon() {
		List<Geometry> temptGeoList = new ArrayList<>();
		this.geoList.forEach(geo -> {
			for (int index = 0; index < geo.GetGeometryCount(); index++) {
				temptGeoList.add(geo.GetGeometryRef(index));
			}
		});
		this.geoList.clear();
		this.geoList = temptGeoList;
	}

	// <=====================================================>
	// Processing
	// <=====================================================>
	private void sortedNodes() {
		// oder all points
		gdal.AllRegister();
		Set<String> temptNodeSet = new LinkedHashSet<>();
		for (Geometry geo : this.geoList) {
			for (int index = 0; index < geo.GetPointCount(); index++) {
				temptNodeSet.add(AtCommonMath.getDecimal_String(geo.GetX(index), this.dataDecimal) + "_"
						+ AtCommonMath.getDecimal_String(geo.GetY(index), this.dataDecimal));
			}
		}

		// create all node class
		for (String key : temptNodeSet) {
			String[] coordinate = key.split("_");
			this.nodeMap.put(key, new NodeClass());
			this.nodeMap.get(key).setX(Double.parseDouble(coordinate[0]));
			this.nodeMap.get(key).setY(Double.parseDouble(coordinate[1]));
			this.nodeMap.get(key).setIndex(this.nodeMap.size() - 1);
			this.nodeList.add(key);
		}
	}

	private void processing() {

		// check geoLists
		preCheck();

		// map every point and also create the list of it
		sortedNodes();

		// run all polygon
		for (Geometry geo : this.geoList) {

			FaceClass temptFace = new FaceClass();
			List<String> faceKeyList = new ArrayList<>();

			// startPoint
			String startPointKey = AtCommonMath.getDecimal_String(geo.GetX(0), this.dataDecimal) + "_"
					+ AtCommonMath.getDecimal_String(geo.GetY(0), this.dataDecimal);
			this.nodeMap.get(startPointKey).addFace(temptFace);

			// nextPoint
			for (int index = 1; index < geo.GetPointCount(); index++) {
				String currentPointKey = AtCommonMath.getDecimal_String(geo.GetX(index), this.dataDecimal) + "_"
						+ AtCommonMath.getDecimal_String(geo.GetY(index), this.dataDecimal);
				faceKeyList.add(String.valueOf(this.nodeMap.get(currentPointKey).getIndex()));
				this.nodeMap.get(currentPointKey).addFace(temptFace);

				// edge
				EdgeClass temptEdgeClass;
				List<String> edgeKeyList = Arrays.asList(this.nodeMap.get(startPointKey).getIndex() + "",
						this.nodeMap.get(currentPointKey).getIndex() + "");
				Collections.sort(edgeKeyList);
				String edgeKey = String.join("_", edgeKeyList);

				try {
					// check is there exist edgeClass
					// if exist extends the original one
					this.edgeMap.get(edgeKey).getKey();
					temptEdgeClass = this.edgeMap.get(edgeKey);
					temptEdgeClass.addFace(temptFace);

				} catch (Exception e) {
					// create a new edgeClass, input every property it needs
					temptEdgeClass = new EdgeClass();
					temptEdgeClass.setKey(edgeKey);
					temptEdgeClass.setCenterX(
							(this.nodeMap.get(startPointKey).getX() + this.nodeMap.get(currentPointKey).getX()) / 2);
					temptEdgeClass.setCenterY(
							(this.nodeMap.get(startPointKey).getY() + this.nodeMap.get(currentPointKey).getY()) / 2);
					temptEdgeClass.addFace(temptFace);
					temptEdgeClass.addNode(this.nodeMap.get(startPointKey));
					temptEdgeClass.addNode(this.nodeMap.get(currentPointKey));
				}
				this.edgeMap.put(edgeKey, temptEdgeClass);

				// linked face and node to edgeClass
				temptFace.addEdge(temptEdgeClass);
				this.nodeMap.get(startPointKey).addEdge(temptEdgeClass);
				this.nodeMap.get(currentPointKey).addEdge(temptEdgeClass);

				// refresh
				startPointKey = currentPointKey;
			}

			// face
			temptFace.setNodeOrder(faceKeyList);
			Collections.sort(faceKeyList);
			String faceKey = String.join("_", faceKeyList);
			Geometry centroid = geo.Centroid();
			temptFace.setCenterX(centroid.GetX());
			temptFace.setCenterY(centroid.GetY());
			temptFace.setGeometry(geo);
			temptFace.setArea(geo.Area());
			faceKeyList.forEach(index -> {
				temptFace.addNode(this.nodeMap.get(this.nodeList.get(Integer.parseInt(index))));
			});
			this.faceMap.put(faceKey, temptFace);
		}

	}

	// <=====================================================>
	// Function
	// <=====================================================>
	public List<NodeClass> getNodes() {
		List<NodeClass> outList = new ArrayList<>();
		this.nodeMap.keySet().forEach(key -> outList.add(this.nodeMap.get(key)));
		return outList;
	}

	public List<EdgeClass> getEdges() {
		List<EdgeClass> outList = new ArrayList<>();
		this.edgeMap.keySet().forEach(key -> outList.add(this.edgeMap.get(key)));
		return outList;
	}

	public List<FaceClass> getFaces() {
		List<FaceClass> outList = new ArrayList<>();
		this.faceMap.keySet().forEach(key -> outList.add(this.faceMap.get(key)));
		return outList;
	}

	// <=====================================================>
	// NetClass
	// <=====================================================>

	public class EdgeClass {
		public double nullValue = -999.0;
		private double centerX = nullValue;
		private double centerY = nullValue;
		private List<NodeClass> linkedNode = new ArrayList<>();
		private List<FaceClass> linkedFace = new ArrayList<>();
		private String key = "";

		public void setKey(String key) {
			this.key = key;
		}

		public void addNode(NodeClass node) {
			this.linkedNode.add(node);
			if (this.linkedNode.size() > 2) {
				try {
					throw new IOException("over 2 nodes in edge " + this.getKey());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void addFace(FaceClass face) {
			this.linkedFace.add(face);
			if (this.linkedNode.size() > 2) {
				try {
					throw new IOException("over 2 face in edge " + this.getKey());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void setCenterX(double centerX) {
			this.centerX = centerX;
		}

		public void setCenterY(double centerY) {
			this.centerY = centerY;
		}

		public List<NodeClass> getLinkedNode() {
			return this.linkedNode;
		}

		public List<FaceClass> getLinkedFace() {
			return this.linkedFace;
		}

		public double getCenterX() {
			return this.centerX;
		}

		public double getCenterY() {
			return this.centerY;
		}

		public String getKey() {
			return this.key;
		}
	}

	public class FaceClass {
		public double nullValue = -999.0;
		private double centerX = nullValue;
		private double centerY = nullValue;
		private double value = nullValue;
		private double area = nullValue;
		private Geometry geo = null;
		private List<Integer> nodeOrderIndex = new ArrayList<>();
		private List<EdgeClass> linkedEdge = new ArrayList<>();
		private List<NodeClass> linkedNode = new ArrayList<>();

		public void setNodeOrder(List<String> orderList) {
			orderList.forEach(s -> this.nodeOrderIndex.add(Integer.parseInt(s)));
		}

		public void setGeometry(Geometry geo) {
			this.geo = geo;
		}

		public void setArea(double area) {
			this.area = area;
		}

		public void addNode(NodeClass node) {
			this.linkedNode.add(node);
		}

		public void addEdge(EdgeClass edge) {
			this.linkedEdge.add(edge);
		}

		public void setCenterX(double centerX) {
			this.centerX = centerX;
		}

		public void setCenterY(double centerY) {
			this.centerY = centerY;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public List<NodeClass> getLinkedNode() {
			return this.linkedNode;
		}

		public List<EdgeClass> getLinkedEdge() {
			return this.linkedEdge;
		}

		public double getCenterX() {
			return this.centerX;
		}

		public double getCenterY() {
			return this.centerY;
		}

		public double getValue() {
			return this.value;
		}

		public double getArea() {
			return area;
		}

		public Geometry getGeo() {
			return this.geo;
		}

		public List<Integer> getNodeOrder() {
			return this.nodeOrderIndex;
		}
	}

	public class NodeClass {
		public double nullValue = -999.0;
		private double x = nullValue;
		private double y = nullValue;
		private double z = nullValue;
		private int index = -1;

		private List<EdgeClass> linkedEdge = new ArrayList<>();
		private List<FaceClass> linkedFace = new ArrayList<>();

		public void setIndex(int index) {
			this.index = index;
		}

		public void addFace(FaceClass face) {
			this.linkedFace.add(face);
		}

		public void addEdge(EdgeClass edge) {
			this.linkedEdge.add(edge);
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void setZ(double z) {
			this.z = z;
		}

		public double getX() {
			return this.x;
		}

		public double getY() {
			return this.y;
		}

		public double getZ() {
			return this.z;
		}

		public int getIndex() {
			return this.index;
		}

		public List<EdgeClass> getLinkedEdge() {
			return this.linkedEdge;
		}

		public List<FaceClass> getLinkedFace() {
			return this.linkedFace;
		}
	}

}
