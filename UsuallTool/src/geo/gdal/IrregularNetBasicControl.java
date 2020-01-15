package geo.gdal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;

import usualTool.AtCommonMath;

public class IrregularNetBasicControl {
	private int dataDecimal = 4;

	// key : Point index , from small to large
	private Map<String, FaceClass> faceMap = new LinkedHashMap<>();
	private List<String> faceList = new ArrayList<>();

	// key : point index , from small to large
	private Map<String, EdgeClass> edgeMap = new LinkedHashMap<>();

	// key : coordinateX + "_" + coodinateY
	private Map<String, NodeClass> nodeMap = new LinkedHashMap<>();
	private List<String> nodeList = new ArrayList<>();

	public IrregularNetBasicControl(List<Geometry> geoList) {
		this.processing(geoList);
	}

	public IrregularNetBasicControl(SpatialReader spatialFile) {
		this.processing(spatialFile.getGeometryList());
	}

	public IrregularNetBasicControl(List<Geometry> geoList, int dataDecimal) {
		this.dataDecimal = dataDecimal;
		this.processing(geoList);
	}

	public IrregularNetBasicControl(SpatialReader spatialFile, int dataDecimal) {
		this.dataDecimal = dataDecimal;
		this.processing(spatialFile.getGeometryList());
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

			for (int index = 0; index < geo.GetGeometryCount(); index++) {
				temptGeoList.add(geo.GetGeometryRef(index));
			}
		});
		geoList.clear();
		geoList = temptGeoList;
		return geoList;
	}

	// <=====================================================>
	// Processing
	// <=====================================================>
	private void sortedNodes(List<Geometry> geoList) {
		// oder all points
		gdal.AllRegister();
		Set<String> temptNodeSet = new LinkedHashSet<>();
		for (Geometry geo : geoList) {
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
			this.nodeMap.get(key).setKey(key);
			this.nodeList.add(key);
		}
	}

	private void processing(List<Geometry> geoList) {

		// check geoLists
		geoList = preCheck(geoList);

		// map every point and also create the list of it
		sortedNodes(geoList);

		// run all polygon
		for (Geometry geo : geoList) {

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
					faceKeyList.add(String.valueOf(this.nodeMap.get(currentPointKey).getIndex()));
				} else {
					currentPointKey = AtCommonMath.getDecimal_String(geo.GetX(index), this.dataDecimal) + "_"
							+ AtCommonMath.getDecimal_String(geo.GetY(index), this.dataDecimal);
					faceKeyList.add(String.valueOf(this.nodeMap.get(currentPointKey).getIndex()));
				}
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
			Collections.sort(faceKeyList);
			String faceKey = String.join("_", faceKeyList);
			Geometry centroid = geo.Centroid();
			temptFace.setFaceKey(faceKey);
			temptFace.setCenterX(centroid.GetX());
			temptFace.setCenterY(centroid.GetY());
			temptFace.setGeometry(geo);
			temptFace.setArea(geo.Area());
			faceKeyList.forEach(index -> {
				temptFace.addNode(this.nodeMap.get(this.nodeList.get(Integer.parseInt(index))));
			});
			temptFace.setIndex(this.faceList.size());
			this.faceMap.put(faceKey, temptFace);
			this.faceList.add(faceKey);
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
				points.add(new Double[] { node.getX(), node.getY() });
			});
			geoList.add(GdalGlobal.CreateLine(points));
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
		private Set<NodeClass> linkedNode = new HashSet<>();
		private Set<FaceClass> linkedFace = new HashSet<>();
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

		public double getCenterX() {
			return AtCommonMath.getDecimal_Double(
					(this.getLinkedNode().get(0).getX() + this.getLinkedNode().get(1).getX()) / 2, dataDecimal);
		}

		public double getCenterY() {
			return AtCommonMath.getDecimal_Double(
					(this.getLinkedNode().get(0).getY() + this.getLinkedNode().get(1).getY()) / 2, dataDecimal);
		}

		public String getKey() {
			return this.key;
		}
		
		protected void clear() {

			// remove linked
			this.linkedFace.forEach(face -> {
				try {
					face.getLinkedEdge().remove(this);
				} catch (Exception e) {
				}
			});

			this.linkedNode.forEach(node -> {
				try {
					node.getLinkedEdge().remove(this);
				} catch (Exception e) {
				}
			});

			// remove catch
			this.linkedFace.clear();
			this.linkedNode.clear();

			// remove global
			edgeMap.remove(this.key);
		}
	}

	public class FaceClass {
		public double nullValue = -999.0;
		private double centerX = nullValue;
		private double centerY = nullValue;
		private double value = nullValue;
		private double area = nullValue;
		private Geometry geo = null;
		private String faceKey = "";
		private Set<EdgeClass> linkedEdge = new HashSet<>();
		private Set<NodeClass> linkedNode = new HashSet<>();
		private Set<FaceClass> linkedFace = new HashSet<>();
		private int index = -1;

		public void setFaceKey(String key) {
			this.faceKey = key;
		}

		public void setGeometry(Geometry geo) {
			try {
				this.geo = GdalGlobal.lineStringToPolygon(geo);
			} catch (Exception e) {
				this.geo = geo;
			}
		}

		public void setArea(double area) {
			this.area = area;
		}

		public void addNode(NodeClass node) {
			this.linkedNode.add(node);
		}

		public void addEdge(EdgeClass edge) {
			this.linkedEdge.add(edge);

			if (edge.getLinkedFace().size() > 0) {
				edge.getLinkedFace().forEach(face -> {
					if (face != this) {
						this.linkedFace.add(face);
					}
				});
			}
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
			return new ArrayList<>(this.linkedFace);
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

		public String getFaceKey() {
			return this.faceKey;
		}

		public int getIndex() {
			return this.index;
		}

		public void remove() {
			for (EdgeClass temptEdge : this.linkedEdge) {
				if (temptEdge.getLinkedFace().size() == 1) {
					temptEdge.clear();
				} else {
					temptEdge.getLinkedFace().remove(this);
				}
			}

			for (NodeClass temptNode : this.linkedNode) {
				if (temptNode.getLinkedFace().size() == 1) {
					temptNode.clear();
				} else {
					temptNode.getLinkedFace().remove(this);
				}
			}

			this.clear();
		}

		protected void clear() {

			// remove linked
			this.linkedEdge.forEach(edge -> edge.linkedFace.remove(this));
			this.linkedNode.forEach(node -> node.linkedFace.remove(this));
			this.linkedFace.forEach(face -> face.linkedFace.remove(this));

			// remove catch
			this.linkedEdge.clear();
			this.linkedFace.clear();
			this.linkedNode.clear();

			// resorted list
			faceMap.remove(this.faceKey);
			faceList.remove(this.index);

			for (int tempt = index; tempt < faceList.size(); tempt++) {
				faceMap.get(faceList.get(tempt)).setIndex(tempt);
			}

		}

		public void mergeOtherLinkedFace(FaceClass face) throws Exception {
			if (!this.linkedFace.contains(face)) {
				throw new Exception("not avilable face");
			} else {
				FaceClass old1Face = face;
				FaceClass old2Face = this;

				FaceClass newFace = new FaceClass();
				List<String> faceKeyList = new ArrayList<>();

				Geometry newGeo = this.geo.Union(face.getGeo());
				Geometry centroid = newGeo.Centroid();

				newFace.setGeometry(newGeo);
				System.out.println(123);
				newFace.setArea(newGeo.Area());
				newFace.setCenterX(centroid.GetX(0));
				newFace.setCenterY(centroid.GetY(0));

				// seValue
				List<Double> valueList = new ArrayList<>();
				if (old1Face.getValue() != nullValue)
					valueList.add(old1Face.getValue());
				if (old2Face.getValue() != nullValue)
					valueList.add(old2Face.getValue());
				try {
					newFace.setValue(new AtCommonMath(valueList).getMean());
				} catch (Exception e) {
					newFace.setValue(nullValue);
				}

				/*------------------------------node-----------------------------------*/
				List<NodeClass> newNodeOrder = new ArrayList<>();
				for (int index = 0; index < newGeo.GetPointCount(); index++) {
					double temptX = AtCommonMath.getDecimal_Double(newGeo.GetX(index), dataDecimal);
					double temptY = AtCommonMath.getDecimal_Double(newGeo.GetY(index), dataDecimal);
					String nodeKey = temptX + "_" + temptY;

					// add all nodes to new new Face
					newNodeOrder.add(nodeMap.get(nodeKey));

					// add this face to nodes
					nodeMap.get(nodeKey).addFace(newFace);

					// add node to this face
					newFace.addNode(nodeMap.get(nodeKey));

					// add node index to faceKeyList
					faceKeyList.add(String.valueOf(nodeMap.get(nodeKey).getIndex()));
				}

				Collections.sort(faceKeyList);
				newFace.setFaceKey(String.join("_", faceKeyList));

				/*------------------------------Edge-----------------------------------*/
				EdgeClass linkedEdge = null;
				for (EdgeClass edge : face.getLinkedEdge()) {
					if (edge.getLinkedFace().contains(this)) {
						linkedEdge = edge;
						break;
					}
				}
				for (EdgeClass edge : old1Face.getLinkedEdge()) {
					if (edge != linkedEdge) {
						edge.addFace(newFace);
						newFace.addEdge(edge);
					}
				}
				for (EdgeClass edge : old2Face.getLinkedEdge()) {
					if (edge != linkedEdge) {
						edge.addFace(newFace);
						newFace.addEdge(edge);
					}
				}

				/*------------------------------remove-----------------------------------*/
				old1Face.clear();
				old2Face.clear();
				linkedEdge.clear();
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

		public List<EdgeClass> getLinkedEdge() {
			return new ArrayList<>(this.linkedEdge);
		}

		public List<FaceClass> getLinkedFace() {
			return new ArrayList<>(this.linkedFace);
		}

		protected void clear() {

			// remove linked
			this.linkedEdge.forEach(edge -> {
				try {
					edge.getLinkedNode().remove(this);
				} catch (Exception e) {
				}
			});

			this.linkedFace.forEach(face -> {
				try {
					face.getLinkedNode().remove(this);
				} catch (Exception e) {
				}
			});

			// remove catch
			this.linkedEdge.clear();
			this.linkedFace.clear();
			nodeMap.remove(nodeList.get(index));
			nodeList.remove(index);

			// resort nodeList
			for (int tempt = index; tempt < nodeList.size(); tempt++) {
				nodeMap.get(nodeList.get(tempt)).setIndex(tempt);
			}
		}
	}

}
