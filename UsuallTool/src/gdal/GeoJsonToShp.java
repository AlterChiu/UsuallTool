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
	
	public GeoJsonToShp setGeoJson(String geoFileAdd) {
		this.geoFileAdd = geoFileAdd;
		return this;
	}
	
	public GeoJsonToShp setShpFile(String shpFileAdd) {
		this.shpFileAdd = shpFileAdd;
		return this;
	}
	
	public void Start() throws IOException {
		FileFunction ff = new FileFunction();
		String delFileName = this.shpFileAdd.substring(0, shpFileAdd.length()-3);
		ff.delFile(delFileName + "dbf");
		ff.delFile(delFileName + "prj");
		ff.delFile(delFileName + "shp");
		ff.delFile(delFileName + "shx");
		
		
		
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
		builder.start();
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