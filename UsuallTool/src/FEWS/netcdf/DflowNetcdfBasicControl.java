package FEWS.netcdf;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import netCDF.NetcdfBasicControl;

public class DflowNetcdfBasicControl {
	private NetcdfBasicControl netFile;
	private Map<String, String> attribute = new TreeMap<>();

	// point
	private List<Double[]> points = new ArrayList<>();

	// polygon
	private List<Path2D> polygons = new ArrayList<>();
	private List<Integer[]> polygonsPointsLink = new ArrayList<>(); // counterClockWise
	private List<Double[]> polygonsCentroid = new ArrayList<>();

	// edges
	private List<Double[]> edgesPoints = new ArrayList<>(); // new Double[]{ point1_X , point1_Y , point2_X , point2_Y }
	private List<Integer[]> edgesPointsLink = new ArrayList<>(); // new Integer[]{point1_Index , point2_index};
	private List<Double[]> edgesMidPoints = new ArrayList<>(); // new Double[]{midPointX , midPointY}

	// model result
	private List<Double> timeList = new ArrayList<>();
	private double timeStep = 0;
	private List<List<Double>> waterDepthValues = new ArrayList<>();
	private List<List<Double>> waterLevelValues = new ArrayList<>();

	// variable name
	private String VARIABLE_Depth = "mesh2d_waterdepth";
	private String VARIABLE_WaterLevel = "mesh2d_s1";
	private String VARIABLE_Time = "time";
	private String VARIABLE_TimeStep = "timestep";

	public DflowNetcdfBasicControl(String netcdfAdd) throws IOException {
		this.netFile = new NetcdfBasicControl(netcdfAdd);
		this.process();
	}

	public List<Double[]> getPoints() {
		return this.points;
	}

	public List<Path2D> getPolygons() {
		return this.getPolygons();
	}

	public List<Integer[]> getPolygonPointsIndex() {
		return this.polygonsPointsLink;
	}

	public List<Double[]> getPolygonCentroid() {
		return this.polygonsCentroid;
	}

	public List<Double[]> getEdgesPoints() {
		return this.edgesPoints;
	}

	public List<Integer[]> getEdgesPointsIndex() {
		return this.edgesPointsLink;
	}

	public List<Double[]> getEdgesMidPoints() {
		return this.edgesMidPoints;
	}

	public Map<String, String> getNetcdfProperty() {
		return this.attribute;
	}

	public String getCoordinateSystem() {
		return this.attribute.get("coordinateSystem");
	}

	public List<Integer> getFaceIndex(Double[] cooridnates) {
		return getFaceIndex(new Double[][] { { cooridnates[0], cooridnates[1] } });
	}

	public List<Integer> getFaceIndex(Double[][] cooridnates) {
		return getFaceIndex(Arrays.asList(cooridnates).parallelStream().collect(Collectors.toList()));
	}

	public List<Integer> getFaceIndex(List<Double[]> cooridnateList) {

		// initail map , tempt store the point coordinates
		Map<Integer, Double[]> pointsCoordinate = new LinkedHashMap<>();
		for (int index = 0; index < cooridnateList.size(); index++) {
			pointsCoordinate.put(index, cooridnateList.get(index));
		}

		// initial map, get the index which polygon contain
		// if there is match face index, set the number to -1
		Map<Integer, Integer> faceIndex = new LinkedHashMap<>();
		for (int index = 0; index < cooridnateList.size(); index++) {
			faceIndex.put(index, -1);
		}

		// detect the index of polygon
		for (int polygonIndex = 0; polygonIndex < this.polygons.size(); polygonIndex++) {

			// detect the point
			for (Integer keyIndex : pointsCoordinate.keySet()) {
				if (this.polygons.get(keyIndex).contains(cooridnateList.get(keyIndex)[0],
						cooridnateList.get(keyIndex)[1])) {
					faceIndex.put(keyIndex, polygonIndex);
					pointsCoordinate.remove(keyIndex);
				}
			}
		}

		return new ArrayList<>(faceIndex.keySet());
	}

	public List<Double> getWaterDepthFromIndex(int index) {
		List<Double> outList = new ArrayList<>();
		this.waterDepthValues.forEach(timeValues -> {
			outList.add(timeValues.get(index));
		});
		return outList;
	}

	public List<List<Double>> getWaterDepth() {
		return this.waterDepthValues;
	}

	public List<Double> getWaterLevelByIndex(int index) {
		List<Double> outList = new ArrayList<>();
		this.waterLevelValues.forEach(timeValues -> {
			outList.add(timeValues.get(index));
		});
		return outList;
	}

	public List<List<Double>> getWaterLevel() {
		return this.waterLevelValues;
	}

	public List<Double> getTimes() {
		return this.timeList;
	}

	public double getTimeStep() {
		return this.timeStep;
	}

