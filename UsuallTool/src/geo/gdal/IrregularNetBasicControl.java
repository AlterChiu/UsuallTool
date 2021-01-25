package geo.gdal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gdal.ogr.Geometry;

import usualTool.AtCommonMath;

public class IrregularNetBasicControl {
	private int dataDecimal = 4;
	private List<Geometry> geoList = new ArrayList<>();;

	// key : Point index , from small to large
	private Map<String, FaceClass> faceMap = new LinkedHashMap<>();
	private List<String> faceList = new ArrayList<>();

	// key : point index , from small to large
	private Map<String, EdgeClass> edgeMap = new LinkedHashMap<>();

	// key : coordinateX + "_" + coodinateY
	private Map<String, NodeClass> nodeMap = new LinkedHashMap<>();
	private List<String> nodeList = new ArrayList<>();

	public IrregularNetBasicControl(List<Geometry> geoList) {
		geoList.forEach(geo -> this.geoList.add(geo));
		this.processing();
	}

	public IrregularNetBasicControl(SpatialReader spatialFile) {
		spatialFile.getGeometryList().forEach(geo -> this.geoList.add(geo));
		this.processing();
	}

	public IrregularNetBasicControl(List<Geometry> geoList, int dataDecimal) {
		geoList.forEach(geo -> this.geoList.add(geo));
		this.dataDecimal = dataDecimal;
		this.processing();
	}

	public IrregularNetBasicControl(SpatialReader spatialFile, int dataDecimal) {
		spatialFile.getGeometryList().forEach(geo -> this.geoList.add(geo));
		this.dataDecimal = dataDecimal;
		this.processing();
	}

	// <=====================================================>
	// preCheck
	// <=====================================================>
	private List<Geometry> preCheck(List<Geometry> geoList) {
		// check is there multipolygon
		geoList = preCehck_MultiPolygon(geoList);
		return geoList;
	}

	private List<Geometry> preCehck_MultiPolygon(List<Geometry> geoList) {
		List<Geometry> temptGeoList = new ArrayList<>();
		geoList.forEach(geo -> {
			GdalGlobal.MultiPolyToSingle(geo).forEach(splitGeo -> temptGeoList.add(splitGeo));
		});
		geoList.clear();
		geoList = temptGeoList;
		return geoList;
	}

	// <=====================================================>
	// Processing
	// <=====================================================>
	private void sortedNodes(List<Geometry> geoList) {
		Map<String, Double> coordinate_Z = new HashMap<>();

		geoList.forEach(geometry -> {
			GdalGlobal.MultiPolyToSingle(geometry).forEach(geo -> {
				for (int geoCount = 0; geoCount < geometry.GetGeometryCount(); geoCount++) {

					Geometry temptGeo = geo.GetGeometryRef(geoCount);
					for (double[] point : temptGeo.GetPoints()) {
						String xString = AtCommonMath.getDecimal_String(point[0], dataDecimal);
						String yString = AtCommonMath.getDecimal_String(point[1], dataDecimal);
						String key = xString + "_" + yString;

						double zString = 0.;
						try {
							zString = point[2];
						} catch (Exception e) {
						}
						coordinate_Z.put(key, zString);
					}
				}
			});
		});

		// create all node class
		for (String key : coordinate_Z.keySet()) {
			String[] coordinate = key.split("_");
			this.nodeMap.put(key, new NodeClass());
			this.nodeMap.get(key).setX(Double.parseDouble(coordinate[0]));
			this.nodeMap.get(key).setY(Double.parseDouble(coordinate[1]));
			this.nodeMap.get(key).setZ(coordinate_Z.get(key));

			this.nodeMap.get(key).setIndex(this.nodeMap.size() - 1);
			this.nodeMap.get(key).setKey(key);
			this.nodeList.add(key);
		}
	}

