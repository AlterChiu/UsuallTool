
package geo.gdal.raster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import geo.gdal.GdalGlobal;
import usualTool.AtFileFunction;
import usualTool.AtFileWriter;

public class Gdal_RasterToPNG {
	private static String prefixName = "RasterToPNG";
	private static String temptFolder;
	private static String sourceTemptAdd;
	private static String colorFileAdd;

	public static void save(String sourceAdd, Map<Double, Integer[]> colorScale, String saveAdd)
			throws IOException, InterruptedException {

		// initialize
		Gdal_RasterToPNG.initialize(sourceAdd, colorScale);

		// run batFile
		Gdal_RasterToPNG.runCommand(sourceTemptAdd, colorFileAdd, saveAdd);

		// clear temptFolder
		AtFileFunction.delete(temptFolder);
	}

	public static void save(String sourceAdd, Map<Double, Integer[]> colorScale, String saveAdd, int pixelWidth,
			int pixelHeight) throws Exception {

		// initialize
		Gdal_RasterToPNG.initialize(sourceAdd, colorScale);

		// reSample raster
		Gdal_RasterWarp warp = new Gdal_RasterWarp(sourceAdd);
		warp.reSample(pixelWidth, pixelHeight);
		warp.save(sourceTemptAdd);
		AtFileFunction.waitFile(sourceTemptAdd, 180000);

		// run command
		Gdal_RasterToPNG.runCommand(sourceTemptAdd, colorFileAdd, saveAdd);

		AtFileFunction.waitFile(sourceTemptAdd, 180000);
		AtFileFunction.delete(temptFolder);
	}

	// PRIVATE FUNCTION
	// <=========================================>
	private static void initialize(String sourceAdd, Map<Double, Integer[]> colorScale) throws IOException {
		// check file is exit
		if (!new File(sourceAdd).exists())
			throw new FileNotFoundException("file not found : " + sourceAdd);

		// create temptFolder
		Gdal_RasterToPNG.temptFolder = AtFileFunction.createTemptFolder(Gdal_RasterToPNG.prefixName);

		// get temptPaths
		Gdal_RasterToPNG.sourceTemptAdd = Gdal_RasterToPNG.copyFile(sourceAdd, temptFolder);
		Gdal_RasterToPNG.colorFileAdd = Gdal_RasterToPNG.outputColorFile(temptFolder, colorScale);
	}

	// create tempt folder
	private static String copyFile(String sourceAdd, String temptFolder) {

		// copy original file
		String sourceFileExtension = sourceAdd.substring(sourceAdd.lastIndexOf("."));
		String sourceTemptName = AtFileFunction.getTempFileName(temptFolder, sourceFileExtension);
		String sourceTemptAdd = temptFolder + "\\" + sourceTemptName;
		AtFileFunction.copyFile(sourceAdd, sourceTemptAdd);
		return sourceTemptAdd;
	}

	private static String outputColorFile(String temptFolder, Map<Double, Integer[]> colorScale) throws IOException {

		// setting color table
		String colorFileName = AtFileFunction.getTempFileName(temptFolder, ".txt");
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

	private static void runCommand(String sourceTemptAdd, String colorFileAdd, String saveAdd)
			throws IOException, InterruptedException {
		List<String> runCommand = new ArrayList<>();
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/w");
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
	}

	// STYLE
	// <======================================>
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

	public static Map<Double, Integer[]> CWB_RainfallScale() {
		Map<Double, Integer[]> outMap = new TreeMap<>();
		outMap.put(0.0, new Integer[] { 255, 255, 255, 0 });
		outMap.put(0.0001, new Integer[] { 202, 202, 202, 255 });
		outMap.put(1.5, new Integer[] { 157, 254, 255, 255 });
		outMap.put(4., new Integer[] { 0, 209, 253, 255 });
		outMap.put(8., new Integer[] { 0, 165, 254, 255 });
		outMap.put(12.5, new Integer[] { 1, 119, 253, 255 });
		outMap.put(17.5, new Integer[] { 39, 163, 27, 255 });
		outMap.put(25., new Integer[] { 1, 249, 48, 255 });
		outMap.put(35., new Integer[] { 254, 253, 50, 255 });
		outMap.put(45., new Integer[] { 255, 211, 40, 255 });
		outMap.put(60., new Integer[] { 255, 167, 31, 255 });
		outMap.put(80., new Integer[] { 254, 43, 6, 255 });
		outMap.put(100., new Integer[] { 217, 34, 4, 255 });
		outMap.put(120., new Integer[] { 170, 24, 1, 255 });
		outMap.put(140., new Integer[] { 170, 33, 163, 255 });
		outMap.put(175., new Integer[] { 218, 43, 208, 255 });
		outMap.put(250., new Integer[] { 255, 56, 252, 255 });
		outMap.put(300., new Integer[] { 255, 214, 254, 255 });

		return outMap;
	}

	public static Map<Double, Integer[]> WRA_FloodDepth() {
		Map<Double, Integer[]> outMap = new TreeMap<>();
		outMap.put(0.0, new Integer[] { 255, 255, 255, 0 });
		outMap.put(0.001, new Integer[] { 255, 255, 255, 255 });
		outMap.put(0.4, new Integer[] { 255, 212, 119, 255 });
		outMap.put(0.75, new Integer[] { 253, 184, 7, 255 });
		outMap.put(1.5, new Integer[] { 253, 99, 6, 255 });
		outMap.put(2.5, new Integer[] { 247, 2, 0, 255 });
		outMap.put(3.0, new Integer[] { 133, 0, 0, 255 });

		return outMap;
	}
}
