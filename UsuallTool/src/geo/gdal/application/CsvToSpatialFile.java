package geo.gdal.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialFeature;
import geo.gdal.SpatialWriter;
import usualTool.AtFileReader;

public class CsvToSpatialFile extends SpatialWriter {
	private List<String> title;
	private List<String[]> csvContent;
	private int indexX = 1;
	private int indexY = 2;

	/*
	 * 
	 */
	public CsvToSpatialFile(String file, Boolean containTitle, int indexX, int indexY) throws IOException {
		List<String[]> temptList = new ArrayList<>(Arrays.asList(new AtFileReader(file).getCsv()));
		this.title = new ArrayList<>(Arrays.asList(temptList.get(0)));
		if (containTitle) {
			temptList.remove(0);
		}

		this.csvContent = temptList;
		this.indexX = indexX;
		this.indexY = indexY;
		initialize();
	}

	public CsvToSpatialFile(String csvContent[][], Boolean containTitle, int indexX, int indexY) {
		this.title = new ArrayList<String>(Arrays.asList(csvContent[0]));
		List<String[]> temptList = new ArrayList<>(Arrays.asList(csvContent));

		// set title
		this.title = new ArrayList<>(Arrays.asList(temptList.get(0)));
		if (containTitle) {
			temptList.remove(0);
		}

		this.csvContent = temptList;
		this.indexX = indexX;
		this.indexY = indexY;
		initialize();
	}

	public CsvToSpatialFile(List<String[]> content, Boolean containTitle, int indexX, int indexY) {
		List<String[]> temptList = content;
		this.title = new ArrayList<String>(Arrays.asList(temptList.get(0)));
		if (containTitle) {
			temptList.remove(0);
		}

		this.csvContent = temptList;
		this.indexX = indexX;
		this.indexY = indexY;
		initialize();
	}

	private void initialize() {
		// set field type
		this.fieldType = new TreeMap<>();
		for (int index = 0; index < this.title.size(); index++) {
			if (index == this.indexX || index == this.indexY) {
				fieldType.put(this.title.get(index), "Double");
			} else {
				fieldType.put(this.title.get(index), "String");
			}
		}

		// set feature
		csvContent.forEach(feature -> {

			// set geometry
			double x = Double.parseDouble(feature[this.indexX]);
			double y = Double.parseDouble(feature[this.indexY]);
			Geometry geo = GdalGlobal.CreatePoint(x, y);

			// set attribute
			Map<String, Object> temptAttribute = new TreeMap<>();
			for (int index = 0; index < this.title.size(); index++) {
				temptAttribute.put(title.get(index), Double.parseDouble(feature[index]));
			}
			this.featureList.add(new SpatialFeature(temptAttribute, geo));
		});
	}
}
