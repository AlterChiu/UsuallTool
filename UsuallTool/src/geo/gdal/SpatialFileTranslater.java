package geo.gdal;

import java.io.File;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import usualTool.FileFunction;

public class SpatialFileTranslater {
	private DataSource dataSource;
	public static int WGS84 = 4326;
	public static int TWD97_121 = 3826;
	public static int TWD97_119 = 3825;
	public static int TWD67_121 = 3828;
	public static int TWD67_119 = 3827;

	private SpatialReference inputSpatital = new SpatialReference();
	private SpatialReference outputSpatital = new SpatialReference();

	/*
	 * 
	 * 
	 */
	// <===========================================>
	// <constructor>
	// <===========================================>
	public SpatialFileTranslater(String file) {
		gdal.AllRegister();
		// gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		// gdal.SetConfigOption("SHAPE_ENCODING", "UTF8");
		this.dataSource = ogr.Open(file);
		this.inputSpatital.ImportFromEPSG(WGS84);
		this.outputSpatital.ImportFromEPSG(WGS84);
	}

	public SpatialFileTranslater(String file, String encode) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_" + encode, "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", encode);
		this.dataSource = ogr.Open(file);
		this.inputSpatital.ImportFromEPSG(WGS84);
		this.outputSpatital.ImportFromEPSG(WGS84);
	}

	public SpatialFileTranslater(DataSource dataSource) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", "UTF8");
		this.dataSource = dataSource;
		this.inputSpatital.ImportFromEPSG(WGS84);
		this.outputSpatital.ImportFromEPSG(WGS84);
	}

	public SpatialFileTranslater(DataSource dataSource, String encode) {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_" + encode, "YES");
		gdal.SetConfigOption("SHAPE_ENCODING", encode);
		this.dataSource = dataSource;
		this.inputSpatital.ImportFromEPSG(WGS84);
		this.outputSpatital.ImportFromEPSG(WGS84);
	}
	// <================================================>

	/*
	 * 
	 * 
	 */
	// <=================================================>
	// <coordinate system>
	// <=================================================>
	public SpatialFileTranslater setOutputFileCoordinate(int coordinateSystem) {
		this.outputSpatital.ImportFromEPSG(coordinateSystem);
		return this;
	}

	public SpatialFileTranslater setInputFileCoordinate(int coordinateSystem) {
		this.inputSpatital.ImportFromEPSG(coordinateSystem);
		return this;
	}
	// <=================================================>

	/*
	 * 
	 * 
	 */
	// <=================================================>
	// <output function>
	// <==================================================>
	public void saveAsGeoJson(String saveAdd) {
		Driver dr = ogr.GetDriverByName("GeoJson");
		getTransformedDataSource(saveAdd, dr);
	}

	public void saveAsShp(String saveAdd) {
		Driver dr = ogr.GetDriverByName("Esri Shapefile");
		getTransformedDataSource(saveAdd, dr);
	}

	public void saveAsCsv(String saveAdd) {
		Driver dr = ogr.GetDriverByName("CSV");
		getTransformedDataSource(saveAdd, dr);
	}

	public void saveAsTopoJson(String saveAdd) {
		Driver dr = ogr.GetDriverByName("TopoJSON");
		getTransformedDataSource(saveAdd, dr);
	}

	public void saveAsCAD(String saveAdd) {
		Driver dr = ogr.GetDriverByName("CAD");
		getTransformedDataSource(saveAdd, dr);
	}

	public void saveAsDWG(String saveAdd) {
		Driver dr = ogr.GetDriverByName("DWG");
		getTransformedDataSource(saveAdd, dr);
	}

	public void saveAsKML(String saveAdd) {
		Driver dr = ogr.GetDriverByName("KML");
		getTransformedDataSource(saveAdd, dr);
	}

	private void getTransformedDataSource(String saveAdd, Driver dataSourceDriver) {
		// get input layer
		DataSource temptDataSource = this.dataSource;
		Layer inputLayer = temptDataSource.GetLayer(0);

		// setting for coordinate system
		CoordinateTransformation geoTrans = new CoordinateTransformation(this.inputSpatital, this.outputSpatital);

		// create output layer
		if (new File(saveAdd).exists()) {
			new FileFunction().delete(saveAdd);
		}
		
		DataSource outDataSource = dataSourceDriver.CreateDataSource(saveAdd);
		Layer outLayer = outDataSource.CreateLayer(inputLayer.GetName(), inputLayer.GetSpatialRef());

		// create field
		FeatureDefn inputField = inputLayer.GetLayerDefn();
		for (int index = 0; index < inputField.GetFieldCount(); index++) {
			outLayer.CreateField(inputField.GetFieldDefn(index));
		}

		// input feature to field
		for (int index = 0; index < inputLayer.GetFeatureCount(); index++) {
			Feature feature = inputLayer.GetFeature(index);
			Feature outFeature = new Feature(outLayer.GetLayerDefn());
			for (int field = 0; field < feature.GetFieldCount(); field++) {
				outFeature.SetField(inputField.GetFieldDefn(field).GetName(), feature.GetFieldAsString(field));
			}
			// geometry translate
			Geometry geometry = feature.GetGeometryRef();
			geometry.Transform(geoTrans);
			outFeature.SetGeometry(ogr.CreateGeometryFromWkt(geometry.ExportToWkt()));

			// add a new feature to field
			outLayer.CreateFeature(outFeature);
		}
		outDataSource.delete();
	}
	// <=======================================================>
}