	private void processing() {

		// check geoLists
		this.geoList = preCheck(this.geoList);

		// map every point and also create the list of it
		sortedNodes(geoList);

		// run all polygon
		for (Geometry temptGeo : geoList) {
			if (temptGeo.GetGeometryCount() == 1) {

				Geometry geo = temptGeo.GetGeometryRef(0);
				FaceClass temptFace = new FaceClass();
				List<String> faceKeyList = new ArrayList<>();

				// startPoint
				String startPointKey = AtCommonMath.getDecimal_String(geo.GetX(0), this.dataDecimal) + "_"
						+ AtCommonMath.getDecimal_String(geo.GetY(0), this.dataDecimal);

				// nextPoint
				for (int index = 1; index <= geo.GetPointCount(); index++) {

					// check for the last point to start point
					String currentPointKey;
					if (index == geo.GetPointCount()) {
						currentPointKey = AtCommonMath.getDecimal_String(geo.GetX(0), this.dataDecimal) + "_"
								+ AtCommonMath.getDecimal_String(geo.GetY(0), this.dataDecimal);
					} else {
						currentPointKey = AtCommonMath.getDecimal_String(geo.GetX(index), this.dataDecimal) + "_"
								+ AtCommonMath.getDecimal_String(geo.GetY(index), this.dataDecimal);
					}

					if (!currentPointKey.equals(startPointKey)) {
						faceKeyList.add(String.valueOf(this.nodeMap.get(currentPointKey).getIndex()));
						this.nodeMap.get(currentPointKey).addFace(temptFace);

						// edge
						List<String> edgeKeyList = Arrays.asList(this.nodeMap.get(startPointKey).getIndex() + "",
								this.nodeMap.get(currentPointKey).getIndex() + "");
						Collections.sort(edgeKeyList);
						String edgeKey = String.join("_", edgeKeyList);

						EdgeClass temptEdgeClass = Optional.ofNullable(this.edgeMap.get(edgeKey))
								.orElse(new EdgeClass());
						temptEdgeClass.setKey(edgeKey);
						temptEdgeClass.addFace(temptFace);
						temptEdgeClass.addNode(this.nodeMap.get(startPointKey));
						temptEdgeClass.addNode(this.nodeMap.get(currentPointKey));
						this.edgeMap.put(edgeKey, temptEdgeClass);

						// linked face and node to edgeClass
						temptFace.addEdge(temptEdgeClass);
						this.nodeMap.get(startPointKey).addEdge(temptEdgeClass);
						this.nodeMap.get(currentPointKey).addEdge(temptEdgeClass);
					}

					// refresh
					startPointKey = currentPointKey;
				}

				// face
				faceKeyList.forEach(index -> {
					temptFace.addNode(this.nodeMap.get(this.nodeList.get(Integer.parseInt(index))));
				});

				Geometry centroid = geo.Centroid();
				temptFace.setIndex(this.faceList.size());
				temptFace.setCenterX(centroid.GetX());
				temptFace.setCenterY(centroid.GetY());

				Collections.sort(faceKeyList);
				String faceKey = String.join("_", faceKeyList);
				temptFace.setFaceKey(faceKey);
				this.faceMap.put(faceKey, temptFace);
				this.faceList.add(faceKey);
			} else {
				new Exception("Skip Linearing while IrregularNetBasicControl");
			}
		}
	}

	// <=====================================================>
	// Function
	// <=====================================================>
	public Map<String, NodeClass> getNodeMap() {
		return this.nodeMap;
	}

	public Map<String, EdgeClass> getEdgeMap() {
		return this.edgeMap;
	}

	public Map<String, FaceClass> getFaceMap() {
		return this.faceMap;
	}

	public List<NodeClass> getNodesList() {
		return this.nodeMap.keySet().parallelStream().map(key -> this.nodeMap.get(key)).collect(Collectors.toList());
	}

	public List<EdgeClass> getEdgesList() {
		return this.edgeMap.keySet().parallelStream().map(key -> this.edgeMap.get(key)).collect(Collectors.toList());
	}

	public List<FaceClass> getFaceList() {
		return this.faceMap.keySet().parallelStream().map(key -> this.faceMap.get(key)).collect(Collectors.toList());
	}

