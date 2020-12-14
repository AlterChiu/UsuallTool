
package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import geo.gdal.GdalGlobal;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_RASTER_TranslateCoordinate {
	private String prefixName = "RasterTanslate";
	private String temptFolder = GdalGlobal.temptFolder;

	private String originalFile = "";
	private int inputCoordinate = 0;
	private double cellSize = -999;

	public GDAL_RASTER_TranslateCoordinate(String originalFile, int inputCoordinate) {
		this.inputCoordinate = inputCoordinate;
		this.originalFile = originalFile;
	}

	public void setReselution(double cellSize) {
		this.cellSize = cellSize;
	}

	public void save(String saveAdd, int outputCoordinate) throws IOException, InterruptedException {
		/*
		 * clear gdalGlobal temptFolder
		 */
		this.temptFolder = GdalGlobal.createTemptFolder(this.prefixName);

		/*
		 * save sourceFile to temptFolder
		 */
		String sourceFileExtension = this.originalFile.substring(this.originalFile.lastIndexOf("."));
		String saveFileExtension = saveAdd.substring(saveAdd.lastIndexOf("."));

		String sourceWorkSpace = this.temptFolder + GdalGlobal.getTempFileName(this.temptFolder, "") + "\\";
		String targetWorkSpace = this.temptFolder + GdalGlobal.getTempFileName(this.temptFolder, "") + "\\";

		FileFunction.newFolder(sourceWorkSpace);
		FileFunction.newFolder(targetWorkSpace);

		String temptSorceFile = sourceWorkSpace + GdalGlobal.getTempFileName(sourceWorkSpace, sourceFileExtension);
		FileFunction.copyFile(this.originalFile, temptSorceFile);

		String temptSaveFile = targetWorkSpace + GdalGlobal.getTempFileName(targetWorkSpace, saveFileExtension);

		/*
		 * setting translate .bat file
		 */
		List<String> batFile = new ArrayList<>();

		// setting gdal working enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(line -> batFile.add(line));

		// setting gdal_translate setting
		StringBuilder translateCommand = new StringBuilder();
		translateCommand.append("gdalwarp.exe -overwrite");

		// setting original file
		translateCommand.append(" \"" + temptSorceFile + "\"");

		// setting targetFile
		translateCommand.append(" \"" + temptSaveFile + "\"");

		// setting targeFormat
		translateCommand.append(" -of GTiff");

		// setting cellSize
		if (this.cellSize != -999) {
			translateCommand.append(" -tr " + this.cellSize + " " + this.cellSize);
		}

		// setting algrithon
		translateCommand.append(" -r average");

		// setting input coordinate
		if (this.inputCoordinate != 0) {
			translateCommand.append(" -s_srs EPSG:" + this.inputCoordinate);
		}

		// setting output coordinate
		if (outputCoordinate != 0) {
			translateCommand.append(" -t_srs EPSG:" + outputCoordinate);
		}

		batFile.add(translateCommand.toString());

		batFile.add("exit");
		new AtFileWriter(batFile.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//gdalwrap_tempt.bat").setEncoding(AtFileWriter.ANSI).textWriter("");

		/*
		 * run bat file
		 */
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/wait");
		runCommand.add("/B");
		runCommand.add(GdalGlobal.gdalBinFolder + "//gdalwrap_tempt.bat");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();

		FileFunction.copyFile(temptSaveFile, saveAdd);
		FileFunction.delete(this.temptFolder);
	}
}
