package geo.common.Task.SOBEK.Modeling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class ModelingSB_BankLine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		 * base on the bankLine, output all the vertices on bankLine, and giving each
		 * vertices an attribute properties, "CrossSection" of "BankLine"
		 * 
		 * "CrossSection" : which vertices is on the crossSection line
		 * 
		 * "BankLine" : other vertices
		 * 
		 */

		String BankLineFileAdd = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\溢堤線更新\\港尾溝溪-建模測試\\HyDEM_BankLine.shp";
		String CrossSectionFileAdd = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\溢堤線更新\\港尾溝溪-建模測試\\HyDEM_SplitLine.shp";
		String outputFileAdd = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\溢堤線更新\\港尾溝溪-建模測試\\ModelingSB_BankLineVertice.shp";

		/*
		 * get CrossSection
		 */
		List<Geometry> crossSectionGeoList = new SpatialReader(CrossSectionFileAdd).getGeometryList(); // get file
		Set<String> crossSectionVertices = GdalGlobal.GeometryToPointKeySet(crossSectionGeoList); // vertices, x_y

		// get BankLine file
		List<Geometry> bankLineGeoList = new SpatialReader(BankLineFileAdd).getGeometryList();
		Set<String> bankLineVertices = GdalGlobal.GeometryToPointKeySet(bankLineGeoList); // vertices, x_y

		// setting output file
		SpatialWriter outputShp = new SpatialWriter();
		outputShp.addFieldType("ID", "String");
		outputShp.addFieldType("X", "double");
		outputShp.addFieldType("Y", "double");
		outputShp.addFieldType("type", "String");

		// getting division
		bankLineVertices.forEach(vertice -> {
			String[] coordinate = vertice.split("_");
			double x = Double.parseDouble(coordinate[0]);
			double y = Double.parseDouble(coordinate[1]);
			Geometry point = GdalGlobal.CreatePoint(x, y);

			Map<String, Object> feature = new HashMap<>();
			feature.put("ID", outputShp.getSize() + "");
			feature.put("X", x);
			feature.put("Y", y);

			if (crossSectionVertices.contains(vertice)) {
				feature.put("type", "CrossSection");
			} else {
				feature.put("type", "BankLine");
			}

			outputShp.addFeature(point, feature);
		});

		outputShp.saveAsShp(outputFileAdd);
	}

}
