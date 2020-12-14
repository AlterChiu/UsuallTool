
package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gdal.ogr.Geometry;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.FileFunction;

public class GDAL_VECTOR_CenterLine {
	private String prefixName = "CenterLine";
	private String temptFolder = GdalGlobal.temptFolder;

	private List<Geometry> geoList;
	private double verticeDensitive = 1.0;
	private List<Map<String, Object>> attrList = null;
	private Map<String, String> attrType = null;

	// only for polygon
	public GDAL_VECTOR_CenterLine(String polygonShp) throws UnsupportedEncodingException {
		SpatialReader shpReader = new SpatialReader(polygonShp);
		this.attrList = shpReader.getAttributeTable();
		this.attrType = shpReader.getAttributeTitleType();
		this.geoList = shpReader.getGeometryList();
	}

	public GDAL_VECTOR_CenterLine(List<Geometry> geoList) {
		this.geoList = geoList;
	}

	public GDAL_VECTOR_CenterLine(Geometry geometry) {
		List<Geometry> temptList = new ArrayList<>();
		temptList.add(geometry);
		this.geoList = temptList;
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
		this.temptFolder = GdalGlobal.createTemptFolder(this.prefixName);

		String temptSaveName = GdalGlobal.getTempFileName(this.temptFolder, ".shp");
		this.saveAsShp(this.temptFolder + temptSaveName);
		List<Geometry> outGeoList = new SpatialReader(GdalGlobal.temptFolder + temptSaveName).getGeometryList();
		this.close();
		return outGeoList;
	}

	private final void close() {
		FileFunction.delete(this.temptFolder);
	}

}
