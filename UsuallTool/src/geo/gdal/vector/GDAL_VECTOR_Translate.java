package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal_DataFormat;
import usualTool.AtFileFunction;

public class GDAL_VECTOR_Translate {

	private int inputCoordinate = -1;
	private int outputCoordinate = -1;

	// output type default for <*.shp>
	private String outputDataType = GdalGlobal_DataFormat.DATAFORMAT_VECTOR_ESRIShapefile;

	private String inputFile = "";

	public GDAL_VECTOR_Translate(String inputFile) {
		this.inputFile = inputFile;
	}

	public GDAL_VECTOR_Translate(String inputFile, int inputCoordination) {
		this.inputFile = inputFile;
		this.inputCoordinate = inputCoordination;
	}

	public GDAL_VECTOR_Translate setInputCoordinate(int inputCoordination) {
		this.inputCoordinate = inputCoordination;
		return this;
	}

	public GDAL_VECTOR_Translate setOutputCoordinate(int outputCoordination) {
		this.outputCoordinate = outputCoordination;
		return this;
	}

	public GDAL_VECTOR_Translate setOutputDataType(String outputDataType) {
		this.outputDataType = outputDataType;
		return this;
	}

	public void save(String saveAdd, String outputDataType) throws IOException, InterruptedException {
		this.outputDataType = outputDataType;
		this.save(saveAdd);
	}

	public void save(String saveAdd, String outputDataType, int outputCoordination)
			throws IOException, InterruptedException {
		this.outputDataType = outputDataType;
		this.outputCoordinate = outputCoordination;
		this.save(saveAdd);
	}

	public void save(String saveAdd, int outputCoordination) throws IOException, InterruptedException {
		this.outputCoordinate = outputCoordination;
		this.save(saveAdd);
	}

	public void saveAsJson(String saveAdd) throws IOException, InterruptedException {
		this.outputDataType = GdalGlobal_DataFormat.DATAFORMAT_VECTOR_GeoJSON;
		this.save(saveAdd);
	}

	public void save(String saveAdd) throws IOException, InterruptedException {

		// check file same path
		if (Paths.get(this.inputFile).toAbsolutePath().toString()
				.equals(Paths.get(saveAdd).toAbsolutePath().toString())) {
			throw new IOException("ogr2ogr are not available for same file path conversion");

			// check target file is exist or not
		} else if (!new File(this.inputFile).exists()) {
			throw new IOException("source file wasn't fount, " + this.inputFile);

			// check file path is accessible
		} else if (!new File(saveAdd).exists()) {
			new File(saveAdd).createNewFile();
		}
		AtFileFunction.delete(saveAdd);

		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/B");
		command.add("ogr2ogr.exe");

		// setting input coordination
		if (this.inputCoordinate != -1) {
			command.add("-s_srs");
			command.add("EPSG:" + this.inputCoordinate);
		}

		// setting output coordination
		if (this.outputCoordinate != -1) {
			command.add("-t_srs");
			command.add("EPSG:" + this.outputCoordinate);
		}

		// setting output dataType
		command.add("-f");
		command.add("\"" + this.outputDataType + "\"");

		// setting output file add
		command.add("\"" + saveAdd + "\"");

		// setting input file add
		command.add("\"" + this.inputFile + "\"");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();

		AtFileFunction.waitFile(saveAdd, 180000);
	}

}
