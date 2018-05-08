package gdal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonToShp {
	private String cordinate = "crs84";
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
		List<String> command = new ArrayList<String>();
		command.add("ogr2ogr");
		command.add("-f");
		command.add("GeoJSON");
		command.add("-t_srs");
		command.add(this.cordinate);
		command.add(this.shpFileAdd);
		command.add(this.geoFileAdd);
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File(GdalGlobal.gdalBinFolder));
		builder.command(command);
		builder.start();
	}
	
	
	
	
	
	public GeoJsonToShp setCordinateToTWD121() {
		this.cordinate = "EPSG:3826";
		return this;
	}
	
	public GeoJsonToShp setCordinateToWGS84() {
		this.cordinate = "crs84";
		return this;
	}
	
}
