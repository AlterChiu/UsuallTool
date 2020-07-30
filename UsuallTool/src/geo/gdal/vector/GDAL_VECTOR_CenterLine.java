package geo.gdal.vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.FileFunction;

public class GDAL_VECTOR_CenterLine {
	private String temptFolder = GdalGlobal.temptFolder + "CenterLine";
	private List<Geometry> geoList;
	private double verticeDensitive = 1.0;

	// only for polygon
	public GDAL_VECTOR_CenterLine(String polygonShp) {
		processing(new SpatialReader(polygonShp).getGeometryList());
	}

	public GDAL_VECTOR_CenterLine(List<Geometry> geoList) {
		processing(geoList);
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
		int reDensitiveVerPolygonsSize = reDensitiveVerPolygons.size();

		// processing to each geo
		for (int densitivePolygonIndex = 0; densitivePolygonIndex < reDensitiveVerPolygonsSize; densitivePolygonIndex++) {
			System.out.print("..." + densitivePolygonIndex * 100 / reDensitiveVerPolygonsSize);

			Geometry multiPolygon = reDensitiveVerPolygons.get(densitivePolygonIndex);
			GdalGlobal.MultiPolyToSingle(multiPolygon).forEach(polygon -> {

				// do voronoiPolygons
				GDAL_VECTOR_Voronoi voronoi = new GDAL_VECTOR_Voronoi(polygon);
				List<Geometry> voronoiPolygons = new ArrayList<>();
				try {
					voronoiPolygons = voronoi.getGeoList();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}

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
				selectLocation.addIntersectGeo(polygon);

				String temptCenterLineFileName = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
				String temptCenterLineFileAdd = this.temptFolder + "/" + temptCenterLineFileName;
				try {
					selectLocation.saveAsShp(temptCenterLineFileAdd);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			});
		}

		List<Geometry> outList = new ArrayList<>();
		for (String fileName : new File(this.temptFolder).list()) {
			if (fileName.contains(".shp")) {
				new SpatialReader(this.temptFolder + "/" + fileName).getGeometryList().forEach(geo -> outList.add(geo));
			}
		}
		new SpatialWriter().setGeoList(outList).saceAs(saveAdd, saveingType);
	}

	public List<Geometry> getGeoList() throws IOException, InterruptedException {
		String temptSaveFileName = GdalGlobal.newTempFileName(this.temptFolder, ".shp");
		String temptSaveFileAdd = this.temptFolder + "\\" + temptSaveFileName;
		this.saveAsShp(temptSaveFileAdd);

		return new SpatialReader(temptSaveFileAdd).getGeometryList();
	}

}
