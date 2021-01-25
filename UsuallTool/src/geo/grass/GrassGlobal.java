package geo.grass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geo.gdal.GdalGlobal;
import usualTool.AtFileFunction;
import usualTool.AtFileWriter;

public class GrassGlobal {

	public static void initializeGrassWorkSpace(String folder) throws IOException {

		/*
		 * create folder
		 */
		String gisBaseFolder = folder + "\\grassdata";
		String location = gisBaseFolder + "\\temp_location";
		String mapSet = location + "\\PERMANENT";

		if (new File(gisBaseFolder).exists()) {
			AtFileFunction.delete(gisBaseFolder);
		}
		AtFileFunction.newFolder(gisBaseFolder);

		if (new File(location).exists()) {
			AtFileFunction.delete(location);
		}
		AtFileFunction.newFolder(location);

		if (new File(mapSet).exists()) {
			AtFileFunction.delete(mapSet);
		}
		AtFileFunction.newFolder(mapSet);

		/*
		 * setting new setting to mapSet
		 */

		// project info
		List<String> proj_info = new ArrayList<>();
		proj_info.add("name: WGS 84");
		proj_info.add("datum: wgs84");
		proj_info.add("ellps: wgs84");
		proj_info.add("proj: ll");
		proj_info.add("no_defs: defined");
		proj_info.add("towgs84: 0.000,0.000,0.000");
		new AtFileWriter(proj_info.parallelStream().toArray(String[]::new), mapSet + "\\PROJ_INFO").textWriter("");

		// project unit
		List<String> proj_unit = new ArrayList<>();
		proj_unit.add("unit: degree");
		proj_unit.add("units: degrees");
		proj_unit.add("meters: 1.0");
		new AtFileWriter(proj_unit.parallelStream().toArray(String[]::new), mapSet + "\\PROJ_UNITS").textWriter("");

		// default wind
		List<String> default_wind = new ArrayList<>();
		default_wind.add("proj:       3");
		default_wind.add("zone:       0");
		default_wind.add("north:      1N");
		default_wind.add("south:      0");
		default_wind.add("east:       1E");
		default_wind.add("west:       0");
		default_wind.add("cols:       1");
		default_wind.add("rows:       1");
		default_wind.add("e-w resol:  1");
		default_wind.add("n-s resol:  1");
		default_wind.add("top:        1.000000000000000");
		default_wind.add("bottom:     0.000000000000000");
		default_wind.add("cols3:      1");
		default_wind.add("rows3:      1");
		default_wind.add("depths:     1");
		default_wind.add("e-w resol3: 1");
		default_wind.add("n-s resol3: 1");
		default_wind.add("t-b resol:  1");
		new AtFileWriter(default_wind.parallelStream().toArray(String[]::new), mapSet + "\\DEFAULT_WIND")
				.textWriter("");
		new AtFileWriter(default_wind.parallelStream().toArray(String[]::new), mapSet + "\\WIND").textWriter("");

		// my name
		List<String> myName = new ArrayList<>();
		myName.add("");
		new AtFileWriter(myName.parallelStream().toArray(String[]::new), mapSet + "\\MYNAME").textWriter("");

		// project epsg
		List<String> project_epsg = new ArrayList<>();
		project_epsg.add("epsg: 4326");
		new AtFileWriter(project_epsg.parallelStream().toArray(String[]::new), mapSet + "\\POJECT_EPSG").textWriter("");
	}

	public static List<String> initializeCommandLine() {
		List<String> command = new ArrayList<>();

		// setting qgisRoot to path
		command.add("set QgisRootFolder=" + GdalGlobal.qgisBinFolder);

		// setting Qgis and Grass enviroment
		command.add("call \"%QgisRootFolder%\\bin\\o4w_env.bat\"");
		command.add("call \"%QgisRootFolder%\\apps\\grass\\grass-7.6.0\\etc\\env.bat\"");

		// setting grass processing file
		command.add("set GISRC=%QgisRootFolder%\\processing.gisrc7 ");
		command.add("set WINGISBASE=%QgisRootFolder%\\apps\\grass\\grass-7.6.0 ");
		command.add("set GISBASE=%QgisRootFolder%\\apps\\grass\\grass-7.6.0");

		// setting projection folder
		command.add("set GRASS_PROJSHARE=%QgisRootFolder%\\share\\proj ");
		command.add("set GRASS_MESSAGE_FORMAT=plain");

		// setting running enviroment
		command.add(
				"set PATH=%path%;%QgisRootFolder%\\apps\\grass\\grass-7.6.0\\bin;%QgisRootFolder%\\apps\\grass\\grass-7.6.0\\lib;%QgisRootFolder%\\apps\\Python27\\lib\\site-packages\\Shapely-1.2.18-py2.7-win-amd64.egg\\shapely\\DLLs;%QgisRootFolder%\\apps\\Python27\\DLLs;%QgisRootFolder%\\apps\\qgis-ltr\\bin;%QgisRootFolder%\\apps\\Python27\\Scripts;%QgisRootFolder%\\bin;%QgisRootFolder%\\apps\\Python27\\lib\\site-packages\\numpy\\.libs");
		command.add("set PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC;.PY");
		command.add(
				"set PYTHONPATH=;%QgisRootFolder%\\apps\\grass\\grass-7.6.0\\etc\\python;%QgisRootFolder%\\apps\\grass\\grass-7.6.0\\etc\\wxpython\\n");

		// setting g.gisenv.exe
		command.add("g.gisenv.exe set=\"MAPSET=PERMANENT\"");
		command.add("g.gisenv.exe set=\"LOCATION=temp_location\" ");
		command.add("g.gisenv.exe set=\"LOCATION_NAME=temp_location\" ");
		command.add("g.gisenv.exe set=\"GISDBASE=" + AtFileFunction.createTemptFolder() + "\\grassdata\"");
		command.add("g.gisenv.exe set=\"GRASS_GUI=text\"");

		return command;
	}

	public void runGrass(List<String> commands) throws IOException, InterruptedException {
		List<String> runCommand = new ArrayList<>();

		// initialize enviroment
		String temptFolder = AtFileFunction.createTemptFolder();
		GrassGlobal.initializeGrassWorkSpace(temptFolder);

		// start cmd
		runCommand.add("cmd");
		runCommand.add("/c");
		runCommand.add("start");
		runCommand.add("/B");

		// set grass enviroment
		initializeCommandLine().forEach(command -> runCommand.add(command));

		// set user commands
		commands.forEach(command -> runCommand.add(command));

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(runCommand);
		Process runProcess = pb.start();
		runProcess.waitFor();
	}
}
