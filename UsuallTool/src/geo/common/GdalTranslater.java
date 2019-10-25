package geo.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;

public class GdalTranslater {
	public static String ascii = "AAIGrid";
	public static String geoTiff = "GTiff";
	public static String arcGrid = "AIG";

	public static void RasterTranslater(String input, String output, String outputFormat)
			throws IOException, InterruptedException {

		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("gdal_translate");
		command.add("-of");
		command.add(outputFormat);
		command.add(input);
		command.add(output);

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);

		Process pr = pb.start();
		pr.waitFor();
	}
}
