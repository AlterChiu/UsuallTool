package gdal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import usualTool.FileFunction;

public class DBFToGeoJson {
	private String cordinate = "WGS84";
	private String geoFileAdd = "";
	private String dbfFileAdd = "";

	public DBFToGeoJson(String dbfFileAdd, String geoFileAdd) {
		this.geoFileAdd = geoFileAdd;
		this.dbfFileAdd = dbfFileAdd;
	}

	public void Start() throws IOException {
		new FileFunction().delete(this.geoFileAdd);

		List<String> command = new ArrayList<String>();
		command.add("cmd.exe");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("ogr2ogr");
		command.add("-f");
		command.add("GeoJSON");
		command.add("-s_srs");
		command.add(this.cordinate);
		command.add(this.geoFileAdd);
		command.add(this.dbfFileAdd);

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

	public DBFToGeoJson setCordinateToTWD121() {
		this.cordinate = "EPSG:3824";
		return this;
	}

	public DBFToGeoJson setCordinateToWGS84() {
		this.cordinate = "WGS84";
		return this;
	}
}