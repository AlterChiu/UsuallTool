package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.FileFunction;

public class GDAL_VECTOR_CenterLine {
	private String temptFolder = GdalGlobal.temptFolder + "CenterLine";
	private List<Geometry> geoList;
	private double verticeDensitive = 1.0;
	private List<Map<String, Object>> attrList = null;
	private Map<String, String> attrType = null;

	// only for polygon
	public GDAL_VECTOR_CenterLine(String polygonShp) {
		SpatialReader shpReader = new SpatialReader(polygonShp);
		this.attrList = shpReader.getAttributeTable();
		this.attrType = shpReader.getAttributeTitleType();
		processing(shpReader.getGeometryList());
	}

	public GDAL_VECTOR_CenterLine(List<Geometry> geoList) {
		processing(geoList);
	}

	public GDAL_VECTOR_CenterLine(Geometry geometry) {
		List<Geometry> temptList = new ArrayList<>();
		temptList.add(geometry);

		processing(temptList);
	}

	private void processing(List<Geometry> geoList) {
		// clear temptFolder
		FileFunction.newFolder(this.temptFolder);
		for (String fileName : new File(this.temptFolder).list()) {
			FileFunction.delete(this.temptFolder + "\\" + fileName);
		}

		this.geoList = geoList;
	}

	public void setVerticeDensitive(double densitive) {
		this.verticeDensitive = densitive;
	}

	public void saveAsGeoJson(String saveAdd) throws IOException, InterruptedException {
		save(saveAdd, "Geojson");
	}

	public void saveAsShp(String saveAdd) throws IOException, InterruptedException {
		save(saveAdd, "Esri Shapefile");
	}

	public void save(String saveAdd, String saveingType) throws IOException, InterruptedException {

		// get reDensitiveVertise polygons
		GDAL_VECTOR_Defensify defensify = new GDAL_VECTOR_Defensify(this.geoList);
		defensify.setInterval(this.verticeDensitive);
		List<Geometry> reDensitiveVerPolygons = defensify.getGeoList();

		// output shpFile
		SpatialWriter outputShp = new SpatialWriter();
		if (this.attrType != null) {
			outputShp.setFieldType(this.attrType);
		}

		// do processing for each polygon
		for (int index = 0; index < reDensitiveVerPolygons.size(); index++) {

			// skip exception
			try {
				Geometry reDensitiveVerPolygon = reDensitiveVerPolygons.get(index);

				// do voronoiPolygons
				GDAL_VECTOR_Voronoi voronoi = new GDAL_VECTOR_Voronoi(reDensitiveVerPolygon);
				List<Geometry> voronoiPolygons = new ArrayList<>();
				voronoiPolygons = voronoi.getGeoList();

				// get polyLine in polygon
				List<Geometry> voronoiPolyLineList = new ArrayList<>();
				for (Geometry voronoiPolygon : voronoiPolygons) {
					voronoiPolyLineList.add(voronoiPolygon.Boundary());
				}
				Geometry voronoiPolyLine = GdalGlobal.mergePolygons(voronoiPolyLineList);

				// select polyLine which within in the polygon
				GDAL_VECTOR_SelectByLocation selectLocation = new GDAL_VECTOR_SelectByLocation(
						GdalGlobal.MultiPolyToSingle(voronoiPolyLine));
				selectLocation.getWithin();
				selectLocation.addIntersectGeo(reDensitiveVerPolygon);

				// setting output geometry properties
				Geometry outputGeo = GdalGlobal.mergePolygons(selectLocation.getGeoList());
				Map<String, Object> temptProperties = null;
				if (this.attrList != null) {
					temptProperties = this.attrList.get(index);
				}
				outputShp.addFeature(outputGeo, temptProperties);

				// output exception
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		outputShp.saceAs(saveAdd, saveingType);
	}

	public List<Geometry> getGeoList() throws IOException, InterruptedException {
		this.saveAsShp(this.temptFolder + "\\temptSave.shp");
		return new SpatialReader(this.temptFolder + "\\temptSave.shp").getGeometryList();
	}

}
