package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class GDAL_VECTOR_SelectByLocation {
	private String temptFolder = GdalGlobal.temptFolder + "SelectByLocation";
	private String inputLayer = temptFolder + "\\temptShp.shp";
	private Set<String> selectType = new HashSet<>();
	/*
	 * 0 — intersect
	 * 
	 * 1 — contain
	 * 
	 * 2 — disjoint
	 * 
	 * 3 — equal
	 * 
	 * 4 — touch
	 * 
	 * 5 — overlap
	 * 
	 * 6 — are within
	 * 
	 * 7 — cross
	 */

	private List<Geometry> interSectGeoList = new ArrayList<>();

	public GDAL_VECTOR_SelectByLocation(String inputLayer) {
		processing(new SpatialReader(inputLayer).getGeometryList());
	}

	public GDAL_VECTOR_SelectByLocation(List<Geometry> geoList) {
		processing(geoList);
	}

	public GDAL_VECTOR_SelectByLocation(Geometry geo) {
		List<Geometry> geoList = new ArrayList<>();
		geoList.add(geo);
		processing(geoList);
	}

	private void processing(List<Geometry> geoList) {
		// clear temptFolder
		FileFunction.newFolder(this.temptFolder);
		for (String fileName : new File(this.temptFolder).list()) {
			FileFunction.delete(this.temptFolder + "\\" + fileName);
		}

		// translate shapeFile to points
		new SpatialWriter().setGeoList(geoList).saveAsShp(this.inputLayer);
	}

	public void addIntersectGeo(Geometry geo) {
		this.interSectGeoList.add(geo);
	}

	public void addIntersectGeo(List<Geometry> geoList) {
		geoList.forEach(geo -> this.interSectGeoList.add(geo));
	}

	public void addIntersectGeo(String shpFileAdd) {
		new SpatialReader(shpFileAdd).getGeometryList().forEach(geo -> this.interSectGeoList.add(geo));
	}

	public void getIntersect() {
		this.selectType.add("0");
	}

	public void getContain() {
		this.selectType.add("1");
	}

	public void getDisjoint() {
		this.selectType.add("2");
	}

	public void getEqual() {
		this.selectType.add("3");
	}

	public void getTouch() {
		this.selectType.add("4");
	}

	public void getOverlap() {
		this.selectType.add("5");
	}

	public void getWithin() {
		this.selectType.add("6");
	}

	public void getCross() {
		this.selectType.add("7");
	}

	public void saveAsShp(String saveAdd) throws IOException, InterruptedException {
		// check select algorithm
		if (this.selectType.size() == 0) {
			this.selectType.add("0");
		}

		// set intersect vector layer
		String intersectLayerFileName = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
		String intersectLayerFileAdd = this.temptFolder + "/" + intersectLayerFileName;
		new SpatialWriter().setGeoList(this.interSectGeoList).saveAsShp(intersectLayerFileAdd);

		List<String> batContent = new ArrayList<>();

		// initial GdalPython enviroment
		GdalGlobal.GDAL_EnviromentStarting().forEach(command -> batContent.add(command));
		batContent.add("\"%PYTHONHOME%\\python\" AtSelectByLocation.py");
		batContent.add("exit");
		new AtFileWriter(batContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtSelectByLocation.bat").textWriter("");

		// initial QgisAlogrithm pythonFile
		List<String> pythonContent = new ArrayList<>();
		GdalGlobal.QGIS_Processing_PythonInitialize().forEach(content -> pythonContent.add(content));

		// create voronoiPolygons parameter
		StringBuilder parameter = new StringBuilder();
		parameter.append("parameter = {");
		parameter.append("\"INPUT\":\"");
		parameter.append(this.inputLayer.replace("\\", "/") + "\",");

		parameter.append("\"PREDICATE\":[");
		parameter.append(String.join(",", this.selectType) + "],");

		parameter.append("\"INTERSECT\":\"");
		parameter.append(intersectLayerFileAdd.replace("\\", "/") + "\",");

		parameter.append("\"OUTPUT\":\"");
		parameter.append(saveAdd.replace("\\", "/") + "\"}");
		pythonContent.add(parameter.toString());

		// create pythonAlogrithm processing
		pythonContent.add("processing.run('native:extractbylocation',parameter)");
		new AtFileWriter(pythonContent.parallelStream().toArray(String[]::new),
				GdalGlobal.gdalBinFolder + "//AtSelectByLocation.py").textWriter("");

		// run batFile
		List<String> command = new ArrayList<>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/wait");
		command.add("/B");
		command.add("AtSelectByLocation.bat");

		// run command
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(new File(GdalGlobal.gdalBinFolder));
		pb.command(command);
		Process runProcess = pb.start();
		runProcess.waitFor();
	}

	public List<Geometry> getGeoList() throws IOException, InterruptedException {
		String temptSaveFileName = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
		String temptSaveFileAdd = this.temptFolder + "\\" + temptSaveFileName;
		this.saveAsShp(temptSaveFileAdd);

		return new SpatialReader(temptSaveFileAdd).getGeometryList();
	}

}