	public int getDataDecimal() {
		return this.dataDecimal;
	}

	public void exportNodeAsGeoJson(String saveAdd) {
		exportNode(saveAdd, SpatialWriter.SAVINGTYPE_GeoJson);
	}

	public void exportNodeAsShp(String saveAdd) {
		exportNode(saveAdd, SpatialWriter.SAVINGTYPE_SHP);
	}

	private void exportNode(String saveAdd, String saveTyping) {
		List<Geometry> geoList = new ArrayList<>();
		this.nodeMap.keySet().forEach(key -> {
			NodeClass node = this.nodeMap.get(key);
			geoList.add(GdalGlobal.CreatePoint(node.getX(), node.getY()));
		});
		export(saveAdd, geoList, saveTyping);
	}

	public void exportFaceAsGeoJson(String saveAdd) {
		exportFace(saveAdd, SpatialWriter.SAVINGTYPE_GeoJson);
	}

	public void exportFaceAsShp(String saveAdd) {
		exportFace(saveAdd, SpatialWriter.SAVINGTYPE_SHP);
	}

	private void exportFace(String saveAdd, String saveTyping) {
		List<Geometry> geoList = new ArrayList<>();
		this.faceMap.keySet().forEach(key -> {
			geoList.add(this.faceMap.get(key).getGeo());
		});
		export(saveAdd, geoList, saveTyping);
	}

	public void exportEdgeAsGeoJson(String saveAdd) {
		exportEdge(saveAdd, SpatialWriter.SAVINGTYPE_GeoJson);
	}

	public void exportEdgeAsShp(String saveAdd) {
		exportEdge(saveAdd, SpatialWriter.SAVINGTYPE_SHP);
	}

	private void exportEdge(String saveAdd, String saveTyping) {
		List<Geometry> geoList = new ArrayList<>();
		this.edgeMap.keySet().forEach(key -> {
			List<Double[]> points = new ArrayList<>();
			this.edgeMap.get(key).getLinkedNode().forEach(node -> {
				points.add(new Double[] { node.getX(), node.getY(), node.getZ() });
			});
			geoList.add(GdalGlobal.CreateLineString(points));
		});

		export(saveAdd, geoList, saveTyping);
	}

	private void export(String saveAdd, List<Geometry> geoList, String saceTyping) {
		new SpatialWriter().setGeoList(geoList).saceAs(saveAdd, saceTyping);
	}

	public List<Geometry> getAllPolygon() {
		List<Geometry> temptGeo = new ArrayList<>();
		this.faceMap.keySet().forEach(key -> temptGeo.add(this.faceMap.get(key).getGeo()));
		return temptGeo;
	}

	// <=====================================================>
	// NetClass
	// <=====================================================>
	public class EdgeClass {
		public double nullValue = -999.0;
		private Set<NodeClass> linkedNode = new LinkedHashSet<>();
		private Set<FaceClass> linkedFace = new LinkedHashSet<>();
		private Set<EdgeClass> linkedEdge = null;
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

		public List<NodeClass> getLinkedNode() {
			return new ArrayList<>(this.linkedNode);
		}

		public List<FaceClass> getLinkedFace() {
			return new ArrayList<>(this.linkedFace);
		}

		public List<EdgeClass> getLinkedEdge() {
			if (linkedEdge == null) {
				Set<EdgeClass> outEdgeList = new LinkedHashSet<>();
				this.linkedNode.forEach(node -> {
					node.getOtherEdge(this).forEach(temptEdge -> {
						outEdgeList.add(temptEdge);
					});
				});
				this.linkedEdge = outEdgeList;
			}
			return new ArrayList<>(linkedEdge);
		}

		public double getCenterX() {
			return AtCommonMath.getDecimal_Double(
					(this.getLinkedNode().get(0).getX() + this.getLinkedNode().get(1).getX()) / 2, dataDecimal);
		}

		public double getCenterY() {
			return AtCommonMath.getDecimal_Double(
					(this.getLinkedNode().get(0).getY() + this.getLinkedNode().get(1).getY()) / 2, dataDecimal);
		}

