package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;
import usualTool.AtFileWriter;

public class GDAL_VECTOR_Voronoi {
	private String inputLayer;
	private double buffer = 0.0;

	public GDAL_VECTOR_Voronoi(String inputLayer) {
		this.inputLayer = inputLayer;
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
		parameter.append("\"INPUT\":");
		parameter.append(this.inputLayer.replace("\\", "/") + ",");

		parameter.append("\"BUFFER\":");
		parameter.append(buffer + ",");

		parameter.append("\"OUTPUT\":");
		parameter.append(saveAdd + "}");
		pythonContent.add(parameter.toString());

		// create pythonAlogrithm processing
		pythonContent.add("processing.run('qgis:voronoipolygons',parameter)");

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
	}

}
