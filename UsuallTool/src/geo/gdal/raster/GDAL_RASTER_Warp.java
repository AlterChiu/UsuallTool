
package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import geo.gdal.GdalGlobal;
import usualTool.FileFunction;

public class GDAL_RASTER_Warp {
	private String originalFileAdd;

	// coordinate transalte
	private List<String> coordinateTranslate = new ArrayList<>();

	// crop
	private List<String> crop = new ArrayList<>();

	// resample
	private List<String> reSameple = new ArrayList<>();

	// type
	private List<String> dataType = new ArrayList<>();

	public GDAL_RASTER_Warp(String fileAdd) {
		this.originalFileAdd = fileAdd;
	}

	public void setCoordinate(int inputEPSG, int outputEPSG) {
		List<String> command = new ArrayList<>();
		command.add("-s_srs");
		command.add(" ");
		command.add("EPSG:" + inputEPSG);
		command.add(" ");
		command.add("-t_srs");
		command.add(" ");
		command.add("EPSG:" + outputEPSG);
		this.coordinateTranslate = command;
	}

	public void setCrop(double maxX, double maxY, double minX, double minY, int sourceEPSG) {

		// determine coordination
		if (this.coordinateTranslate.size() == 0) {
			List<String> temptList = new ArrayList<>();
			temptList.add("-s_srs");
			temptList.add("EGSP:" + sourceEPSG);
			this.coordinateTranslate = temptList;
		}

		// set crop
		List<String> command = new ArrayList<>();
		command.add("-te");
		command.add(minX + "");
		command.add(minY + "");
		command.add(maxX + "");
		command.add(maxY + "");
		this.crop = command;
	}

	public void setCrop(String geometryFile, int sourceEPSG) {

		// determine coordination
		if (this.coordinateTranslate.size() == 0) {
			List<String> temptList = new ArrayList<>();
			temptList.add("-s_srs");
			temptList.add("EGSP:" + sourceEPSG);
			this.coordinateTranslate = temptList;
		}

		// set crop
		List<String> command = new ArrayList<>();
		command.add("-cuptline");
		command.add("\"" + geometryFile + "\"");
		command.add("-crop_to_cutline");
		this.crop = command;
	}

	public void reSample(double xResolution, double yResolution, int sourceEPSG) {
		// determine coordination
		if (this.coordinateTranslate.size() == 0) {
			List<String> temptList = new ArrayList<>();
			temptList.add("-s_srs");
			temptList.add("EGSP:" + sourceEPSG);
			this.coordinateTranslate = temptList;
		}

		List<String> command = new ArrayList<>();
		command.add("-tr");
		command.add(xResolution + "");
		command.add(yResolution + "");
		this.reSameple = command;
	}

	public void reSample(double outputWidth, double outputHeight) {
		List<String> command = new ArrayList<>();
		command.add("-ts");
		command.add(outputWidth + "");
		command.add(outputHeight + "");
		this.reSameple = command;
	}

	public void save(String fileAdd) throws InterruptedException, IOException {
		String extention = FilenameUtils.getExtension(fileAdd);
		if (!extention.toUpperCase().contains("TIF")) {
			fileAdd += "tif";
		}

		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/b");
		command.add("gdalwarp");
		command.add("-overwrite");

		this.coordinateTranslate.forEach(e -> command.add(e));

		this.crop.forEach(e -> command.add(e));

		this.dataType.forEach(e -> command.add(e));

		this.reSameple.forEach(e -> command.add(e));

		command.add("\"" + this.originalFileAdd + "\"");
		command.add("\"" + fileAdd + "\"");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();

		FileFunction.waitFile(fileAdd, 180000);
	}

}
