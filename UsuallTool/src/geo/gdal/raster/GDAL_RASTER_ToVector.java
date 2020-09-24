
package geo.gdal.raster;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gdal.ogr.Geometry;
import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal_DataFormat;
import geo.gdal.SpatialWriter;
import usualTool.AtCommonMath;
import usualTool.FileFunction;

public class GDAL_RASTER_ToVector {
	private String temptFolder = GdalGlobal.temptFolder + "RasterToVector\\";

	/*
	 * this function will not using gda_polygonize function because there is
	 * something not so customize So I making this processing to translate raster
	 * data to vector
	 * 
	 * this processing will include raster_translation 1. convert raster data to
	 * asciiFormate 2. convert asciiFormate to vector // without no dataValue
	 */
	private String inputFile = "";
	private int dataDecimal = 4;
	private String outputDataType = GdalGlobal_DataFormat.DATAFORMAT_VECTOR_ESRIShapefile;

	public GDAL_RASTER_ToVector(String inputFile) {
		this.inputFile = inputFile;
	}

	public GDAL_RASTER_ToVector setOutputDataType(String outputDataType) {
		this.outputDataType = outputDataType;
		return this;
	}

	public GDAL_RASTER_ToVector setDataDecimal(int dataDecimal) {
		this.dataDecimal = dataDecimal;
		return this;
	}

	public void save(String saveAdd, String outputDataType) throws IOException, InterruptedException {
		this.outputDataType = outputDataType;
		this.save(saveAdd);
	}

	public void save(String saveAdd) throws IOException, InterruptedException {
		/*
		 * clear gdalGlobal temptFolder
		 */
		this.temptFolder = this.temptFolder + "-" + GdalGlobal.getTempFileName(GdalGlobal.temptFolder, "");
		FileFunction.newFolder(this.temptFolder);
		for (String fileName : new File(this.temptFolder).list()) {
			FileFunction.delete(this.temptFolder + "\\" + fileName);
		}

		/*
		 * setting temptFile fileName
		 */
		String temptFileName = GdalGlobal.getTempFileName(this.temptFolder, ".txt");
		String temptFileDirection = this.temptFolder + temptFileName;

		// translate raster data to asciiFormat
		GDAL_RASTER_TranslateFormat rasterTranslate = new GDAL_RASTER_TranslateFormat(this.inputFile);
		rasterTranslate.setNullValue("-999");
		rasterTranslate.save(temptFileDirection, this.outputDataType);

		// get asciiFormate data
		AsciiBasicControl ascii = new AsciiBasicControl(temptFileDirection);

		// setting output geoList and attributeData
		List<Geometry> geoList = new ArrayList<>();
		List<Map<String, Object>> attrData = new ArrayList<>();
		Map<String, String> dataType = new HashMap<>();

		// setting attribute table as value (double value)
		dataType.put("value", "Double");

		// skip value contain
		for (int row = 0; row < ascii.getRow(); row++) {
			for (int column = 0; column < ascii.getColumn(); column++) {

				String temptValue = ascii.getValue(column, row);
				if (!temptValue.equals(ascii.getNullValue())) {

					// setting value to each grid
					Map<String, Object> temptValues = new HashMap<>();
					temptValues.put("value", Double.parseDouble(temptValue));
					attrData.add(temptValues);

					// setting polygon to each grid
					geoList.add(getPolygon(ascii.getCoordinate(column, row), ascii.getCellSize()));
				}
			}
		}

		// output geoList and attribute table to spacialFile
		new SpatialWriter().setGeoList(geoList).setFieldType(dataType).setAttribute(attrData).saceAs(saveAdd,
				this.outputDataType);
		FileFunction.delete(this.temptFolder);
	}

	private Geometry getPolygon(double[] centerPoint, double cellSize) {
		double minX = AtCommonMath.getDecimal_Double(centerPoint[0] - 0.5 * cellSize, this.dataDecimal);
		double maxX = AtCommonMath.getDecimal_Double(centerPoint[0] + 0.5 * cellSize, this.dataDecimal);
		double minY = AtCommonMath.getDecimal_Double(centerPoint[1] - 0.5 * cellSize, this.dataDecimal);
		double maxY = AtCommonMath.getDecimal_Double(centerPoint[1] + 0.5 * cellSize, this.dataDecimal);

		Path2D temptPath = new Path2D.Double();
		temptPath.moveTo(minX, minY);
		temptPath.lineTo(maxX, minY);
		temptPath.lineTo(maxX, maxY);
		temptPath.lineTo(minX, maxY);

		return GdalGlobal.Path2DToGeometry(temptPath);
	}
}
