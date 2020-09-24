
package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal_DataFormat;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_RASTER_Merge {
	private String temptFolder = GdalGlobal.temptFolder + "RasterMerge\\";
	private static String tmeptRunFileName = "gdal_merge_tempt.bat";

	/*
	 * variable type
	 */
	public static String VARIABLETYPE_BYTE = "Byte";
	public static String VARIABLETYPE_Int16 = "Int16";
	public static String VARIABLETYPE_UInt32 = "UInt32";
	public static String VARIABLETYPE_Int32 = "Int32";
	public static String VARIABLETYPE_Float32 = "Float32";
	public static String VARIABLETYPE_Float64 = "Float64";
	public static String VARIABLETYPE_CInt16 = "CInt16";
	public static String VARIABLETYPE_CInt32 = "CInt32";
	public static String VARIABLETYPE_CFloat32 = "CFloat32";
	public static String VARIABLETYPE_CFloat64 = "CFloat64";

	private List<String> batContent = new ArrayList<>();
	private List<String> mergeFiles = new ArrayList<>();

	private String noDataValue = "-999";

	public void addRaster(String fileAdd) {
		this.mergeFiles.add(fileAdd);
	}

	public void setNoDataValue(String nullValue) {
		this.noDataValue = nullValue;
	}

	public void save(String saveAdd) throws IOException, InterruptedException {
		save(saveAdd, VARIABLETYPE_Float32);
	}

	public void save(String saveAdd, String variableType)

			throws IOException, InterruptedException {
		/*
		 * clear gdalGlobal temptFolder
		 */
		this.temptFolder = this.temptFolder + "-" + GdalGlobal.getTempFileName(GdalGlobal.temptFolder, "");
		FileFunction.newFolder(this.temptFolder);
		for (String fileName : new File(this.temptFolder).list()) {
			FileFunction.delete(this.temptFolder + "\\" + fileName);
		}

		/*
		 * setting temptFile fileName
		 */
		String temptFileName = GdalGlobal.getTempFileName(this.temptFolder, ".txt");
		String temptFileDirection = this.temptFolder + "\\" + temptFileName;

		/*
		 * copy file to temptFolder
		 */
		List<String> mergeList = new ArrayList<>();
		for (String temptFile : this.mergeFiles) {
			String sourceFileExtension = temptFile.substring(temptFile.lastIndexOf("."));
			String sourceFileName = GdalGlobal.getTempFileName(this.temptFolder, sourceFileExtension);
			String sourceFileAdd = this.temptFolder + "\\" + sourceFileName;

			FileFunction.copyFile(temptFile, sourceFileAdd);
			mergeList.add("\"" + sourceFileAdd + "\"");
		}

		/*
		 * merged file SavedAdd
		 */
		String targetFileExtension = saveAdd.substring(saveAdd.lastIndexOf("."));
		String targetFileName = GdalGlobal.getTempFileName(this.temptFolder, targetFileExtension);
		String targetFileAdd = this.temptFolder + "\\" + targetFileName;

		/*
		 * setting inputFile
		 */
		new AtFileWriter(mergeList.parallelStream().toArray(String[]::new), temptFileDirection).textWriter("");

		/*
		 * setting raster merge .bat file
		 */
		GdalGlobal.GDAL_EnviromentStarting().forEach(line -> this.batContent.add(line));
		StringBuilder mergeCommand = new StringBuilder();
		mergeCommand.append("\"%GRASS_PYTHON%\" gdal_merge.py");
		mergeCommand.append(" -a_nodata  " + this.noDataValue);
		mergeCommand.append(" -ot " + VARIABLETYPE_Float32);
		mergeCommand.append(" -of " + GdalGlobal_DataFormat.DATAFORMAT_RASTER_GTiff);
		mergeCommand.append(" -o " + targetFileAdd);
		mergeCommand.append(" --optfile " + temptFileDirection);

		this.batContent.add(mergeCommand.toString());
		this.batContent.add("exit");
		/*
		 * save .bat file to gdalBin folder
		 */
		new AtFileWriter(this.batContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + tmeptRunFileName).setEncoding(AtFileWriter.ANSI).textWriter("");

		/*
		 * run bat file
		 */
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/wait");
		runCommand.add("/B");
		runCommand.add(GdalGlobal.gdalBinFolder + tmeptRunFileName);

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();

		/*
		 * copy merged file to saveAdd
		 */

		FileFunction.copyFile(targetFileAdd, saveAdd);
		FileFunction.delete(this.temptFolder);
	}
}
