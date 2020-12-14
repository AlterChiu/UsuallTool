
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

public class GDAL_VECTOR_SplitByLine {
	private String prefixName = "SplitByLine";
	private String temptFolder = GdalGlobal.temptFolder;
	private String inputLayer = temptFolder + "\\temptShp.shp";
	private String splitLineLayer = temptFolder + "\\splitLine.shp";
	private List<Geometry> splitLines = new ArrayList<>();

	public GDAL_VECTOR_SplitByLine(String inputLayer) throws UnsupportedEncodingException {
		processing(new SpatialReader(inputLayer).getGeometryList());
	}

	public GDAL_VECTOR_SplitByLine(List<Geometry> geoList) {
		processing(geoList);
	}

	public GDAL_VECTOR_SplitByLine(Geometry geo) {
		List<Geometry> geoList = new ArrayList<>();
		geoList.add(geo);
		processing(geoList);
	}

	private void processing(List<Geometry> geoList) {
		// clear temptFolder
		this.temptFolder = GdalGlobal.createTemptFolder(this.prefixName);

		// translate shapeFile to points
		new SpatialWriter().setGeoList(geoList).saveAsShp(this.inputLayer);
	}

	public void addSplitLine(List<Geometry> splitLines) {
		splitLines.forEach(geo -> this.splitLines.add(geo));
	}

	public void addSplitLine(Geometry splitLine) {
		this.splitLines.add(splitLine);
	}

	public void addSplitLine(String splitLineSHP) throws UnsupportedEncodingException {
		new SpatialReader(splitLineSHP).getGeometryList().forEach(geo -> this.splitLines.add(geo));
	}

	public void saveAsShp(String saveAdd) throws IOException, InterruptedException {

		// save split line to new shapeFile
		new SpatialWriter().setGeoList(this.splitLines).saveAsShp(this.splitLineLayer);

		List<String> batContent = new ArrayList<>();

		// initial GdalPython enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(command -> batContent.add(command));
		batContent.add("\"%PYTHONHOME%\\python\" AtSplitByLine.py");
		batContent.add("exit");
		new AtFileWriter(batContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtSplitByLine.bat").textWriter("");

		// initial QgisAlogrithm pythonFile
		List<String> pythonContent = new ArrayList<>();
		GdalGlobal.QGIS_Processing_PythonInitialize().forEach(content -> pythonContent.add(content));

		// create voronoiPolygons parameter
		StringBuilder parameter = new StringBuilder();
		parameter.append("parameter = {");
		parameter.append("\"INPUT\":\"");
		parameter.append(this.inputLayer.replace("\\", "/") + "\",");

		parameter.append("\"LINES\":\"");
		parameter.append(this.splitLineLayer.replace("\\", "/") + "\",");

		parameter.append("\"OUTPUT\":\"");
		parameter.append(saveAdd.replace("\\", "/") + "\"}");
		pythonContent.add(parameter.toString());

		// create pythonAlogrithm processing
		pythonContent.add("processing.run('qgis:splitwithlines',parameter)");
		new AtFileWriter(pythonContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtSplitByLine.py").textWriter("");

		// run batFile
		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("AtSplitByLine.bat");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();
		this.close();
	}

	public List<Geometry> getGeoList() throws IOException, InterruptedException {
		this.temptFolder = GdalGlobal.createTemptFolder(this.prefixName);

		String temptSaveName = GdalGlobal.getTempFileName(this.temptFolder, ".shp");
		this.saveAsShp(this.temptFolder + temptSaveName);
		List<Geometry> outGeoList = new SpatialReader(GdalGlobal.temptFolder + temptSaveName).getGeometryList();
		this.close();
		return outGeoList;
	}

	private final void close() {
		FileFunction.delete(this.temptFolder);
	}

}
