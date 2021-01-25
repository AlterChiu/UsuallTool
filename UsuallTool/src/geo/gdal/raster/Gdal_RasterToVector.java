
package geo.gdal.raster;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gdal.ogr.Geometry;
import asciiFunction.AsciiBasicControl;
import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal_DataFormat;
import geo.gdal.SpatialFeature;
import geo.gdal.SpatialWriter;
import usualTool.AtCommonMath;
import usualTool.AtFileFunction;

public class Gdal_RasterToVector {
	private String temptFolder = AtFileFunction.createTemptFolder();

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

	public Gdal_RasterToVector(String inputFile) {
		this.inputFile = inputFile;
	}

	public Gdal_RasterToVector setOutputDataType(String outputDataType) {
		this.outputDataType = outputDataType;
		return this;
	}

	public Gdal_RasterToVector setDataDecimal(int dataDecimal) {
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
		this.temptFolder = AtFileFunction.createTemptFolder();

		/*
		 * setting temptFile fileName
		 */
		String temptFileName = AtFileFunction.getTempFileName(this.temptFolder, ".txt");
		String temptFileDirection = this.temptFolder + temptFileName;

		// translate raster data to asciiFormat
		Gdal_RasterTranslateFormat rasterTranslate = new Gdal_RasterTranslateFormat(this.inputFile);
		rasterTranslate.setNullValue("-999");
		rasterTranslate.save(temptFileDirection, this.outputDataType);

		// get asciiFormate data
		AsciiBasicControl ascii = new AsciiBasicControl(temptFileDirection);

		// setting output geoList and attributeData
		List<SpatialFeature> featureList = new ArrayList<>();
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

					// setting polygon to each grid
					Geometry geo = getPolygon(ascii.getCoordinate(column, row), ascii.getCellSize());

					// add
					featureList.add(new SpatialFeature(temptValues, geo));
				}
			}
		}

		// output geoList and attribute table to spacialFile
		new SpatialWriter().setFieldType(dataType).setFeatureList(featureList).saveAs(saveAdd, this.outputDataType);
		AtFileFunction.delete(this.temptFolder);
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
