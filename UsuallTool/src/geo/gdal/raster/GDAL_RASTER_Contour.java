package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;
import usualTool.AtCommonMath;

public class GDAL_RASTER_Contour {

	public static void Create(double minValue, double interval, String attrTitle, String sourceFile,
			String targetFile) throws IOException, InterruptedException {
		String targetFormat = GdalGlobal.extensionAutoDetect(targetFile);

		List<String> command = new ArrayList<>();
		// cmdProcessing
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/w");
		command.add("/B");

		command.add("gdal_contour");

		// default band in raster file
		command.add("-b");
		command.add("1");

		// attrTitle
		command.add("-a");
		command.add(attrTitle);

		// interval
		command.add("-i");
		command.add(AtCommonMath.getDecimal_String(interval, 3));

		// min-value
		command.add("-off");
		command.add(AtCommonMath.getDecimal_String(minValue, 3));

		// output type
		command.add("-f");
		command.add(targetFormat);

		// sourceFile
		command.add("\"" + sourceFile + "\"");

		// targetFile
		command.add("\"" + targetFile + "\"");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();
	}
}
