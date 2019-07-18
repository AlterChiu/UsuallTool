package geo.gdal.Interpolation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;

public interface AtInterpolation {

	public AsciiBasicControl getAscii(Map<String, Double> boundary, double cellSize) throws IOException, InterruptedException;

	public AsciiBasicControl getAscii(AsciiBasicControl ascii) throws IOException, InterruptedException;

	public List<Double[]> getXYZ(Map<String, Double> boundary, double cellSize) throws IOException, InterruptedException;

	public List<Double[]> getXYZ(List<Double[]> xyList) throws IOException, InterruptedException;

	public List<Double[]> getGeometry(List<Geometry> geometryList) throws IOException, InterruptedException;
	
}
