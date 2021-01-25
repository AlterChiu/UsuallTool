
package geo.gdal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

import geo.gdal.vector.Gdal_VectorTranslate;
import usualTool.AtFileFunction;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class SpatialReader {
	private DataSource dataSource;
	private List<String> attributeTitles = new ArrayList<>();
	private List<SpatialFeature> featureList = new ArrayList<>();
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
		this.dataSource.delete();
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
		return this.featureList.parallelStream().map(feature -> feature.getProperties()).collect(Collectors.toList());
	}

	public List<Geometry> getGeometryList() {
		return this.featureList.parallelStream().map(feature -> feature.getGeometry()).collect(Collectors.toList());
	}

	public List<SpatialFeature> getFeatureList() {
		return this.featureList;
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
		this.featureList.forEach(feature -> {
			String attrValue = String.valueOf(feature.getProperty(columnID));
			List<Geometry> temptGeoList = Optional.ofNullable(outMap.get(attrValue)).orElse(new ArrayList<>());
			temptGeoList.add(feature.getGeometry());
			outMap.put(attrValue, temptGeoList);
		});
		return outMap;
	}

	public void reNameFeild(String oldFieldName, String newFeildName) {

		// change type
		if (this.attributeTitleType.containsKey(oldFieldName)) {
			this.attributeTitleType.put(newFeildName, this.attributeTitleType.get(oldFieldName));
		}

		// change attrTables
		this.featureList.forEach(feature -> {
			feature.renameField(oldFieldName, newFeildName);
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
		spWriter.setFeatureList(this.featureList);

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

			// get the attribute table
			Map<String, Object> temptMap = new HashMap<String, Object>();
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
			featureList.add(new SpatialFeature(temptMap, feature.GetGeometryRef()));
		}
	}

	private void encodingTranslte(String sourcePath, String encoding)
			throws UnsupportedEncodingException, FileNotFoundException, IOException, InterruptedException {

		// create folder
		String temptFolder = AtFileFunction.createTemptFolder();
		String temptSourceJson = temptFolder + "temptSourceJson.geoJson";
		String temptConvertJson = temptFolder + "temptConvertJson.geoJson";

		// tempt create filePath
		Gdal_VectorTranslate ogr2ogr = new Gdal_VectorTranslate(sourcePath);
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
		this.featureList = convertedShp.getFeatureList();
		this.EPSG = convertedShp.getEPSG();

		// delete temptFiles
		AtFileFunction.delete(temptFolder);
	}

	// <==============================================>
	private void close() {
		this.dataSource.delete();
	}

}
