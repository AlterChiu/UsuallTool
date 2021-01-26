package testFolder;

import geo.gdal.RasterReader;

public class testAtCommon {
	public static void main(String[] args) throws Exception {

		String fileAdd = "D:\\tempFolder\\testingDEM_copy.asc";

		RasterReader raster = new RasterReader(fileAdd);
		raster.setValue((int) 0, 0, -50);
		raster.saveAs("D:\\tempFolder\\testingDEM_modify.asc");
		raster.close();

	}
}
