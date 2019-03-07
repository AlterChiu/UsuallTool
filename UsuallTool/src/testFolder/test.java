package testFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import geo.gdal.SpatialFileTranslater;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileList[] = new String[] { "H:/SHP/南崁集水區(下水道)20190205.shp", "H:/SHP/97南崁集水分區(明渠)2.0.shp",
				"H:/SHP/97東門溪集水分區(明渠)(222個分區).shp" };

		SpatialWriter spWrite = new SpatialWriter();
		Map<String, String> attributeTitle = new TreeMap<>();

		attributeTitle.put("Polygon_ID", "String");
		spWrite.setCoordinateSystem(SpatialWriter.TWD97_121);
		spWrite.setField(attributeTitle);

		for (String fileAdd : fileList) {
			SpatialReader shp = new SpatialReader(fileAdd,"UTF-8");
			List<Geometry> temptList = shp.getGeometryList();
			List<Map<String, String>> temptAttribute = shp.getAttributeTable();

			for (int index = 0; index < temptList.size(); index++) {
				Map<String, Object> attributeValue = new TreeMap<>();
				attributeValue.put("Polygon_ID", temptAttribute.get(index).get("Polygon_ID"));
				spWrite.addFeature(temptList.get(index), attributeValue);
			}
		}

		spWrite.saveAsShp("H:/SHP/dongman_merge(Sobek).shp");

	}

}
