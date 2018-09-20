package gdal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import usualTool.FileFunction;

public class GeoJsonToShp {
	private String cordinate = "WGS84";
	private String geoFileAdd = "";
	private String shpFileAdd = "";

	public GeoJsonToShp(String geoFileAdd, String shpFileAdd) {
		this.geoFileAdd = geoFileAdd;
		this.shpFileAdd = shpFileAdd;
	}

	public void Start() throws IOException {
		FileFunction ff = new FileFunction();
		String delFileName = this.shpFileAdd.substring(0, shpFileAdd.length() - 3);
		ff.delete(delFileName + "dbf");
		ff.delete(delFileName + "prj");
		ff.delete(delFileName + "shp");
		ff.delete(delFileName + "shx");

		List<String> command = new ArrayList<String>();
		command.add("cmd.exe");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("ogr2ogr");
		command.add("-f");
		command.add("ESRI Shapefile");
		command.add("-a_srs");
		command.add(this.cordinate);
		command.add(this.shpFileAdd);
		command.add(this.geoFileAdd);

		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File(GdalGlobal.gdalBinFolder));
		builder.command(command);
		Process process = builder.start();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GeoJsonToShp setCordinateToTWD121() {
		this.cordinate = "EPSG:3824";
		return this;
	}

	public GeoJsonToShp setCordinateToWGS84() {
		this.cordinate = "WGS84";
		return this;
	}

}