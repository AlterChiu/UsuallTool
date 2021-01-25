
package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import geo.gdal.GdalGlobal;
import usualTool.AtFileFunction;
import usualTool.AtFileWriter;

public class GDAL_RASTER_TranslateFormat {
	private String temptFolder = AtFileFunction.createTemptFolder();

	private String nullValue = "-999";
	private String originalFile = "";
	private String clipBoundary = "";

	public GDAL_RASTER_TranslateFormat(String originalFile) {
		this.originalFile = originalFile;
	}

	public GDAL_RASTER_TranslateFormat setNullValue(String nullValue) {
		this.nullValue = nullValue;
		return this;
	}

	public GDAL_RASTER_TranslateFormat setBoundary(double minX, double maxX, double minY, double maxY) {
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
		String newFolderName = AtFileFunction.getTempFileName(this.temptFolder, "");
		String newWorkSpace = this.temptFolder + newFolderName + "\\";
		AtFileFunction.newFolder(newWorkSpace);

		/*
		 * save sourceFile to temptFolder
		 */
		String sourceFileExtension = this.originalFile.substring(this.originalFile.lastIndexOf("."));
		String saveFileExtension = saveAdd.substring(saveAdd.lastIndexOf("."));

		String temptSorceFile = newWorkSpace + AtFileFunction.getTempFileName(newWorkSpace, sourceFileExtension);
		String temptSaveFile = this.temptFolder + AtFileFunction.getTempFileName(this.temptFolder, saveFileExtension);
		AtFileFunction.copyFile(this.originalFile, temptSorceFile);

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

		// setting no-data value
		translateCommand.append(" -a_nodata " + this.nullValue);

		// setting output data type
		translateCommand.append(" -of " + dataType);

		// setting original file
		translateCommand.append(" " + temptSorceFile + "");

		// setting targetFile
		translateCommand.append(" " + temptSaveFile + "\"");

		batFile.add(translateCommand.toString());

		// close batch file
		batFile.add("exit");
		new AtFileWriter(batFile.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//gdal_translate_tempt.bat").setEncoding(AtFileWriter.ANSI).textWriter("");

		/*
		 * run bat file
		 */
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/wait");
		runCommand.add("/B");
		runCommand.add(GdalGlobal.gdalBinFolder + "//gdal_translate_tempt.bat");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();

		/*
		 * move temptSaved file to targetAdd
		 */
		AtFileFunction.copyFile(temptSaveFile, saveAdd);
		AtFileFunction.delete(this.temptFolder);
	}
}