		public NodeClass getOtherNode(NodeClass temptNode) {
			List<NodeClass> temptNodeList = new ArrayList<>(this.linkedNode);
			if (!this.isContain(temptNode)) {
				new Exception("no such node is containd in edge");
				return null;
			} else {
				if (temptNodeList.get(0).getIndex() == temptNode.getIndex()) {
					return temptNodeList.get(1);
				} else {
					return temptNodeList.get(0);
				}
			}
		}

		public FaceClass getOtherFace(FaceClass temptFace) {
			List<FaceClass> temptFaceList = new ArrayList<>(this.linkedFace);
			if (!this.isLinked(temptFace)) {
				new Exception("no such face is linked to edge");
				return null;
			} else {
				if (temptFaceList.get(0).getIndex() == temptFace.getIndex()) {
					return temptFaceList.get(1);
				} else {
					return temptFaceList.get(0);
				}
			}
		}

		public String getKey() {
			return this.key;
		}

		public double getLength() {
			return this.getGeo().Length();
		}

		public Geometry getGeo() {
			List<Double[]> temptList = new ArrayList<>();
			this.linkedNode.forEach(node -> {
				temptList.add(new Double[] { node.getX(), node.getY(), node.getZ() });
			});
			return GdalGlobal.CreateLineString(temptList);
		}

		public boolean isContain(NodeClass temptNodeClass) {
			boolean returnBoolean = false;
			for (NodeClass node : this.linkedNode) {
				if (temptNodeClass.getIndex() == node.getIndex()) {
					returnBoolean = true;
					break;
				}
			}
			return returnBoolean;
		}

		public boolean isLinked(EdgeClass temptEdgeClass) {
			getLinkedEdge();

			boolean returnBoolean = false;
			for (EdgeClass edge : this.linkedEdge) {
				if (temptEdgeClass.getKey().equals(edge.getKey())) {
					returnBoolean = true;
					break;
				}
			}
			return returnBoolean;
		}

		public boolean isLinked(FaceClass temptFaceClass) {
			boolean returnBoolean = false;
			for (FaceClass face : this.linkedFace) {
				if (temptFaceClass.getFaceKey().equals(face.getFaceKey())) {
					returnBoolean = true;
					break;
				}
			}
			return returnBoolean;
		}
	}

	public class FaceClass {
		public double nullValue = -999.0;
		private double centerX = nullValue;
		private double centerY = nullValue;
		private double value = nullValue;
		private String faceKey = "";
		private Set<EdgeClass> linkedEdge = new LinkedHashSet<>();
		private Set<NodeClass> linkedNode = new LinkedHashSet<>();
		private Set<FaceClass> linkedFace = null;
		private int index = -1;