	/*
	 * private funtion
	 */
	private void process() throws IOException {
		// get all points
		this.setAllPoints();

		// get all polygons
		this.setAllPolygons();

		// get all egdes
		this.setAllEdges();

		// get all attribute
		this.setAttribute();

		// get time
		try {
			this.timeList = this.setTime();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get timeStep
		try {
			this.timeStep = this.setTimeStep();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// get waterLevel
		try {
			this.waterLevelValues = this.setWaterLevel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get waterDepth
		try {
			this.waterDepthValues = this.setWaterDepth();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get Points++++++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private void setAllPoints() throws IOException {
		List<Object> xList = this.netFile.getVariableValues("mesh2d_node_x");
		List<Object> yList = this.netFile.getVariableValues("mesh2d_node_y");
		for (int index = 0; index < xList.size(); index++) {
			this.points.add(new Double[] { (double) xList.get(index), (double) yList.get(index) });
		}
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get Polygons++++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private void setAllPolygons() throws IOException {
		// get points coordinate of polygon
		List<Object> polygonPointsX = this.netFile.getVariableValues("mesh2d_face_x_bnd");
		List<Object> polygonPointsY = this.netFile.getVariableValues("mesh2d_face_y_bnd");

		// get points index of polygon
		List<Object> polygonPointsIndex = this.netFile.getVariableValues("mesh2d_face_nodes");

		// get centroid coordinate of polygon
		List<Object> polygonCentroidX = this.netFile.getVariableValues("mesh2d_face_x");
		List<Object> polygonCentroidY = this.netFile.getVariableValues("mesh2d_face_y");

		// get polygons
		for (int polygon = 0; polygon < polygonPointsX.size(); polygon++) {
			List<Double> pointsX = (List<Double>) polygonPointsX.get(polygon);
			List<Double> pointsY = (List<Double>) polygonPointsY.get(polygon);
			List<Integer> pointsIndex = (List<Integer>) polygonPointsIndex.get(polygon);

			// get points
			List<Double[]> temptPoints = new ArrayList<>();
			List<Integer> temptPointsIndex = new ArrayList<>();
			for (int index = 0; index < pointsX.size(); index++) {
				if (pointsIndex.get(index) == -999) {
					break;
				} else {
					temptPoints.add(new Double[] { pointsX.get(index), pointsY.get(index) });
					temptPointsIndex.add(pointsIndex.get(index));
				}
			}

			// get path2D
			this.polygons.add(this.pointsToPaht2d(temptPoints));
			this.polygonsPointsLink.add(temptPointsIndex.parallelStream().toArray(Integer[]::new));
			this.polygonsCentroid.add(
					new Double[] { (Double) polygonCentroidX.get(polygon), (Double) polygonCentroidY.get(polygon) });
		}
	}

	private Path2D pointsToPaht2d(List<Double[]> pointList) {
		Path2D temptPath = new Path2D.Double();
		temptPath.moveTo(pointList.get(0)[0], pointList.get(0)[1]);

		for (int index = 1; index < pointList.size(); index++) {
			temptPath.lineTo(pointList.get(index)[0], pointList.get(index)[1]);
		}
		return temptPath;
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get Edges++++++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private void setAllEdges() throws IOException {
		// get coordinate of edge midPoint
		List<Object> edgeMidX = this.netFile.getVariableValues("mesh2d_edge_x");
		List<Object> edgeMidY = this.netFile.getVariableValues("mesh2d_edge_y");

		// get coordinate of edge points
		List<Object> edgeLinkX = this.netFile.getVariableValues("mesh2d_edge_x_bnd");
		List<Object> edgeLinkY = this.netFile.getVariableValues("mesh2d_edge_y_bnd");

		// get coordinate of edge points Index
		List<Object> edgeLinkIndex = this.netFile.getVariableValues("mesh2d_edge_nodes");

		for (int edge = 0; edge < edgeMidX.size(); edge++) {

			// get midPoint
			this.edgesMidPoints.add(new Double[] { (Double) edgeMidX.get(edge), (Double) edgeMidY.get(edge) });

			// get linkPoint
			List<Double> linkX = (List<Double>) edgeLinkX.get(edge);
			List<Double> linkY = (List<Double>) edgeLinkY.get(edge);
			this.edgesPoints.add(new Double[] { linkX.get(0), linkY.get(0), linkX.get(1), linkY.get(1) });

			// get linkPointIndex
			List<Integer> linkIndex = (List<Integer>) edgeLinkIndex.get(edge);
			this.edgesPointsLink.add(new Integer[] { linkIndex.get(0), linkIndex.get(1) });
		}

	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get Attribute++++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private void setAttribute() {
		// get coordinate system
		this.netFile.getVaiable("projected_coordinate_system").getAttributes().forEach(attribute -> {
			if (attribute.getFullName().contains("code")) {
				this.attribute.put("coordinateSystem", attribute.getStringValue());
			}
		});

		// get mesh property
		this.netFile.getNetFile().getGlobalAttributes().forEach(attribute -> {
			switch (attribute.getFullName()) {
			case "nmesh2d_edge":
				this.attribute.put("edgeNum", attribute.getStringValue());

			case "nmesh2d_node":
				this.attribute.put("nodeNum", attribute.getStringValue());

			case "nmesh2d_face":
				this.attribute.put("polygonNum", attribute.getStringValue());

			case "max_nmesh2d_face_nodes":
				this.attribute.put("polygonMaxPointNum", attribute.getStringValue());
			}

		});
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get Time ++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private List<Double> setTime() throws IOException {
		List<Object> timeValues = this.netFile.getVariableValues(this.VARIABLE_Time);
		List<Double> outList = new ArrayList<>();
		timeValues.forEach(time -> {
			outList.add((Double) time);
		});
		return outList;
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get TimeStep ++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private double setTimeStep() throws IOException {
		List<Object> timeStep = this.netFile.getVariableValues(this.VARIABLE_TimeStep);
		return (Double) timeStep.get(0);
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get WaterLevel ++++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private List<List<Double>> setWaterLevel() throws IOException {
		List<Object> values = this.netFile.getVariableValues(this.VARIABLE_WaterLevel);
		List<List<Double>> outList = new ArrayList<>();

		values.forEach(timeValues -> {
			outList.add((ArrayList<Double>) (timeValues));
		});
		return outList;
	}

	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Get WaterDepth +++++++++++++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++++++++++++++++>
	private List<List<Double>> setWaterDepth() throws IOException {
		List<Object> values = this.netFile.getVariableValues(this.VARIABLE_Depth);
		List<List<Double>> outList = new ArrayList<>();

		values.forEach(timeValues -> {
			outList.add((ArrayList<Double>) (timeValues));
		});
		return outList;
	}
}
