package geo.gdal.Vector.CenterLine;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.gdal.ogr.Geometry;

import geo.common.GeoVector;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.MathEqualtion.AtLineIntersection;

public class AtCenterLine {
	private List<Geometry> outGeoList = new ArrayList<>();

	public AtCenterLine(String shpFileAdd) {

		// get the geoList
		SpatialReader shapeFile = new SpatialReader(shpFileAdd);
		List<Geometry> geoList = shapeFile.getGeometryList();

		// starting process
		centerLineProcess(geoList);
	}

	public AtCenterLine(List<Geometry> geoList) {

		// starting process
		centerLineProcess(geoList);
	}

	private void centerLineProcess(List<Geometry> geoList) {
		// doing process by each polygon
		outGeoList.clear();
		for (Geometry geo : geoList) {

			// get the polygon points
			List<Double[]> pointList = geoListToPointList(geo);
			System.out.println(pointList.size());

			// get the centerLinePoint
			List<Double[]> cneterLinePoints = getCenterLinePoints(geo, pointList);

			// link the centerLinePoint
			List<Geometry> geoCenterLine = centerLinePolyLine(cneterLinePoints, geo);
			geoCenterLine.forEach(centerLine -> outGeoList.add(centerLine));
		}
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++ Output to ShapeFile ++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	public void outputShpaFile(String saveAdd) {
		SpatialWriter sw = new SpatialWriter();
		sw.setGeoList(outGeoList);
		sw.saveAsShp(saveAdd);
	}

	public List<Geometry> getCenterLine() {
		return this.outGeoList;
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++Link centerLine points +++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	private List<Geometry> centerLinePolyLine(List<Double[]> points, Geometry geo) {
		List<List<Double[]>> linkedPoints = new ArrayList<>();
		Set<String> storedPoints = new TreeSet<>();
		Map<String, Double[]> remainPoints = new TreeMap<>();
		points.forEach(e -> {
			remainPoints.put(e[0] + "_" + e[1], e);
		});

		// detect for each point
		while (remainPoints.size() > 0) {

			Double[] startPoint = points.get(0);
			remainPoints.remove(startPoint[0] + "_" + startPoint[1]);

			List<Double[]> temptLink = new ArrayList<>();
			temptLink.add(startPoint);
			storedPoints.add(startPoint[0] + "_" + startPoint[1]);

			// the first way
			List<Double[]> linkablePoints = getContainsPoints(startPoint, remainPoints, geo);
			Double[] nextPoint = linkablePoints.get(0);

			// detect which point is linkable,
			// (a) link line is contained by the geoPolygon
			// (b) length of link line must smaller than length of "nextPoint to startPoint"
			while (linkablePoints.size() > 0 && nextPoint[2] < GeoVector.getDis(nextPoint, startPoint)) {
				temptLink.add(nextPoint);
				remainPoints.remove(nextPoint[0] + "_" + nextPoint[1]);

				// if next point linked before, break the loop
				if (storedPoints.contains(nextPoint[0] + "_" + nextPoint[1])) {
					break;
				} else {
					storedPoints.add(nextPoint[0] + "_" + nextPoint[1]);
					linkablePoints = getContainsPoints(linkablePoints.get(0), remainPoints, geo);
					nextPoint = linkablePoints.get(0);
				}
			}

			// the second way
			linkablePoints = getContainsPoints(startPoint, remainPoints, geo);
			nextPoint = linkablePoints.get(0);

			while (linkablePoints.size() > 0 && nextPoint[2] < GeoVector.getDis(nextPoint, startPoint)) {
				temptLink.add(0, nextPoint);
				remainPoints.remove(nextPoint[0] + "_" + nextPoint[1]);

				// if next point linked before break the loop
				if (storedPoints.contains(nextPoint[0] + "_" + nextPoint[1])) {
					break;
				} else {
					storedPoints.add(nextPoint[0] + "_" + nextPoint[1]);
					linkablePoints = getContainsPoints(linkablePoints.get(0), remainPoints, geo);
					nextPoint = linkablePoints.get(0);
				}
			}

			linkedPoints.add(temptLink);
		}

		/*
		 * make linked points to geometry
		 */
		List<Geometry> outList = new ArrayList<>();
		for (List<Double[]> temptList : linkedPoints) {
			outList.add(GdalGlobal.LineToGeometry(temptList));
		}
		return outList;
	}

	// check the lineString is in the polygon or not
	// and store the distance
	// return Double[] = {x , y , distance}
	private List<Double[]> getContainsPoints(Double[] point, Map<String, Double[]> points, Geometry geo) {
		List<Double[]> temptList = new ArrayList<>();

		points.keySet().forEach(e -> {
			Double[] temptPoint = points.get(e);
			Geometry polyLine = GdalGlobal.LineToGeometry(point, temptPoint);
			if (geo.Contains(polyLine)) {
				temptList.add(new Double[] { temptPoint[0], temptPoint[1], GeoVector.getDis(temptPoint, point) });
			}
		});

		Collections.sort(temptList, (s1, s2) -> s1[2].compareTo(s2[2]));
		return temptList;
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++get centerLine points ++++++++++++++>
	// <+++++++++++++++++++++++++++++++++++++++++F+++++>
	private List<Double[]> getCenterLinePoints(Geometry geo, List<Double[]> polyPoints) {
		List<Double[]> cnterLinePoints = new ArrayList<>();
		for (int index = 0; index < polyPoints.size(); index++) {
			Double[] point = polyPoints.get(index);

			// get the besides points , if over list, go back
			Double[] point1;
			try {
				point1 = polyPoints.get(index + 1);
			} catch (Exception e) {
				point1 = polyPoints.get(index + 1 - polyPoints.size());
			}
			Double[] point2;
			try {
				point2 = polyPoints.get(index - 1);
			} catch (Exception e) {
				point2 = polyPoints.get(index - 1 + polyPoints.size());
			}

			// get the perpendicular line
			Double[] line1 = GeoVector.getPerpendicularBisector(point, point1);
			Double[] line2 = GeoVector.getPerpendicularBisector(point, point2);

			// get the in-cneter of the three points
			Double[] inCenter = getIntersectPoint(line1, line2);

			// check the point is in polygon or not
			// if in the polygon, store it in the list
			if (geo.Contains(GdalGlobal.pointToGeometry(inCenter))) {
				cnterLinePoints.add(new Double[] { inCenter[0], inCenter[1] });
			}
		}

		return cnterLinePoints;
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <++++++++++++++++++Vector Function +++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// y = ax + b , line[] = {a,b}
	private Double[] getIntersectPoint(Double[] line1, Double[] line2) {
		AtLineIntersection intersectPoint = new AtLineIntersection(line1[0],line1[1], line2[0],line2[1]);
		double temptPoint[] = intersectPoint.getIntersect();
		return new Double[] { temptPoint[0], temptPoint[1] };
	}

	// <++++++++++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++++ Translation Function +++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++++++++>
	private List<Double[]> geoListToPointList(Geometry geo) {
		return GdalGlobal.getBreakPoint(geo);
	}

}
