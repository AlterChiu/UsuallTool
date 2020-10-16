
package geo.gdal.raster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import geo.gdal.GdalGlobal;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_RASTER_ToPNG {

	public static void save(String sourceAdd, Map<Double, Integer[]> colorScale, String saveAdd)
			throws IOException, InterruptedException {
		String temptFolder = GdalGlobal.temptFolder + "RasterToPNG";
		temptFolder = temptFolder + "-" + GdalGlobal.getTempFileName(GdalGlobal.temptFolder, "");

		String sourceTemptAdd = GDAL_RASTER_ToPNG.copyFile(sourceAdd, temptFolder);
		String colorFileAdd = GDAL_RASTER_ToPNG.outputColorFile(temptFolder, colorScale);

		// run command
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/wait");
		runCommand.add("/B");
		runCommand.add("gdaldem");
		runCommand.add("color-relief");
		runCommand.add("-of");
		runCommand.add("PNG");
		runCommand.add("-alpha");
		runCommand.add("-nearest_color_entry");

		runCommand.add("\"" + sourceTemptAdd + "\"");
		runCommand.add("\"" + colorFileAdd + "\"");
		runCommand.add("\"" + saveAdd + "\"");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();

		FileFunction.delete(temptFolder);
	}

	public static void save(String sourceAdd, Map<Double, Integer[]> colorScale, String saveAdd, int pixelWidth,
			int pixelHeight) throws IOException, InterruptedException {

		String temptFolder = GdalGlobal.temptFolder + "RasterToPNG";
		temptFolder = temptFolder + "-" + GdalGlobal.getTempFileName(GdalGlobal.temptFolder, "");

		// clear folder
		FileFunction.newFolder(temptFolder);
		for (String fileName : new File(temptFolder).list()) {
			FileFunction.delete(temptFolder + "\\" + fileName);
		}

		// warp
		String sourceTemptAdd = temptFolder + "\\" + GdalGlobal.getTempFileName(temptFolder, ".tif");
		String colorFileAdd = GDAL_RASTER_ToPNG.outputColorFile(temptFolder, colorScale);

		// reSample raster
		GDAL_RASTER_Warp warp = new GDAL_RASTER_Warp(sourceAdd);
		warp.reSample(pixelWidth, pixelHeight);
		warp.save(sourceTemptAdd);
		Thread.sleep(1000);


		// run command
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/wait");
		runCommand.add("/B");
		runCommand.add("gdaldem");
		runCommand.add("color-relief");
		runCommand.add("-of");
		runCommand.add("PNG");
		runCommand.add("-alpha");
		runCommand.add("-nearest_color_entry");

		runCommand.add("\"" + sourceTemptAdd + "\"");
		runCommand.add("\"" + colorFileAdd + "\"");
		runCommand.add("\"" + saveAdd + "\"");

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();

		System.out.println(sourceTemptAdd);
		System.out.println(colorFileAdd);
		System.out.println(saveAdd);

		FileFunction.delete(temptFolder);
	}

	private static String copyFile(String sourceAdd, String temptFolder) {
		// clear temptFolder

		FileFunction.newFolder(temptFolder);
		for (String fileName : new File(temptFolder).list()) {
			FileFunction.delete(temptFolder + "\\" + fileName);
		}

		// copy original file
		String sourceFileExtension = sourceAdd.substring(sourceAdd.lastIndexOf("."));
		String sourceTemptName = GdalGlobal.getTempFileName(temptFolder, sourceFileExtension);
		String sourceTemptAdd = temptFolder + "\\" + sourceTemptName;
		FileFunction.copyFile(sourceAdd, sourceTemptAdd);
		return sourceTemptAdd;
	}

	private static String outputColorFile(String temptFolder, Map<Double, Integer[]> colorScale) throws IOException {

		// setting color table
		String colorFileName = GdalGlobal.getTempFileName(temptFolder, ".txt");
		String colorFileAdd = temptFolder + "\\" + colorFileName;
		List<String[]> colorContext = new ArrayList<>();
		colorScale.keySet().forEach(key -> {
			Integer[] rgba = colorScale.get(key);

			List<String> temptLine = new ArrayList<>();
			temptLine.add(String.valueOf(key));
			temptLine.add(String.valueOf(rgba[0]));
			temptLine.add(String.valueOf(rgba[1]));
			temptLine.add(String.valueOf(rgba[2]));
			try {
				temptLine.add(String.valueOf(rgba[3]));
			} catch (Exception e) {
				temptLine.add("255");
			}
			colorContext.add(temptLine.parallelStream().toArray(String[]::new));
		});
		new AtFileWriter(colorContext.parallelStream().toArray(String[][]::new), colorFileAdd).textWriter(" ");
		return colorFileAdd;
	}



	public static Map<Double, Integer[]> FEWS_RainfallScale() {
		Map<Double, Integer[]> outMap = new TreeMap<>();
		outMap.put(0.0, new Integer[] { 255, 255, 255, 0 });
		outMap.put(0.001, new Integer[] { 255, 255, 255, 76 });
		outMap.put(1., new Integer[] { 219, 219, 219, 125 });
		outMap.put(2., new Integer[] { 175, 238, 238, 125 });
		outMap.put(6., new Integer[] { 0, 191, 255, 125 });
		outMap.put(10., new Integer[] { 79, 148, 205, 125 });
		outMap.put(15., new Integer[] { 0, 0, 255, 125 });
		outMap.put(20., new Integer[] { 50, 205, 50, 125 });
		outMap.put(30., new Integer[] { 124, 252, 0, 125 });
		outMap.put(40., new Integer[] { 255, 255, 0, 125 });
		outMap.put(50., new Integer[] { 255, 236, 139, 125 });
		outMap.put(70., new Integer[] { 255, 165, 79, 125 });
		outMap.put(90., new Integer[] { 255, 0, 0, 125 });
		outMap.put(110., new Integer[] { 205, 38, 38, 125 });
		outMap.put(130., new Integer[] { 205, 0, 205, 125 });
		outMap.put(150., new Integer[] { 205, 41, 144, 125 });
		outMap.put(200., new Integer[] { 255, 0, 255, 125 });
		outMap.put(300., new Integer[] { 255, 240, 245, 125 });

		return outMap;
	}

	public static Map<Double, Integer[]> FEWS_FloodDepth() {
		Map<Double, Integer[]> outMap = new TreeMap<>();
		outMap.put(0., new Integer[] { 255, 255, 255, 0 });
		outMap.put(0.001, new Integer[] { 173, 216, 230, 125 });
		outMap.put(0.5, new Integer[] { 255, 228, 181, 125 });
		outMap.put(1., new Integer[] { 255, 185, 15, 125 });
		outMap.put(1.5, new Integer[] { 255, 165, 0, 125 });
		outMap.put(2.0, new Integer[] { 255, 0, 0, 125 });
		outMap.put(2.5, new Integer[] { 205, 41, 144, 125 });
		outMap.put(3.0, new Integer[] { 255, 0, 255, 125 });

		return outMap;
	}
}
