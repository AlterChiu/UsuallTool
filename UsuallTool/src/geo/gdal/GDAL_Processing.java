package geo.gdal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gdal.ogr.Geometry;

import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_Processing {
	private String temptFolder = GdalGlobal.temptFolder + "Processing";
	private List<Map<String, String>> parameters = new ArrayList<>();
	private List<String> processingAlgrothims = new ArrayList<>();
	private String inputLayer = temptFolder + "//temptShp.shp";

	public GDAL_Processing(String inputLayer) {
		processing(new SpatialReader(inputLayer).getGeometryList());
	}

	public GDAL_Processing(List<Geometry> geoList) {
		processing(geoList);
	}

	public GDAL_Processing(Geometry geo) {
		List<Geometry> geoList = new ArrayList<>();
		geoList.add(geo);
		processing(geoList);
	}

	private void processing(List<Geometry> geoList) {
		// clear temptFolder
		FileFunction.newFolder(this.temptFolder);
		for (String fileName : new File(this.temptFolder).list()) {
			FileFunction.delete(this.temptFolder + "\\" + fileName);
		}

		// translate shapeFile to points
		new SpatialWriter().setGeoList(geoList).saveAsShp(this.inputLayer);
	}

	public void addProcessing(String processing, Map<String, String> paramteres) {
		if (this.processingAlgrothims.size() != 0) {
			Map<String, String> lastParameter = this.parameters.get(this.parameters.size() - 1);

			// make input file to last output of processing
			String inputAdd = (String) Optional.ofNullable(paramteres.get("INPUT")).orElse(lastParameter.get("OUTPUT"));
			paramteres.put("INPUT", inputAdd);

			// if no address to save file, save to temptFolder
			String outputTemptFile = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
			String outputAdd = Optional.ofNullable(paramteres.get("OUTPUT"))
					.orElse(this.temptFolder.replace("\\", "/") + "/" + outputTemptFile);
			paramteres.put("OUTPUT", outputAdd);

		} else if (this.processingAlgrothims.size() == 0) {
			String outputTemptFile = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
			paramteres.put("OUTPUT", this.temptFolder.replace("\\", "/") + "/" + outputTemptFile);
			paramteres.put("INPUT", this.inputLayer.replace("\\", "/"));

			this.processingAlgrothims.add(processing);
			this.parameters.add(paramteres);
		}
	}

	public void saveAsShp(String saveAdd) throws IOException, InterruptedException {
		List<String> batContent = new ArrayList<>();

		// initial GdalPython enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(command -> batContent.add(command));
		batContent.add("\"%PYTHONHOME%\\python\" AtQgisAlgrothimProcessing.py");
		batContent.add("exit");
		new AtFileWriter(batContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtQgisAlgrothimProcessing.bat").textWriter("");

		// initial QgisAlogrithm pythonFile
		List<String> pythonContent = new ArrayList<>();
		GdalGlobal.QGIS_Processing_PythonInitialize().forEach(content -> pythonContent.add(content));

		// create voronoiPolygons parameter
		for (int index = 0; index < this.processingAlgrothims.size(); index++) {
			StringBuilder parameterString = new StringBuilder();
			List<String> parameterList = new ArrayList<>();
			parameterString.append("parameter" + index + "={");

			Map<String, String> temptParameters = this.parameters.get(index);
			for (String key : temptParameters.keySet()) {
				parameterList.add("\"" + key + "\":");
				parameterList.add(temptParameters.get(key) + "");

			}
			parameterString.append(String.join(",", parameterList) + "}");
			pythonContent.add(parameterString.toString());

			StringBuilder processRunString = new StringBuilder();
			processRunString.append("processing.run(\"");
			processRunString.append(this.processingAlgrothims.get(index) + "\",");
			processRunString.append("parameter" + index);
			processRunString.append(")");
			pythonContent.add(processRunString.toString());
		}

		// create pythonAlogrithm processing
		new AtFileWriter(pythonContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtQgisAlgrothimProcessing.py").textWriter("");

		// run batFile
		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("AtQgisAlgrothimProcessing.bat");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();
	}

	public List<Geometry> getDefensigyGeoList() throws IOException, InterruptedException {
		String temptSaveFileName = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
		String temptSaveFileAdd = this.temptFolder + "\\" + temptSaveFileName;
		this.saveAsShp(temptSaveFileAdd);

		return new SpatialReader(temptSaveFileAdd).getGeometryList();
	}

}
