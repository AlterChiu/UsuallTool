
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
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_VECTOR_Defensify {
	private String temptFolder = GdalGlobal.temptFolder + "DensifyInterva";
	private String inputLayer = temptFolder + "\\temptShp.shp";
	private double interval = 1.0;

	public GDAL_VECTOR_Defensify(String inputLayer) throws UnsupportedEncodingException {
		processing(new SpatialReader(inputLayer).getGeometryList());
	}

	public GDAL_VECTOR_Defensify(List<Geometry> geoList) {
		processing(geoList);
	}

	public GDAL_VECTOR_Defensify(Geometry geo) {
		List<Geometry> geoList = new ArrayList<>();
		geoList.add(geo);
		processing(geoList);
	}

	private void processing(List<Geometry> geoList) {
		// clear temptFolder
		this.temptFolder = this.temptFolder + "-" + GdalGlobal.getTempFileName(GdalGlobal.temptFolder, "");
		FileFunction.newFolder(this.temptFolder);
		for (String fileName : new File(this.temptFolder).list()) {
			FileFunction.delete(this.temptFolder + "\\" + fileName);
		}

		// translate shapeFile to points
		new SpatialWriter().setGeoList(geoList).saveAsShp(this.inputLayer);
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public void saveAsShp(String saveAdd) throws IOException, InterruptedException {
		List<String> batContent = new ArrayList<>();

		// initial GdalPython enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(command -> batContent.add(command));
		batContent.add("\"%PYTHONHOME%\\python\" AtDefensifyInterval.py");
		batContent.add("exit");
		new AtFileWriter(batContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtDefensifyInterval.bat").textWriter("");

		// initial QgisAlogrithm pythonFile
		List<String> pythonContent = new ArrayList<>();
		GdalGlobal.QGIS_Processing_PythonInitialize().forEach(content -> pythonContent.add(content));

		// create voronoiPolygons parameter
		StringBuilder parameter = new StringBuilder();
		parameter.append("parameter = {");
		parameter.append("\"INPUT\":\"");
		parameter.append(this.inputLayer.replace("\\", "/") + "\",");

		parameter.append("\"INTERVAL\":");
		parameter.append(this.interval + ",");

		parameter.append("\"OUTPUT\":\"");
		parameter.append(saveAdd.replace("\\", "/") + "\"}");
		pythonContent.add(parameter.toString());

		// create pythonAlogrithm processing
		pythonContent.add("processing.run('native:densifygeometriesgivenaninterval',parameter)");
		new AtFileWriter(pythonContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtDefensifyInterval.py").textWriter("");

		// run batFile
		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("AtDefensifyInterval.bat");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();
		this.close();
	}

	public List<Geometry> getGeoList() throws IOException, InterruptedException {
		String temptSaveName = GdalGlobal.getTempFileName(GdalGlobal.temptFolder, ".shp");
		this.saveAsShp(GdalGlobal.temptFolder + temptSaveName);
		List<Geometry> outGeoList = new SpatialReader(GdalGlobal.temptFolder + temptSaveName).getGeometryList();
		this.close();
		return outGeoList;
	}

	private final void close() {
		FileFunction.delete(this.temptFolder);
	}
}
