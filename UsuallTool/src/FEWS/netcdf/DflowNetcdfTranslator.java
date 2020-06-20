package FEWS.netcdf;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import netCDF.NetcdfWriter;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import usualTool.AtCommonMath;
import usualTool.TimeTranslate;

public class DflowNetcdfTranslator {
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Java Object++++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// original data set
	// all polygons
	private List<Geometry> geoList = new ArrayList<>();
	// all points in polygons which setting in dataDecimal
	private List<List<String>> geoPoints = new ArrayList<>();
	private NetcdfWriter writer;
	private int dataDecimale = 7;

	/*********** points *******/
	// value : String of coordinate (CoordinateX + "_" + CoordinateY)
	private List<String> points = new ArrayList<>();

	// key : String of coordinate (CoordinateX + "_" + CoordinateY)
	// value : index of points, start from 0;
	private Map<String, Integer> pointsIndexMap;

	// key : String of coordinate (CoordinateX + "_" + CoordinateY)
	// value : value of Z;
	private Map<String, Double> points_Z_Value = new TreeMap<>();

	/*********** polygon *******/
	// value : list of polygon which build by index of points
	private List<List<Integer>> polygonPointsIndex = new ArrayList<>();

	// value : list of polygon centroid coordinate
	private List<Double[]> polygonCentroid = new ArrayList<>();

	// value : list of polygon bedLevel
	private List<Double> polygonLevel = new ArrayList<>();

	private int maxPolygonNodeNum = 0;

	/*********** edge *******/
	// key : pointsIndex (point1Indext + "_" + point2Index)
	// value : faceIndex , size only for 0,1 , start from 0
	private Map<String, List<Integer>> edgeFaceIndex = new LinkedHashMap<>();

	/*********** TimeSeries *******/
	private Boolean TimeSeriesOutput = false;
	private double timeStep = 0;
	private List<Double> timeSeries = new ArrayList<>();

	/************ Water level output ********/
	private Boolean waterLevelOutput = false;
	private List<Double[]> polygonTimeSeries = new ArrayList<>();;

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Netcdf Object ++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>

