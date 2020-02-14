package testFolder;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.poi.sl.draw.geom.Path;
import org.gdal.ogr.Geometry;

import geo.gdal.GdalGlobal;
import geo.gdal.IrregularReachBasicControl;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class testAtCommon {
	public static String fileAdd = "E:\\LittleProject\\報告書\\109 - 淹水預警平台之建置與整合\\各項目文件\\二維模型申請圖幅\\";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String reachesSHP = "E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\100_10_reach.shp";

		IrregularReachBasicControl ir = new IrregularReachBasicControl(reachesSHP);
		List<Geometry> geoList = new ArrayList<>();

		ir.getNodeList().forEach(node -> geoList.add(node.getGeo()));
		new SpatialWriter().setGeoList(geoList)
				.saveAsGeoJson("E:\\LittleProject\\報告書\\109 - SMM\\測試\\製作集水區scsNode\\temptNodes.geoJson");

	}
}