		public void setFaceKey(String key) {
			this.faceKey = key;
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

		public void setIndex(int index) {
			this.index = index;
		}

		public List<NodeClass> getLinkedNode() {
			return new ArrayList<>(this.linkedNode);
		}

		public List<EdgeClass> getLinkedEdge() {
			return new ArrayList<>(this.linkedEdge);
		}

		public List<FaceClass> getLinkedFace() {
			if (this.linkedFace == null) {
				Set<FaceClass> temptList = new HashSet<>();

				for (EdgeClass temptEdge : this.linkedEdge) {
					for (FaceClass otherFace : temptEdge.getLinkedFace()) {
						if (otherFace != this) {
							temptList.add(otherFace);
						}
					}
				}
				this.linkedFace = temptList;
			}
			return new ArrayList<>(this.linkedFace);
		}

		public List<EdgeClass> getLinkedEdge(FaceClass faceClass) {
			List<EdgeClass> outList = new ArrayList<>();
			this.linkedEdge.forEach(edge -> {
				if (faceClass.isContain(edge)) {
					outList.add(edge);
				}
			});
			return outList;
		}

		public List<NodeClass> getOtherNode(NodeClass temptNode) {
			List<NodeClass> outList = this.getLinkedNode();
			try {
				outList.remove(temptNode);
			} catch (Exception e) {
			}
			return outList;
		}

		public List<EdgeClass> getOtherEdge(EdgeClass temptEdge) {
			List<EdgeClass> outList = this.getLinkedEdge();
			try {
				outList.remove(temptEdge);
			} catch (Exception e) {
			}
			return outList;
		}

		public List<FaceClass> getOtherFace(FaceClass temptFace) {
			List<FaceClass> outList = this.getLinkedFace();
			try {
				outList.remove(temptFace);
			} catch (Exception e) {
			}
			return outList;
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
			return this.getGeo().Area();
		}

		public Geometry getGeo() {
			List<NodeClass> nodeList = new ArrayList<>(this.linkedNode);
			List<Double[]> outList = new ArrayList<>();
			nodeList.forEach(node -> {
				outList.add(new Double[] { node.getX(), node.getY(), node.getZ() });
			});
			outList.add(new Double[] { nodeList.get(0).getX(), nodeList.get(0).getY(), nodeList.get(0).getZ() });

			return GdalGlobal.CreatePolygon(outList);
		}

		public String getFaceKey() {
			return this.faceKey;
		}

		public int getIndex() {
			return this.index;
		}

		public boolean isContain(EdgeClass edgeClass) {
			boolean returnBoolean = false;
			for (EdgeClass edge : this.linkedEdge) {
				if (edge.getKey().equals(edgeClass.getKey())) {
					returnBoolean = true;
					break;
				}
			}
			return returnBoolean;
		}

		public boolean isContain(NodeClass nodeClass) {
			boolean returnBoolean = false;
			for (NodeClass node : this.linkedNode) {
				if (node.getIndex() == nodeClass.getIndex()) {
					returnBoolean = true;
					break;
				}
			}
			return returnBoolean;
		}

		public boolean isEnd() {
			if (this.getLinkedFace().size() <= 1) {
				return true;
			} else {
				return false;
			}
		}

	}

	public class NodeClass {
		public double nullValue = -999.0;
		private double x = nullValue;
		private double y = nullValue;
		private double z = nullValue;
		private int index = -1;
		private String key = null;

		private Set<EdgeClass> linkedEdge = new HashSet<>();
		private Set<FaceClass> linkedFace = new HashSet<>();

		public void setIndex(int index) {
			this.index = index;
		}

		public void setKey(String key) {
			this.key = key;
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

		public String getKey() {
			return this.key;
		}

		public Geometry getGeo() {
			return GdalGlobal.CreatePoint(this.x, this.y, this.z);
		}

		public List<NodeClass> getLinkedNode() {
			List<NodeClass> nodeList = new ArrayList<>();
			this.getLinkedEdge().forEach(edge -> {
				Optional.ofNullable(edge.getOtherNode(this)).ifPresent(node -> nodeList.add(node));
			});
			return nodeList;
		}

		public List<EdgeClass> getLinkedEdge() {
			return new ArrayList<>(this.linkedEdge);
		}

		public List<FaceClass> getLinkedFace() {
			return new ArrayList<>(this.linkedFace);
		}

		public List<FaceClass> getOtherFace(FaceClass temptFaceClass) {
			Set<FaceClass> outFaceList = new HashSet<>(this.linkedFace);
			try {
				outFaceList.remove(temptFaceClass);
			} catch (Exception e) {
			}
			return new ArrayList<>(outFaceList);
		}

		public List<EdgeClass> getOtherEdge(EdgeClass temptEdgeClass) {
			Set<EdgeClass> outEdgeList = new HashSet<>(this.linkedEdge);
			try {
				outEdgeList.remove(temptEdgeClass);
			} catch (Exception e) {
			}

			return new ArrayList<>(outEdgeList);
		}

		public List<NodeClass> getOtherNode(NodeClass temptNodeClass) {
			List<NodeClass> outList = this.getLinkedNode();
			try {
				outList.remove(temptNodeClass);
			} catch (Exception e) {
			}
			return outList;
		}

	}

}
