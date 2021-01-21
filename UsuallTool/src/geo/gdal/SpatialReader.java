
package geo.gdal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

import geo.gdal.vector.GDAL_VECTOR_Translate;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class SpatialReader {
	private DataSource dataSource;
	private List<String> attributeTitles = new ArrayList<String>();
	private List<Geometry> geometryList = new ArrayList<Geometry>();
	private List<Map<String, Object>> featureTable = new ArrayList<>();
	private Map<String, String> attributeTitleType = new LinkedHashMap<>();
	private int EPSG = 4326;

	// <=========================================>
	// <constructor>
	// <=========================================>
	public SpatialReader(String fileAdd) throws UnsupportedEncodingException {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");

		this.dataSource = ogr.Open(fileAdd);
		detectAttributeTitle();
		detectAttributeTable();
		this.close();
	}

	public SpatialReader(String fileAdd, String encode)
			throws FileNotFoundException, IOException, InterruptedException {

		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");

		this.encodingTranslte(fileAdd, encode);
	}

	public SpatialReader(DataSource dataSource) throws UnsupportedEncodingException {
		gdal.AllRegister();
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");

		this.dataSource = dataSource;
		detectAttributeTitle();
		detectAttributeTable();
		this.close();
	}

	// <===========================================>

	/*
	 * return function
	 * 
	 */
	// <============================================>
	public List<String> getAttrubuteTitle() {
		return this.attributeTitles;
	}

	public List<Map<String, Object>> getAttributeTable() {
		return this.featureTable;
	}

	public List<Geometry> getGeometryList() {
		return this.geometryList;
	}

	public DataSource getSpatitalDataSource() {
		return this.dataSource;
	}

	public Map<String, String> getAttributeTitleType() {
		return this.attributeTitleType;
	}

	public Map<String, List<Geometry>> getGeoListMap(String columnID) {
		if (!this.attributeTitleType.containsKey(columnID)) {
			new Exception("No such column ID in this spatialFile");
			return null;
		}

		Map<String, List<Geometry>> outMap = new LinkedHashMap<>();
		for (int index = 0; index < this.geometryList.size(); index++) {
			String attrValue = String.valueOf(this.featureTable.get(index).get(columnID));

			List<Geometry> temptGeoList = Optional.ofNullable(outMap.get(attrValue)).orElse(new ArrayList<>());
			temptGeoList.add(this.geometryList.get(index));
			outMap.put(attrValue, temptGeoList);
		}

		return outMap;
	}

	public void reNameFeild(String oldFieldName, String newFeildName) {

		// change type
		if (this.attributeTitleType.containsKey(oldFieldName)) {
			this.attributeTitleType.put(newFeildName, this.attributeTitleType.get(oldFieldName));
		}

		// change attrTables
		this.featureTable.forEach(feature -> {
			feature.put(newFeildName, feature.get(oldFieldName));
			feature.remove(oldFieldName);
		});
	}

	public int getEPSG() {
		return this.EPSG;
	}
	// <============================================>

	/*
	 * translate function
	 */
	public void saveAsGeoJson(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsGeoJson(saveAdd);
	}

	public void saveAsShp(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsShp(saveAdd);
	}

	public void saveAsCsv(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsCsv(saveAdd);
	}

	public void saveAsTopoJson(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsTopoJson(saveAdd);
	}

	public void saveAsCAD(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsCAD(saveAdd);
	}

	public void saveAsDWG(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsDWG(saveAdd);
	}

	public void saveAsKML(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = getSpatialWriter(saveAdd, importCoordinate, outputCoordinate);
		spWriter.saveAsKML(saveAdd);
	}

	private SpatialWriter getSpatialWriter(String saveAdd, int importCoordinate, int outputCoordinate) {
		SpatialWriter spWriter = new SpatialWriter();
		spWriter.setFieldType(this.attributeTitleType);

		List<Geometry> temptGeoList = new ArrayList<>();
		this.geometryList.forEach(geo -> {
			temptGeoList.add(GdalGlobal.GeometryTranslator(geo, importCoordinate, outputCoordinate));
		});
		spWriter.setGeoList(temptGeoList);

		List<Map<String, Object>> temptList = new ArrayList<>();
		this.featureTable.forEach(feature -> {
			Map<String, Object> temptMap = new TreeMap<>();
			for (String key : feature.keySet()) {
				temptMap.put(key, feature.get(key));
			}
			temptList.add(temptMap);
		});
		spWriter.setAttribute(temptList);
		spWriter.setCoordinateSystem(outputCoordinate);
		return spWriter;
	}

	/*
	 * 
	 * function for private
	 */
	// <===========================================>
	// <get the name of attribute titles>
	private void detectAttributeTitle() {
		FeatureDefn layerDefn = this.dataSource.GetLayer(0).GetLayerDefn();
		for (int index = 0; index < layerDefn.GetFieldCount(); index++) {
			String titleName = layerDefn.GetFieldDefn(index).GetName();

			// get title name
			attributeTitles.add(titleName);

			// get title style
			attributeTitleType.put(titleName, layerDefn.GetFieldDefn(index).GetTypeName().toUpperCase());
		}
	}

	private void detectAttributeTable() throws UnsupportedEncodingException {
		Layer layer = this.dataSource.GetLayer(0);
		try {
			this.EPSG = layer.GetSpatialRef().AutoIdentifyEPSG();
		} catch (Exception e) {
			new Exception("*WARN* error while detect EPSG ");
		}

		for (int index = 0; index < layer.GetFeatureCount(); index++) {
			Feature feature = layer.GetFeature(index);

			// get the geometry of each feature
			this.geometryList.add(feature.GetGeometryRef());

			// get the attribute table
			Map<String, Object> temptMap = new TreeMap<String, Object>();
			for (String key : this.attributeTitles) {
				key = new String(key.getBytes());

				String type = this.attributeTitleType.get(key);
				try {
					if (type.contains("STRING") || type.contains("CHAR") || type.contains("STR")
							|| type.contains("CHARATER")) {
						temptMap.put(key, feature.GetFieldAsString(key));

					} else if (type.contains("DOUBLE") || type.contains("FLOAT") || type.contains("REAL")) {
						temptMap.put(key, feature.GetFieldAsDouble(key));

					} else if (type.contains("INT") || type.contains("INTEGER")) {
						temptMap.put(key, feature.GetFieldAsInteger(key));

					} else if (type.contains("DATE") || type.contains("TIME")) {
						temptMap.put(key, feature.GetFieldAsDouble(key));
					} else {
						temptMap.put(key, null);
					}
				} catch (Exception e) {
					temptMap.put(key, null);
				}
			}
			this.featureTable.add(temptMap);
		}
	}

	private void encodingTranslte(String sourcePath, String encoding)
			throws UnsupportedEncodingException, FileNotFoundException, IOException, InterruptedException {

		// create folder
		String temptFolder = GdalGlobal.createTemptFolder("Reader");
		String temptSourceJson = temptFolder + "temptSourceJson.geoJson";
		String temptConvertJson = temptFolder + "temptConvertJson.geoJson";

		// tempt create filePath
		GDAL_VECTOR_Translate ogr2ogr = new GDAL_VECTOR_Translate(sourcePath);
		ogr2ogr.saveAsJson(temptSourceJson);

		// converted encoding
		String content = String.join("", new AtFileReader(temptSourceJson, encoding).getContain());

		// check quote at ending
		if (content.contains("\\\"")) {
			content = content.replace("\\\"\"", "\\\"");
			content = content.replace("\\\"", "\\\"\"");
		}
		if (content.contains("\\\'")) {
			content = content.replace("\\\'\'", "\\\'");
			content = content.replace("\\\'", "\\\'\'");
		}

		// check quote of [\"]
		int quoteIndex = content.indexOf("\\");
		while (quoteIndex != -1) {

			// check for next string
			if (!"\"\'".contains(content.substring(quoteIndex + 1, quoteIndex + 2))) {
				content = content.substring(0, quoteIndex) + content.substring(quoteIndex + 1, content.length());
				quoteIndex = content.indexOf("\\", quoteIndex);
			} else {
				quoteIndex = content.indexOf("\\", quoteIndex + 1);
			}
		}

		new AtFileWriter(content, temptConvertJson).setEncoding("UTF-8").csvWriter();

		// get feature from converted shapeFile
		SpatialReader convertedShp = new SpatialReader(temptConvertJson);
		this.attributeTitles = convertedShp.getAttrubuteTitle();
		this.attributeTitleType = convertedShp.getAttributeTitleType();
		this.featureTable = convertedShp.getAttributeTable();
		this.geometryList = convertedShp.getGeometryList();
		this.EPSG = convertedShp.getEPSG();

		// delete temptFiles
//		FileFunction.delete(temptFolder);
	}

	// <==============================================>
	private void close() {
		this.dataSource.delete();
	}

}