	public DflowNetcdfTranslator(AsciiBasicControl ascii) {

		double cellSize = ascii.getCellSize();

		// get each grid
		for (int row = 0; row < Integer.parseInt(ascii.getProperty().get("row")); row++) {
			for (int column = 0; column < Integer.parseInt(ascii.getProperty().get("column")); column++) {

				// check for null grid
				String temptValue = ascii.getValue(column, row);
				if (!temptValue.equals(ascii.getNullValue())) {

					// draw path2D
					double[] coordinate = ascii.getCoordinate(column, row);
					Path2D temptPath = new Path2D.Double();
					temptPath.moveTo(
							BigDecimal.valueOf(coordinate[0] - 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue(),
							BigDecimal.valueOf(coordinate[1] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue());
					temptPath.lineTo(
							BigDecimal.valueOf(coordinate[0] - 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue(),
							BigDecimal.valueOf(coordinate[1] - 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue());
					temptPath.lineTo(
							BigDecimal.valueOf(coordinate[0] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue(),
							BigDecimal.valueOf(coordinate[1] - 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue());
					temptPath.lineTo(
							BigDecimal.valueOf(coordinate[0] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue(),
							BigDecimal.valueOf(coordinate[1] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).doubleValue());
					geoList.add(GdalGlobal.Path2DToGeometry(temptPath));

					// make geoPoints
					List<String> temptGeoPoints = new ArrayList<>();
					temptGeoPoints.add(BigDecimal.valueOf(coordinate[0] - 0.5 * cellSize)
							.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString() + "_"
							+ BigDecimal.valueOf(coordinate[1] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString());
					temptGeoPoints.add(BigDecimal.valueOf(coordinate[0] - 0.5 * cellSize)
							.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString() + "_"
							+ BigDecimal.valueOf(coordinate[1] - 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString());
					temptGeoPoints.add(BigDecimal.valueOf(coordinate[0] + 0.5 * cellSize)
							.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString() + "_"
							+ BigDecimal.valueOf(coordinate[1] - 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString());
					temptGeoPoints.add(BigDecimal.valueOf(coordinate[0] + 0.5 * cellSize)
							.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString() + "_"
							+ BigDecimal.valueOf(coordinate[1] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString());
					temptGeoPoints.add(BigDecimal.valueOf(coordinate[0] - 0.5 * cellSize)
							.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString() + "_"
							+ BigDecimal.valueOf(coordinate[1] + 0.5 * cellSize)
									.setScale(dataDecimale, BigDecimal.ROUND_HALF_UP).toString());
					geoPoints.add(temptGeoPoints);

				}
			}
		}
		this.process();
	}

	public DflowNetcdfTranslator(SpatialReader shpFile, Boolean checkClockWise) {
		this.setGeoPointsDecimale(shpFile.getGeometryList(), checkClockWise);
		this.process();
	}

	public DflowNetcdfTranslator(SpatialReader shpFile) {
		this.setGeoPointsDecimale(shpFile.getGeometryList(), false);
		this.process();
	}

	public DflowNetcdfTranslator(SpatialReader shpFile, int decimale, Boolean checkClockWise) {
		this.dataDecimale = decimale;
		this.setGeoPointsDecimale(shpFile.getGeometryList(), checkClockWise);
		this.process();
	}

	public DflowNetcdfTranslator(SpatialReader shpFile, int decimale) {
		this.dataDecimale = decimale;
		this.setGeoPointsDecimale(shpFile.getGeometryList(), false);
		this.process();
	}

	public DflowNetcdfTranslator(List<Geometry> geoList, Boolean checkClockWise) {
		this.setGeoPointsDecimale(geoList, checkClockWise);
		this.process();
	}

	public DflowNetcdfTranslator(List<Geometry> geoList) {
		this.setGeoPointsDecimale(geoList, false);
		this.process();
	}

	public DflowNetcdfTranslator(List<Geometry> geoList, int decimale, Boolean checkClockWise) {
		this.dataDecimale = decimale;
		this.setGeoPointsDecimale(geoList, checkClockWise);
		this.process();
	}

	public DflowNetcdfTranslator(List<Geometry> geoList, int decimale) {
		this.dataDecimale = decimale;
		this.setGeoPointsDecimale(geoList, false);
		this.process();
	}

	private void setGeoPointsDecimale(List<Geometry> geoList, Boolean checkClockWise) {
		geoList.forEach(geo -> {
			try {
				if (geo.GetGeometryType() == 3) {
					// get geoPolygon
					if (checkClockWise) {
						this.geoList.add(checkPolygonClockWise(geo));
					} else {
						this.geoList.add(geo);
					}

					// get geoPoints
					Geometry temptPolygon = geo.Boundary();
					geoPoints
							.add(Arrays.asList(temptPolygon.GetPoints()).parallelStream()
									.map(point -> new BigDecimal(point[0])
											.setScale(this.dataDecimale, BigDecimal.ROUND_HALF_UP).toString()
											+ "_"
											+ new BigDecimal(point[1])
													.setScale(this.dataDecimale, BigDecimal.ROUND_HALF_UP).toString())
									.collect(Collectors.toList()));
				}
			} catch (Exception e) {
			}
		});
	}

	private void process() {
		gdal.AllRegister();

		System.out.println("order all points");
		// get all points
		Set<String> temptPointSet = new LinkedHashSet<>();
		this.geoPoints.forEach(polygonPoints -> {
			polygonPoints.forEach(polygonPoint -> {
				temptPointSet.add(polygonPoint);
			});
		});

		// order the points
		pointsIndexMap = new LinkedHashMap<>();
		temptPointSet.iterator().forEachRemaining(point -> {
			pointsIndexMap.put(point, pointsIndexMap.size());
			points_Z_Value.put(point, -999.0);
		});
		this.points = new ArrayList<>(pointsIndexMap.keySet());

		// order the other variable
		for (int polygon = 0; polygon < this.geoList.size(); polygon++) {
			Geometry temptGeo = this.geoList.get(polygon);

			// get centroid
			Geometry centroid = temptGeo.Centroid();
			this.polygonCentroid.add(new Double[] { centroid.GetX(), centroid.GetY() });

			// set bedLevel with -999
			this.polygonLevel.add(-999.0);

			/*
			 * get points
			 */
			// polygon link point
			List<Integer> temptPolygonPointsIndex = new ArrayList<>();

			// link start point // direct to point index
			String startPointKey = this.geoPoints.get(polygon).get(0);
			int startPointIndex = pointsIndexMap.get(startPointKey);
			temptPolygonPointsIndex.add(startPointIndex);

			// link end point // direct to point index
			for (int point = 1; point < this.geoPoints.get(polygon).size(); point++) {
				String endPointKey = this.geoPoints.get(polygon).get(point);
				int endPointIndex = pointsIndexMap.get(endPointKey);
				temptPolygonPointsIndex.add(endPointIndex);

				// set edge link nodes
				List<Integer> edgePointIndexList = new ArrayList<>(Arrays.asList(startPointIndex, endPointIndex));
				Collections.sort(edgePointIndexList);

				// set edge linked face
				String edgeFaceKey = edgePointIndexList.get(0) + "_" + edgePointIndexList.get(1);
				try {
					this.edgeFaceIndex.get(edgeFaceKey).add(polygon);
				} catch (Exception e) {
					this.edgeFaceIndex.put(edgeFaceKey, new ArrayList<Integer>());
					this.edgeFaceIndex.get(edgeFaceKey).add(polygon);
				}

				// make end point to start point
				startPointIndex = endPointIndex;
				startPointKey = endPointKey;
			}

			// set polygon nodes index
			// remove the last node from polygon // geometry will repeat begining point
			temptPolygonPointsIndex.remove(temptPolygonPointsIndex.size() - 1);
			this.polygonPointsIndex.add(temptPolygonPointsIndex);
			// check for the max nodes number in polygon
			if (temptPolygonPointsIndex.size() > this.maxPolygonNodeNum) {
				this.maxPolygonNodeNum = temptPolygonPointsIndex.size();
			}
		}
	}

	// convert all polygon to unclockwise
	private Geometry checkPolygonClockWise(Geometry geo) {
		Geometry temptBound = geo.Boundary();
		Geometry centroid = temptBound.Centroid();
		double centroidX = centroid.GetX();
		double centroidY = centroid.GetY();

		List<double[]> pointList = Arrays.asList(temptBound.GetPoints());

		double[] vector1 = new double[] { pointList.get(0)[0] - centroidX, pointList.get(0)[1] - centroidY };
		double degree1 = AtCommonMath.getAzimuth(vector1);

		double[] vector2 = new double[] { pointList.get(1)[0] - centroidX, pointList.get(1)[1] - centroidY };
		double degree2 = AtCommonMath.getAzimuth(vector2);

		// clockwise
		if (degree2 > degree1) {
			Collections.reverse(pointList);
			Geometry temptGeo = GdalGlobal.Path2DToGeometry(GdalGlobal.CreatePath2D(
					pointList.parallelStream().map(d -> new Double[] { d[0], d[1] }).collect(Collectors.toList())));
			return temptGeo;
		} else {
			return geo;
		}
	}

	public DflowNetcdfTranslator checkOverlappingShp() {
		Map<String, Integer> polygonPoints = new TreeMap<>();

		// check for overlapping by using the same points
		for (int index = 0; index < polygonPointsIndex.size(); index++) {
			List<Integer> temptList = new ArrayList<>(polygonPointsIndex.get(index));
			Collections.sort(temptList);
			String key = String.join(",",
					temptList.parallelStream().map(e -> String.valueOf(e)).collect(Collectors.toList()));

			polygonPoints.put(key, index);
		}

		// if there is overlapping, return a new class
		if (polygonPoints.size() != this.polygonPointsIndex.size()) {
			List<Geometry> outList = new ArrayList<>();
			polygonPoints.keySet().forEach(key -> {
				outList.add(geoList.get(polygonPoints.get(key)));
			});
			return new DflowNetcdfTranslator(outList);
		} else {
			return this;
		}
	}

	public DflowNetcdfTranslator checkQualTreeOverLappingLine() {
		List<String> temptKeyList = new ArrayList<>(this.edgeFaceIndex.keySet());
		for (String key : temptKeyList) {

			// if there is only on face is connected
			if (edgeFaceIndex.get(key).size() == 1) {

				// get face index
				int faceIndex = edgeFaceIndex.get(key).get(0);

				// get the center of edge points
				String[] pointIndexs = key.split("_");
				String[] point1 = this.points.get(Integer.parseInt(pointIndexs[0])).split("_");
				String[] point2 = this.points.get(Integer.parseInt(pointIndexs[1])).split("_");
				String[] edgeCenter = new String[] {
						new BigDecimal((Double.parseDouble(point1[0]) + Double.parseDouble(point2[0])) / 2)
								.setScale(this.dataDecimale, BigDecimal.ROUND_HALF_UP).toString(),
						new BigDecimal((Double.parseDouble(point1[1]) + Double.parseDouble(point2[1])) / 2)
								.setScale(this.dataDecimale, BigDecimal.ROUND_HALF_UP).toString() };

				// if points is exist, remove the logger one
				if (this.pointsIndexMap.get(edgeCenter[0] + "_" + edgeCenter[1]) != null) {
					edgeFaceIndex.remove(key);

					// then add another two smaller edge
					Integer centerPointIndex = this.pointsIndexMap.get(edgeCenter[0] + "_" + edgeCenter[1]);

					// 1
					List<Integer> edgePointIndexList1 = new ArrayList<>(
							Arrays.asList(Integer.parseInt(pointIndexs[0]), centerPointIndex));
					Collections.sort(edgePointIndexList1);
					this.edgeFaceIndex.get(edgePointIndexList1.get(0) + "_" + edgePointIndexList1.get(1))
							.add(faceIndex);

					// 2
					List<Integer> edgePointIndexList2 = new ArrayList<>(
							Arrays.asList(Integer.parseInt(pointIndexs[1]), centerPointIndex));
					Collections.sort(edgePointIndexList2);
					this.edgeFaceIndex.get(edgePointIndexList2.get(0) + "_" + edgePointIndexList2.get(1))
							.add(faceIndex);

					// add a new point to the face
					List<Integer> facePoints = this.polygonPointsIndex.get(faceIndex);
					for (int index = 0; index < facePoints.size(); index++) {
						if (facePoints.get(index) == Integer.parseInt(pointIndexs[1])
								|| facePoints.get(index) == Integer.parseInt(pointIndexs[0])) {
							facePoints.add(index + 1, centerPointIndex);
							break;
						}
					}
					if (facePoints.size() > this.maxPolygonNodeNum) {
						this.maxPolygonNodeNum = facePoints.size();
					}
				}
			}
		}
		return this;
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Netcdf Writer ++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	public void saveAs(String saveAdd) throws IOException, InvalidRangeException {
		this.writer = new NetcdfWriter(saveAdd);

		// set dimension
		this.setDimension();

		// set globalAttribute
		this.setGlobalAttribute();

		// set mesh2d
		this.setMesh2D();

		// set projection
		this.setProjection();

		// set nodeXYZ
		this.set_mesh2d_node_xyz();

		// set mesh2d_edge_xy
		this.set_mesh2d_edge_xy();

		// set mesh2d edge xy_bnd
		this.set_mesh2d_edge_xy_bnd();

		// set edge nodes
		this.set_mesh2d_edge_nodes();

		// set mesh2d_face_nodes
		this.set_mesh2d_face_nodes();

		// set mesh2d edge faces
		this.set_mesh2d_edge_faces();

		// set mesh2d_face_xy
		this.set_mesh2d_face_xy();

		// set mesh2d_face_xy_bnd
		this.set_mesh2d_face_xy_bnd();

		// set mesh2d_cell_Area
		this.set_mesh2d_Cell_Area();

		// set mesh2d cell bedLevel
		this.set_mesh2d_flowelem_bl();

		// series time output
		if (this.TimeSeriesOutput) {
			this.set_Time();
			this.set_TimeStep();
		}

		// output timeSeries value
		if (this.waterLevelOutput) {
			this.set_mesh2d_waterDepth();
			this.set_mesh2d_waterLevel();
		}

		// set value
		writer.create();
		this.set_Projection_Value();
		this.set_mesh2D_Value();
		this.set_mesh2d_edge_nodes_Value();
		this.set_mesh2d_edge_xy_bnd_Value();
		this.set_mesh2d_edge_xy_Value();
		this.set_mesh2d_node_xyz_Value();
		this.set_mesh2d_face_nodes_Value();
		this.set_mesh2d_edge_faces_Value();
		this.set_mesh2d_face_xy_Value();
		this.set_mesh2d_face_xy_bnd_Value();
		this.set_mesh2d_Cell_Area_Value();
		this.set_mesh2d_flowelem_bl_value();

		// series time output
		if (this.TimeSeriesOutput) {
			this.set_Time_Value();
			this.set_TimeStep_Value();
		}

		// output timeSeries value
		if (this.waterLevelOutput) {
			this.set_mesh2d_waterLevel_waterDepth_value();
		}

		writer.close();
	}

	private void setDimension() {
		writer.addDimension("Two", 2);
		writer.addDimension("nmesh2d_edge", this.edgeFaceIndex.keySet().size());
		writer.addDimension("nmesh2d_node", this.points.size());
		writer.addDimension("nmesh2d_face", this.polygonPointsIndex.size());
		writer.addDimension("max_nmesh2d_face_nodes", this.maxPolygonNodeNum);

		if (this.TimeSeriesOutput) {
			writer.addDimension("time", this.timeSeries.size());
		}
	}

	private void setGlobalAttribute() {
		writer.addGlobalAttribute("institution", "Deltares");
		writer.addGlobalAttribute("references", "http://www.deltares.n");
		writer.addGlobalAttribute("source", "RGFGRID 6.00.01.61844. Model: ---");
		writer.addGlobalAttribute("history",
				"Created on " + TimeTranslate.getDateString(System.currentTimeMillis(), "yyyy-MM-dd") + "T"
						+ TimeTranslate.getDateString(System.currentTimeMillis(), "HH:mm:ss") + "+0800, RGFGRID");
		writer.addGlobalAttribute("Conventions", "CF-1.6 UGRID-1.0/Deltares-0.8");

	}

	private void setMesh2D() throws IOException, InvalidRangeException {
		writer.addVariable("mesh2d", DataType.INT);

		writer.addVariableAttribute("mesh2d", "cf_role ", "mesh_topology");
		writer.addVariableAttribute("mesh2d", "long_name ", "Topology data of 2D network");
		writer.addVariableAttribute("mesh2d", "topology_dimension ", (int) 2);
		writer.addVariableAttribute("mesh2d", "node_coordinates ", "mesh2d_node_x mesh2d_node_y");
		writer.addVariableAttribute("mesh2d", "node_dimension ", "nmesh2d_node");
		writer.addVariableAttribute("mesh2d", "max_face_nodes_dimension ", "max_nmesh2d_face_nodes");
		writer.addVariableAttribute("mesh2d", "edge_node_connectivity ", "mesh2d_edge_nodes");
		writer.addVariableAttribute("mesh2d", "edge_dimension ", "nmesh2d_edge");
		writer.addVariableAttribute("mesh2d", "edge_coordinates ", "mesh2d_edge_x mesh2d_edge_y");
		writer.addVariableAttribute("mesh2d", "face_node_connectivity ", "mesh2d_face_nodes");
		writer.addVariableAttribute("mesh2d", "face_dimension ", "nmesh2d_face");
		writer.addVariableAttribute("mesh2d", "edge_face_connectivity ", "mesh2d_edge_faces");
		writer.addVariableAttribute("mesh2d", "face_coordinates ", "mesh2d_face_x mesh2d_face_y");
	}

	private void setProjection() {
		writer.addVariable("projected_coordinate_system", DataType.INT);
		writer.addVariableAttribute("projected_coordinate_system", "name ", "Unknown projected");
		writer.addVariableAttribute("projected_coordinate_system", "epsg ", (int) 0);
		writer.addVariableAttribute("projected_coordinate_system", "grid_mapping_name ", "Unknown projected");
		writer.addVariableAttribute("projected_coordinate_system", "longitude_of_prime_meridian ", 0.0);
		writer.addVariableAttribute("projected_coordinate_system", "semi_major_axis ", 6378137.0);
		writer.addVariableAttribute("projected_coordinate_system", "semi_minor_axis ", 6356752.314245);
		writer.addVariableAttribute("projected_coordinate_system", "inverse_flattening ", 298.257223563);
		writer.addVariableAttribute("projected_coordinate_system", "EPSG_code ", "EPSG:0");
		writer.addVariableAttribute("projected_coordinate_system", "value ", "value is equal to EPSG code");
	}

	private void set_mesh2d_node_xyz_Value() throws IOException, InvalidRangeException {
		// get cordinate from point list
		List<Double> xList = new ArrayList<>();
		List<Double> yList = new ArrayList<>();
		List<Double> zList = new ArrayList<>();
		this.points.forEach(key -> {
			String[] cordinate = key.split("_");
			xList.add(Double.parseDouble(cordinate[0]));
			yList.add(Double.parseDouble(cordinate[1]));
			zList.add(this.points_Z_Value.get(key));
		});

		writer.addValue("mesh2d_node_x",
				ArrayDouble.factory(xList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

		writer.addValue("mesh2d_node_y",
				ArrayDouble.factory(yList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

		writer.addValue("mesh2d_node_z",
				Array.factory(zList.parallelStream().mapToDouble(Double::doubleValue).toArray()));
	}

	private void set_mesh2d_node_xyz() throws IOException, InvalidRangeException {

		// x
		writer.addVariable("mesh2d_node_x", DataType.DOUBLE, "nmesh2d_node");
		writer.addVariableAttribute("mesh2d_node_x", "units", "m");
		writer.addVariableAttribute("mesh2d_node_x", "standard_name", "projection_x_coordinate");
		writer.addVariableAttribute("mesh2d_node_x", "long_name", "x-coordinate of mesh nodes");
		writer.addVariableAttribute("mesh2d_node_x", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_node_x", "location", "node");

		// y
		writer.addVariable("mesh2d_node_y", DataType.DOUBLE, "nmesh2d_node");
		writer.addVariableAttribute("mesh2d_node_y", "units", "m");
		writer.addVariableAttribute("mesh2d_node_y", "standard_name", "projection_y_coordinate");
		writer.addVariableAttribute("mesh2d_node_y", "long_name", "y-coordinate of mesh nodes");
		writer.addVariableAttribute("mesh2d_node_y", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_node_y", "location", "node");

		// z
		writer.addVariable("mesh2d_node_z", DataType.DOUBLE, "nmesh2d_node");
		writer.addVariableAttribute("mesh2d_node_z", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_node_z", "location", "node");
		writer.addVariableAttribute("mesh2d_node_z", "coordinates", "mesh2d_node_x mesh2d_node_y");
		writer.addVariableAttribute("mesh2d_node_z", "standard_name", "altitude");
		writer.addVariableAttribute("mesh2d_node_z", "long_name", "z-coordinate of mesh nodes");
		writer.addVariableAttribute("mesh2d_node_z", "units", "m");
		writer.addVariableAttribute("mesh2d_node_z", "grid_mapping", "projected_coordinate_system");
		writer.addVariableAttribute("mesh2d_node_z", "_FillValue", -999.0);

	}

	private void set_mesh2d_edge_xy_Value() throws IOException, InvalidRangeException {
		// get coordinate from edge mid points list
		List<Double> xList = new ArrayList<>();
		List<Double> yList = new ArrayList<>();
		this.edgeFaceIndex.keySet().forEach(edgePointsIndex -> {
			String pointsIndex[] = edgePointsIndex.split("_");
			String startPoint[] = this.points.get(Integer.parseInt(pointsIndex[0])).split("_");
			String endPoint[] = this.points.get(Integer.parseInt(pointsIndex[1])).split("_");

			xList.add((Double.parseDouble(startPoint[0]) + Double.parseDouble(endPoint[0])) / 2);
			yList.add((Double.parseDouble(startPoint[1]) + Double.parseDouble(endPoint[1])) / 2);
		});

		writer.addValue("mesh2d_edge_x",
				ArrayDouble.factory(xList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

		writer.addValue("mesh2d_edge_y",
				ArrayDouble.factory(yList.parallelStream().mapToDouble(Double::doubleValue).toArray()));

	}

	private void set_mesh2d_edge_xy() throws IOException, InvalidRangeException {

		// edge x
		writer.addVariable("mesh2d_edge_x", DataType.DOUBLE, "nmesh2d_edge");
		writer.addVariableAttribute("mesh2d_edge_x", "units", "m");
		writer.addVariableAttribute("mesh2d_edge_x", "standard_name", "projection_x_coordinate");
		writer.addVariableAttribute("mesh2d_edge_x", "long_name",
				"characteristic x-coordinate of the mesh edge (e.g. midpoint)");
		writer.addVariableAttribute("mesh2d_edge_x", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_edge_x", "location", "edge");
		writer.addVariableAttribute("mesh2d_edge_x", "bounds", "mesh2d_edge_x_bnd");

		// edge y
		writer.addVariable("mesh2d_edge_y", DataType.DOUBLE, "nmesh2d_edge");
		writer.addVariableAttribute("mesh2d_edge_y", "units", "m");
		writer.addVariableAttribute("mesh2d_edge_y", "standard_name", "projection_y_coordinate");
		writer.addVariableAttribute("mesh2d_edge_y", "long_name",
				"characteristic y-coordinate of the mesh edge (e.g. midpoint)");
		writer.addVariableAttribute("mesh2d_edge_y", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_edge_y", "location", "edge");
		writer.addVariableAttribute("mesh2d_edge_y", "bounds", "mesh2d_edge_y_bnd");

	}

	private void set_mesh2d_edge_xy_bnd_Value() throws IOException, InvalidRangeException {
		// get coordinate from edge points index
		List<Double[]> xList = new ArrayList<>();
		List<Double[]> yList = new ArrayList<>();
		this.edgeFaceIndex.keySet().forEach(edgePointsIndex -> {
			String[] pointsIndex = edgePointsIndex.split("_");
			String[] startPoint = this.points.get(Integer.parseInt(pointsIndex[0])).split("_");
			String[] endPoint = this.points.get(Integer.parseInt(pointsIndex[1])).split("_");

			xList.add(new Double[] { Double.parseDouble(startPoint[0]), Double.parseDouble(endPoint[0]) });
			yList.add(new Double[] { Double.parseDouble(startPoint[1]), Double.parseDouble(endPoint[1]) });
		});

		writer.addValue("mesh2d_edge_x_bnd",
				ArrayDouble.factory(DoubleTodouble(xList.parallelStream().toArray(Double[][]::new))));
		writer.addValue("mesh2d_edge_y_bnd",
				ArrayDouble.factory(DoubleTodouble(yList.parallelStream().toArray(Double[][]::new))));
	}

	private void set_mesh2d_edge_xy_bnd() throws IOException, InvalidRangeException {

		// x
		writer.addVariable("mesh2d_edge_x_bnd", DataType.DOUBLE, "nmesh2d_edge Two");
		writer.addVariableAttribute("mesh2d_edge_x_bnd", "units", "m");
		writer.addVariableAttribute("mesh2d_edge_x_bnd", "standard_name", "projection_x_coordinate");
		writer.addVariableAttribute("mesh2d_edge_x_bnd", "long_name",
				"x-coordinate bounds of 2D mesh edge (i.e. end point coordinates)");
		writer.addVariableAttribute("mesh2d_edge_x_bnd", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_edge_x_bnd", "location", "edge");

		// y
		writer.addVariable("mesh2d_edge_y_bnd", DataType.DOUBLE, "nmesh2d_edge Two");
		writer.addVariableAttribute("mesh2d_edge_y_bnd", "units", "m");
		writer.addVariableAttribute("mesh2d_edge_y_bnd", "standard_name", "projection_y_coordinate");
		writer.addVariableAttribute("mesh2d_edge_y_bnd", "long_name",
				"y-coordinate bounds of 2D mesh edge (i.e. end point coordinates)");
		writer.addVariableAttribute("mesh2d_edge_y_bnd", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_edge_y_bnd", "location", "edge");

	}

	private void set_mesh2d_edge_nodes_Value() throws IOException, InvalidRangeException {
		// get the point index from edge nodes index
		List<Integer[]> outList = new ArrayList<>();
		this.edgeFaceIndex.keySet().forEach(edgePointsIndex -> {
			String[] pointsIndex = edgePointsIndex.split("_");
			outList.add(new Integer[] { Integer.parseInt(pointsIndex[0]) + 1, Integer.parseInt(pointsIndex[1]) + 1 });
		});

		writer.addValue("mesh2d_edge_nodes",
				ArrayInt.factory(IntegerToint(outList.parallelStream().toArray(Integer[][]::new))));
	}

	private void set_mesh2d_edge_nodes() throws IOException, InvalidRangeException {
		//
		writer.addVariable("mesh2d_edge_nodes", DataType.INT, "nmesh2d_edge Two");
		writer.addVariableAttribute("mesh2d_edge_nodes", "cf_role", "edge_node_connectivity");
		writer.addVariableAttribute("mesh2d_edge_nodes", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_edge_nodes", "location", "edge");
		writer.addVariableAttribute("mesh2d_edge_nodes", "long_name",
				"Mapping from every edge to the two nodes that it connects");
		writer.addVariableAttribute("mesh2d_edge_nodes", "start_index", (int) 1);
		writer.addVariableAttribute("mesh2d_edge_nodes", "_FillValue", (int) -999);

	}

	private void set_mesh2d_face_nodes_Value() throws IOException, InvalidRangeException {
		// get points index from polygon point index
		List<Integer[]> outList = new ArrayList<>();
		for (List<Integer> temptPolygonPoints : this.polygonPointsIndex) {
			List<Integer> temptList = new ArrayList<>();
			for (int index = 0; index < this.maxPolygonNodeNum; index++) {
				try {
					temptList.add(temptPolygonPoints.get(index) + 1);
				} catch (Exception e) {
					temptList.add(-999);
				}
			}
			outList.add(temptList.parallelStream().toArray(Integer[]::new));
		}

		writer.addValue("mesh2d_face_nodes",
				ArrayInt.factory(this.IntegerToint(outList.parallelStream().toArray(Integer[][]::new))));
	}

	private void set_mesh2d_face_nodes() throws IOException, InvalidRangeException {
		//
		writer.addVariable("mesh2d_face_nodes", DataType.INT, "nmesh2d_face max_nmesh2d_face_nodes");
		writer.addVariableAttribute("mesh2d_face_nodes", "cf_role", "face_node_connectivity");
		writer.addVariableAttribute("mesh2d_face_nodes", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_face_nodes", "location", "face");
		writer.addVariableAttribute("mesh2d_face_nodes", "long_name",
				"Mapping from every face to its corner nodes (counterclockwise)");
		writer.addVariableAttribute("mesh2d_face_nodes", "start_index", (int) 1);
		writer.addVariableAttribute("mesh2d_face_nodes", "_FillValue", (int) -999);

	}

	private void set_mesh2d_edge_faces_Value() throws IOException, InvalidRangeException {
		// get edge index from edgeFaceIndex
		List<Integer[]> outList = new ArrayList<>();
		this.edgeFaceIndex.keySet().forEach(edgePointsIndex -> {
			List<Integer> facesIndex = new ArrayList<>();
			for (int index = facesIndex.size(); index < 2; index++) {
				try {
					facesIndex.add(edgeFaceIndex.get(edgePointsIndex).get(index) + 1);
				} catch (Exception e) {
					facesIndex.add(0);
				}
			}
			outList.add(facesIndex.parallelStream().toArray(Integer[]::new));
		});

		writer.addValue("mesh2d_edge_faces",
				ArrayInt.factory(this.IntegerToint(outList.parallelStream().toArray(Integer[][]::new))));
	}

	private void set_mesh2d_edge_faces() throws IOException, InvalidRangeException {
		//
		writer.addVariable("mesh2d_edge_faces", DataType.INT, "nmesh2d_edge Two");
		writer.addVariableAttribute("mesh2d_edge_faces", "cf_role", "edge_face_connectivity");
		writer.addVariableAttribute("mesh2d_edge_faces", "long_name",
				"Mapping from every edge to the two faces that it separates");
		writer.addVariableAttribute("mesh2d_edge_faces", "start_index", (int) 1);
		writer.addVariableAttribute("mesh2d_edge_faces", "_FillValue", (int) -999);

	}

	private void set_mesh2d_face_xy_Value() throws IOException, InvalidRangeException {
		// get the centroid of polygon
		List<Double> xList = new ArrayList<>();
		List<Double> yList = new ArrayList<>();
		this.polygonCentroid.forEach(centroid -> {
			xList.add(centroid[0]);
			yList.add(centroid[1]);
		});
		writer.addValue("mesh2d_face_x",
				ArrayDouble.factory(xList.parallelStream().mapToDouble(Double::doubleValue).toArray()));
		writer.addValue("mesh2d_face_y",
				ArrayDouble.factory(yList.parallelStream().mapToDouble(Double::doubleValue).toArray()));
	}

	private void set_mesh2d_face_xy() throws IOException, InvalidRangeException {

		// x
		writer.addVariable("mesh2d_face_x", DataType.DOUBLE, "nmesh2d_face");
		writer.addVariableAttribute("mesh2d_face_x", "units", "m");
		writer.addVariableAttribute("mesh2d_face_x", "standard_name", "projection_x_coordinate");
		writer.addVariableAttribute("mesh2d_face_x", "long_name", "Characteristic x-coordinate of mesh face");
		writer.addVariableAttribute("mesh2d_face_x", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_face_x", "location", "face");
		writer.addVariableAttribute("mesh2d_face_x", "bounds", "mesh2d_face_x_bnd");

		// y
		writer.addVariable("mesh2d_face_y", DataType.DOUBLE, "nmesh2d_face");
		writer.addVariableAttribute("mesh2d_face_y", "units", "m");
		writer.addVariableAttribute("mesh2d_face_y", "standard_name", "projection_y_coordinate");
		writer.addVariableAttribute("mesh2d_face_y", "long_name", "Characteristic y-coordinate of mesh face");
		writer.addVariableAttribute("mesh2d_face_y", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_face_y", "location", "face");
		writer.addVariableAttribute("mesh2d_face_y", "bounds", "mesh2d_face_y_bnd");
	}

	private void set_mesh2d_face_xy_bnd_Value() throws IOException, InvalidRangeException {
		// get points coordinate of polygons
		List<Double[]> polygonXList = new ArrayList<>();
		List<Double[]> polygonYList = new ArrayList<>();
		this.polygonPointsIndex.forEach(pointsIndex -> {
			List<Double> temptXList = new ArrayList<>();
			List<Double> temptYList = new ArrayList<>();

			for (int index = 0; index < this.maxPolygonNodeNum; index++) {
				try {
					String[] pointCoordinate = this.points.get(pointsIndex.get(index)).split("_");
					temptXList.add(Double.parseDouble(pointCoordinate[0]));
					temptYList.add(Double.parseDouble(pointCoordinate[1]));
				} catch (Exception e) {
					temptXList.add(-999.0);
					temptYList.add(-999.0);
				}
			}
			polygonXList.add(temptXList.parallelStream().toArray(Double[]::new));
			polygonYList.add(temptYList.parallelStream().toArray(Double[]::new));
		});

		writer.addValue("mesh2d_face_x_bnd",
				Array.factory(this.DoubleTodouble(polygonXList.parallelStream().toArray(Double[][]::new))));
		writer.addValue("mesh2d_face_y_bnd",
				Array.factory(this.DoubleTodouble(polygonYList.parallelStream().toArray(Double[][]::new))));
	}

	private void set_mesh2d_face_xy_bnd() throws IOException, InvalidRangeException {
		// x
		writer.addVariable("mesh2d_face_x_bnd", DataType.DOUBLE, "nmesh2d_face max_nmesh2d_face_nodes");
		writer.addVariableAttribute("mesh2d_face_x_bnd", "units", "m");
		writer.addVariableAttribute("mesh2d_face_x_bnd", "standard_name", "projection_x_coordinate");
		writer.addVariableAttribute("mesh2d_face_x_bnd", "long_name",
				"x-coordinate bounds of 2D mesh face (i.e. corner coordinates)");
		writer.addVariableAttribute("mesh2d_face_x_bnd", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_face_x_bnd", "location", "face");
		writer.addVariableAttribute("mesh2d_face_x_bnd", "_FillValue", -999.0);

		// y
		writer.addVariable("mesh2d_face_y_bnd", DataType.DOUBLE, "nmesh2d_face max_nmesh2d_face_nodes");
		writer.addVariableAttribute("mesh2d_face_y_bnd", "units", "m");
		writer.addVariableAttribute("mesh2d_face_y_bnd", "standard_name", "projection_y_coordinate");
		writer.addVariableAttribute("mesh2d_face_y_bnd", "long_name",
				"y-coordinate bounds of 2D mesh face (i.e. corner coordinates)");
		writer.addVariableAttribute("mesh2d_face_y_bnd", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_face_y_bnd", "location", "face");
		writer.addVariableAttribute("mesh2d_face_y_bnd", "_FillValue", -999.0);

	}

	private void set_mesh2d_Cell_Area() {
		writer.addVariable("mesh2d_flowelem_ba", DataType.DOUBLE, "nmesh2d_face");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "location", "face");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "coordinates", "mesh2d_face_x mesh2d_face_y");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "cell_methods", "nmesh2d_face: mean");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "cell_measures", "area: mesh2d_flowelem_ba");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "standard_name", "cell_area");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "long_name", "");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "units", "m2");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "grid_mapping", "projected_coordinate_system");
		writer.addVariableAttribute("mesh2d_flowelem_ba", "_FillValue", -999.0);
	}

	private void set_mesh2d_Cell_Area_Value() throws IOException, InvalidRangeException {
		List<Double> cellArea = new ArrayList<>();
		this.geoList.forEach(geo -> {
			cellArea.add(geo.GetArea());
		});
		writer.addValue("mesh2d_flowelem_ba",
				Array.factory(cellArea.parallelStream().mapToDouble(Double::doubleValue).toArray()));
	}

	private void set_mesh2d_flowelem_bl() {
		writer.addVariable("mesh2d_flowelem_bl", DataType.DOUBLE, "nmesh2d_face");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "location", "face");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "coordinates", "mesh2d_face_x mesh2d_face_y");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "cell_methods", "nmesh2d_face: mean");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "cell_measures", "area: mesh2d_flowelem_ba");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "standard_name", "altitude");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "long_name", "flow element center bedlevel (bl)");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "units", "m");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "grid_mapping", "projected_coordinate_system");
		writer.addVariableAttribute("mesh2d_flowelem_bl", "_FillValue", -999.0);
	}

	private void set_mesh2d_flowelem_bl_value() throws IOException, InvalidRangeException {

		for (int index = 0; index < this.polygonPointsIndex.size(); index++) {
			List<Double> temptValue = new ArrayList<>();

			// add bedLevel
			try {
				if (this.polygonLevel.get(index) > -998) {
					for (int tempt = 0; tempt < this.polygonPointsIndex.get(index).size(); tempt++) {
						temptValue.add(this.polygonLevel.get(index));
					}
				}
			} catch (Exception e) {
			}

			// interpolation by corner points level
			polygonPointsIndex.get(index).forEach(pointIndex -> {
				if (this.points_Z_Value.get(this.points.get(pointIndex)) > -998) {
					temptValue.add(this.points_Z_Value.get(this.points.get(pointIndex)));
				}
			});
			try {
				polygonLevel.add(index + 1, new AtCommonMath(temptValue).getMean());
				polygonLevel.remove(index);
			} catch (Exception e) {
			}
		}

		writer.addValue("mesh2d_flowelem_bl",
				Array.factory(polygonLevel.parallelStream().mapToDouble(Double::doubleValue).toArray()));
	}

	private void set_Time() {
		writer.addVariable("time", DataType.DOUBLE, "time");
		writer.addVariableAttribute("time", "standard_name", "time");
		writer.addVariableAttribute("time", "units", "seconds since 2001-01-01 00:00:00");
	}

	private void set_Time_Value() throws IOException, InvalidRangeException {
		writer.addValue("time",
				Array.factory(this.timeSeries.parallelStream().mapToDouble(Double::doubleValue).toArray()));
	}

	private void set_TimeStep() {
		writer.addVariable("timestep", DataType.DOUBLE, "time");
		writer.addVariableAttribute("timestep", "long_name",
				"Latest computational timestep size in each output interval");
		writer.addVariableAttribute("timestep", "units", "s");
	}

	private void set_TimeStep_Value() throws IOException, InvalidRangeException {
		List<Double> outList = new ArrayList<>();
		this.timeSeries.forEach(e -> outList.add(this.timeStep + 0.));
		writer.addValue("timestep", Array.factory(outList.parallelStream().mapToDouble(Double::doubleValue).toArray()));
	}

	private void set_mesh2d_waterDepth() {
		writer.addVariable("mesh2d_waterdepth", DataType.DOUBLE, "time nmesh2d_face");
		writer.addVariableAttribute("mesh2d_waterdepth", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_waterdepth", "location", "face");
		writer.addVariableAttribute("mesh2d_waterdepth", "coordinates", "mesh2d_face_x mesh2d_face_y");
		writer.addVariableAttribute("mesh2d_waterdepth", "cell_methods", "nmesh2d_face: mean");
		writer.addVariableAttribute("mesh2d_waterdepth", "cell_measures", "area: mesh2d_flowelem_ba");
		writer.addVariableAttribute("mesh2d_waterdepth", "standard_name", "sea_floor_depth_below_sea_surface");
		writer.addVariableAttribute("mesh2d_waterdepth", "long_name", "Water depth at pressure points");
		writer.addVariableAttribute("mesh2d_waterdepth", "units", "m");
		writer.addVariableAttribute("mesh2d_waterdepth", "grid_mapping", "projected_coordinate_system");
		writer.addVariableAttribute("timestep", "_FillValue", -999.0);
	}

	private void set_mesh2d_waterLevel() {
		writer.addVariable("mesh2d_s1", DataType.DOUBLE, "time nmesh2d_face");
		writer.addVariableAttribute("mesh2d_s1", "mesh", "mesh2d");
		writer.addVariableAttribute("mesh2d_s1", "location", "face");
		writer.addVariableAttribute("mesh2d_s1", "coordinates", "mesh2d_face_x mesh2d_face_y");
		writer.addVariableAttribute("mesh2d_s1", "cell_methods", "nmesh2d_face: mean");
		writer.addVariableAttribute("mesh2d_s1", "cell_measures", "area: mesh2d_flowelem_ba");
		writer.addVariableAttribute("mesh2d_s1", "standard_name", "sea_surface_height");
		writer.addVariableAttribute("mesh2d_s1", "long_name", "Water level");
		writer.addVariableAttribute("mesh2d_s1", "units", "m");
		writer.addVariableAttribute("mesh2d_s1", "grid_mapping", "projected_coordinate_system");
		writer.addVariableAttribute("timestep", "_FillValue", -999.0);
	}

	private void set_mesh2d_waterLevel_waterDepth_value() throws IOException, InvalidRangeException {
		// set water depth value;
		writer.addValue("mesh2d_waterdepth",
				Array.factory(DoubleTodouble(this.polygonTimeSeries.parallelStream().toArray(Double[][]::new))));

		// set water level value
		List<Double[]> timeSeriesWaterLevel = new ArrayList<>();
		for (int time = 0; time < this.polygonTimeSeries.size(); time++) {

			List<Double> waterLevel = new ArrayList<>();
			for (int polygon = 0; polygon < this.polygonPointsIndex.size(); polygon++) {
				if (this.polygonLevel.get(polygon) > -990) {
					waterLevel.add(this.polygonTimeSeries.get(time)[polygon] + this.polygonLevel.get(polygon));
				} else {
					waterLevel.add(this.polygonTimeSeries.get(time)[polygon]);
				}
			}
			timeSeriesWaterLevel.add(polygonLevel.parallelStream().toArray(Double[]::new));
		}
		writer.addValue("mesh2d_s1",
				Array.factory(DoubleTodouble(timeSeriesWaterLevel.parallelStream().toArray(Double[][]::new))));
	}

	private void set_Projection_Value() throws IOException, InvalidRangeException {
		ArrayInt.D0 array = new ArrayInt.D0();
		array.set(-2147483647);
		writer.addValue("mesh2d", array);
	}

	private void set_mesh2D_Value() throws IOException, InvalidRangeException {
		ArrayInt.D0 array = new ArrayInt.D0();
		array.set(-2147483647);
		writer.addValue("projected_coordinate_system", array);
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ User Optional ++++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>

	public DflowNetcdfTranslator setNodeLevel(AsciiBasicControl ascii) throws IOException {
		// setZ
		this.points.forEach(nodeXY -> {
			double[] coordinate = Arrays.asList(nodeXY.split("_")).parallelStream().map(s -> Double.parseDouble(s))
					.mapToDouble(Double::doubleValue).toArray();

			if (ascii.isContain(coordinate[0], coordinate[1])) {
				String temptValue = ascii.getValue(coordinate[0], coordinate[1]);
				if (!temptValue.equals(ascii.getNullValue())) {
					points_Z_Value.put(nodeXY, Double.parseDouble(temptValue));
				}
			}
		});
		return this;
	}

	public DflowNetcdfTranslator setFaceCenterLevel(AsciiBasicControl ascii) {

		// set bedLevel
		for (int index = 0; index < this.polygonCentroid.size(); index++) {
			if (ascii.isContain(polygonCentroid.get(index)[0], polygonCentroid.get(index)[1])) {
				String temptValue = ascii.getValue(polygonCentroid.get(index)[0], polygonCentroid.get(index)[1]);
				if (!temptValue.equals(ascii.getNullValue())) {
					this.polygonLevel.add(index + 1, Double.parseDouble(temptValue));
					this.polygonLevel.remove(index);
				}
			}
		}
		return this;
	}

	public List<Double[]> getAllPoints() {
		List<Double[]> outPoints = new ArrayList<>();
		this.points.forEach(point -> {
			String[] coordinate = point.split("_");
			outPoints.add(new Double[] { Double.parseDouble(coordinate[0]), Double.parseDouble(coordinate[1]) });
		});
		return outPoints;
	}

	public List<Double[]> getAllEdge() {
		List<Double[]> outPoints = new ArrayList<>();
		this.edgeFaceIndex.keySet().forEach(edgePointsIndex -> {
			String pointsIndex[] = edgePointsIndex.split("_");
			String startPoint[] = this.points.get(Integer.parseInt(pointsIndex[0])).split("_");
			String endPoint[] = this.points.get(Integer.parseInt(pointsIndex[1])).split("_");

			outPoints.add(new Double[] { (Double.parseDouble(startPoint[0]) + Double.parseDouble(endPoint[0])) / 2,
					(Double.parseDouble(startPoint[1]) + Double.parseDouble(endPoint[1])) / 2 });
		});
		return outPoints;
	}

	public List<Double> getPolygonArea() {
		return this.geoList.parallelStream().map(geo -> new Double(geo.GetArea())).collect(Collectors.toList());
	}

	public List<Double> getPolygonBedLevel() {
		List<Double> polygonBedLevel = new ArrayList<>();
		this.polygonPointsIndex.forEach(polygon -> {

			List<Double> temptValue = new ArrayList<>();
			polygon.forEach(pointIndex -> {
				if (this.points_Z_Value.get(this.points.get(pointIndex)) > -998) {
					temptValue.add(this.points_Z_Value.get(this.points.get(pointIndex)));
				}
			});
			try {
				polygonBedLevel.add(new AtCommonMath(temptValue).getMean());
			} catch (Exception e) {
				polygonBedLevel.add(-999.0);
			}
		});
		return polygonBedLevel;
	}

	// timeSeries from 2001-01-01 00:00:00 in second
	// timeStep in secon
	public DflowNetcdfTranslator set_outputTimeSeries(int timeStep, List<Double> timeSeries_Second) {
		this.timeSeries = timeSeries_Second;
		this.timeStep = timeStep;
		this.TimeSeriesOutput = true;
		return this;
	}

	// add water depth
	public DflowNetcdfTranslator addWaterDepth(List<AsciiBasicControl> asciiList) {

		// initialize the global variable
		this.waterLevelOutput = true;
		this.polygonTimeSeries.clear();
		List<List<Integer[]>> polygonValueIndex = new ArrayList<>();

		// initialize the polygonValeIndex
		this.geoList.forEach(geo -> {
			try {
				polygonValueIndex.add(getIndexFromAscii(geo, asciiList.get(0)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		// to check each time series value in asciiList
		asciiList.forEach(temptAscii -> {
			List<Double> timeSeriesValues = new ArrayList<>();

			// set timeSeries from asciiList
			polygonValueIndex.forEach(valueIndexs -> {

				// to check each grid that polygon contains
				List<Double> temptValue = new ArrayList<>();
				valueIndexs.forEach(index -> {
					temptValue.add(Double.parseDouble(temptAscii.getValue(index[0], index[1])));
				});

				// get mean value of temptValue
				// if no data in list add null value to list
				try {
					timeSeriesValues.add(new AtCommonMath(temptValue).getMean());
				} catch (Exception e) {
					timeSeriesValues.add(-999.0);
				}
			});

			this.polygonTimeSeries.add(timeSeriesValues.parallelStream().toArray(Double[]::new));
		});
		return this;
	}

	// get the coordinate index which geometry contains from asciiFile
	private List<Integer[]> getIndexFromAscii(Geometry geo, AsciiBasicControl ascii) throws IOException {
		List<Path2D> pathList = GdalGlobal.GeomertyToPath2D(geo);
		List<Integer[]> coordinateIndex = new ArrayList<>();

		pathList.forEach(path -> {
			Rectangle rec = path.getBounds();

			int[] bottomLeftCenter = ascii.getPosition(rec.getMinX(), rec.getMinY());
			int[] topRightCenter = ascii.getPosition(rec.getMaxX(), rec.getMaxY());

			for (int row = topRightCenter[1]; row <= bottomLeftCenter[1]; row++) {
				for (int column = bottomLeftCenter[0]; column <= topRightCenter[0]; column++) {

					// check for no value grid
					String temptValue = ascii.getValue(column, row);
					if (!temptValue.equals(ascii.getNullValue())) {

						// check is inside the path
						double[] coordinate = ascii.getCoordinate(column, row);
						if (path.contains(coordinate[0], coordinate[1])) {
							coordinateIndex.add(new Integer[] { column, row });
						}
					}
				}
			}
		});
		return coordinateIndex;
	}

	private double[][] DoubleTodouble(Double[][] temptDouble) {
		double[][] newDouble = new double[temptDouble.length][];
		for (int index = 0; index < temptDouble.length; index++) {
			newDouble[index] = Arrays.asList(temptDouble[index]).parallelStream().mapToDouble(Double::doubleValue)
					.toArray();
		}
		return newDouble;
	}

	private int[][] IntegerToint(Integer[][] temptInt) {
		int[][] newInt = new int[temptInt.length][];
		for (int index = 0; index < temptInt.length; index++) {
			newInt[index] = Arrays.asList(temptInt[index]).parallelStream().mapToInt(Integer::intValue).toArray();
		}
		return newInt;
	}

}
