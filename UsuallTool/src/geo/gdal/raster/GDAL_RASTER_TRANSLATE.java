package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_RASTER_TRANSLATE {

	private String nullValue = "-999";
	private int coordinateSystem = 0;
	private String originalFile = "";

	public GDAL_RASTER_TRANSLATE(String originalFile) {
		this.originalFile = originalFile;
	}

	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	public void setCoordinate(int coordinate) {
		this.coordinateSystem = coordinate;
	}

	public void save(String saveAdd, String dataType) throws IOException, InterruptedException {
		/*
		 * clear gdalGlobal temptFolder
		 */
		for (String fileName : new File(GdalGlobal.temptFolder).list()) {
			FileFunction.delete(GdalGlobal.temptFolder + "\\" + fileName);
		}

		/*
		 * setting translate .bat file
		 */
		List<String> batFile = new ArrayList<>();
		GdalGlobal.GDAL_EnviromentStarting().forEach(line -> batFile.add(line));
		StringBuilder translateCommand = new StringBuilder();
		translateCommand.append("gdal_translate");
		if (this.coordinateSystem != 0) {
			translateCommand.append(" -a_srs EPSG:" + this.coordinateSystem);
		}
		translateCommand.append(" -a_nodata " + this.nullValue);
		translateCommand.append(" -of " + dataType);
		translateCommand.append(" " + this.originalFile);
		translateCommand.append(" " + saveAdd);

		batFile.add(translateCommand.toString());
		new AtFileWriter(batFile.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//gdal_translate_tempt.bat").textWriter("");

		/*
		 * run bat file
		 */
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/B");
		runCommand.add(GdalGlobal.gdalBinFolder + "//gdal_translate_tempt.bat");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();

	}
}
