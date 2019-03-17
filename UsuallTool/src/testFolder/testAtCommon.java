package testFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gdal.ogr.Geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import asciiFunction.AsciiBasicControl;
import asciiFunction.XYZToAscii;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import usualTool.AtFileReader;

public class testAtCommon {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SpatialReader shpFile = new SpatialReader("H:\\SHP\\水文分析一維\\MergeAll.shp");
		List<Geometry> geoList = shpFile.getGeometryList();
		List<Map<String, String>> shpAttr = shpFile.getAttributeTable();

		List<String> outList = new ArrayList<>();

		for (int index = 0; index < geoList.size(); index++) {
			if (GdalGlobal.GeomertyToPath2D(geoList.get(index)) == null) {
				JsonObject json = new JsonParser().parse(geoList.get(index).ExportToJson()).getAsJsonObject();

				System.out.print(shpAttr.get(index).get("ID"));
				JsonArray polygons = json.get("coordinates").getAsJsonArray();
				for (int polygon = 0; polygon < polygons.size(); polygon++) {
					for (int point = 0; point < polygons.get(polygon).getAsJsonArray().size(); point++) {
						System.out.print("\t"
								+ polygons.get(polygon).getAsJsonArray().get(point).getAsJsonArray().size() + "\t");
					}
				}
				System.out.println();
			}
		}

	}

}
