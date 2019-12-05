package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_RASTER_Translate {

	private String nullValue = "-999";
	private int coordinateSystem = 0;
	private String originalFile = "";
	private String clipBoundary = "";

	public GDAL_RASTER_Translate(String originalFile) {
		this.originalFile = originalFile;
	}

	public GDAL_RASTER_Translate setNullValue(String nullValue) {
		this.nullValue = nullValue;
		return this;
	}

	public GDAL_RASTER_Translate setCoordinate(int coordinate) {
		this.coordinateSystem = coordinate;
		return this;
	}

	public GDAL_RASTER_Translate setBoundary(double minX, double maxX, double minY, double maxY) {
		this.clipBoundary = "";

		StringBuilder sb = new StringBuilder();
		sb.append("-projwin ");
		sb.append(minX + " ");
		sb.append(maxX + " ");
		sb.append(minY + "");
		sb.append(maxY + "");

		this.clipBoundary = sb.toString();
		return this;
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

		// setting gdal working enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(line -> batFile.add(line));

		// setting gdal_translate setting
		StringBuilder translateCommand = new StringBuilder();
		translateCommand.append("gdal_translate");

		// setting clip boundary
		translateCommand.append(this.clipBoundary);

		// setting coordination system
		if (this.coordinateSystem != 0) {
			translateCommand.append(" -a_srs EPSG:" + this.coordinateSystem);
		}

		// setting no-data value
		translateCommand.append(" -a_nodata " + this.nullValue);

		// setting output data type
		translateCommand.append(" -of " + dataType);

		// setting original file
		translateCommand.append(" " + this.originalFile);

		// setting targetFile
		translateCommand.append(" " + saveAdd);

		batFile.add(translateCommand.toString());
		new AtFileWriter(batFile.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//gdal_translate_tempt.bat").setEncoding(AtFileWriter.ANSI).textWriter("");

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
