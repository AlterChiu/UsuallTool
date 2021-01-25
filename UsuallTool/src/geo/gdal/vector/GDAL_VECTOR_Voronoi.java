
package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.Geometry;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtFileFunction;
import usualTool.AtFileWriter;

public class GDAL_VECTOR_Voronoi {
	private String temptFolder = AtFileFunction.createTemptFolder();
	private String inputLayer = temptFolder + "\\temptPoints.shp";
	private double buffer = 0.0;

	public GDAL_VECTOR_Voronoi(String inputLayer) throws UnsupportedEncodingException {
		List<Geometry> geoList = new SpatialReader(inputLayer).getGeometryList();
		processing(geoList);
	}

	public GDAL_VECTOR_Voronoi(List<Geometry> geoList) {
		processing(geoList);
	}

	public GDAL_VECTOR_Voronoi(Geometry geo) {
		List<Geometry> geoList = new ArrayList<>();
		geoList.add(geo);
		processing(geoList);
	}

	private void processing(List<Geometry> geoList) {
		// translate shapeFile to points
		List<Geometry> outPoints = new ArrayList<>();

		geoList.forEach(geo -> {
			GdalGlobal.MultiPolyToSingle(geo).forEach(singlePolygon -> {
				GdalGlobal.GeometryToPointGeos(singlePolygon).forEach(point -> {
					outPoints.add(point);
				});
			});
		});
		new SpatialWriter().setGeoList(outPoints).saveAsShp(this.inputLayer);
	}

	public void setBuffer(double buffer) {
		this.buffer = buffer;
	}

	public void saveAsShp(String saveAdd) throws IOException, InterruptedException {
		List<String> batContent = new ArrayList<>();

		// initial GdalPython enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(command -> batContent.add(command));
		batContent.add("\"%PYTHONHOME%\\python\" AtVoronoiPolygons.py");
		batContent.add("exit");
		new AtFileWriter(batContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtVoronoiPolygons.bat").textWriter("");

		// initial QgisAlogrithm pythonFile
		List<String> pythonContent = new ArrayList<>();
		GdalGlobal.QGIS_Processing_PythonInitialize().forEach(content -> pythonContent.add(content));

		// create voronoiPolygons parameter
		StringBuilder parameter = new StringBuilder();
		parameter.append("parameter = {");
		parameter.append("\"INPUT\":\"");
		parameter.append(this.inputLayer.replace("\\", "/") + "\",");

		parameter.append("\"BUFFER\":");
		parameter.append(buffer + ",");

		parameter.append("\"OUTPUT\":\"");
		parameter.append(saveAdd.replace("\\", "/") + "\"}");
		pythonContent.add(parameter.toString());

		// create pythonAlogrithm processing
		pythonContent.add("processing.run('qgis:voronoipolygons',parameter)");
		new AtFileWriter(pythonContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtVoronoiPolygons.py").textWriter("");

		// run batFile
		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("AtVoronoiPolygons.bat");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();
		this.close();
	}

	public List<Geometry> getGeoList() throws IOException, InterruptedException {
		String temptSaveName = AtFileFunction.getTempFileName(this.temptFolder, ".shp");
		this.saveAsShp(this.temptFolder + temptSaveName);
		List<Geometry> outGeoList = new SpatialReader(this.temptFolder + temptSaveName).getGeometryList();
		this.close();
		return outGeoList;
	}

	private final void close() {
		AtFileFunction.delete(this.temptFolder);
	}

}
