package geo.baseMap.MBTiles;

import java.sql.SQLException;

import geo.gdal.EnvelopBoundary;
import geo.gdal.GdalGlobal;

public class MBTiles {
	public final static double startX = -20037508.34278700;
	public final static double startY = -20037508.34278700;
	public final static double resolution = 156543.033928;
	public final static int tilePixel = 256;
	public final static int mbtilesEPSG = 3857;

	public static MBTilesReader read(String fileAdd) throws ClassNotFoundException, SQLException {
		return new MBTilesReader(fileAdd);
	}

	public static double getZoomLevelResolution(int zoomLevel) throws Exception {
		if (zoomLevel < 0) {
			new Exception().printStackTrace();
			throw new Exception("*ERROR* MBTiles zoomLevel must over 0");
		}
		return MBTiles.resolution * Math.pow(2, -1 * (zoomLevel));
	}

	public static int[] getColumnRow(int zoomLevel, double x, double y, int inEPSG) throws Exception {
		double[] mbtileCoordinate = GdalGlobal.CoordinateTranslator(x, y, inEPSG, MBTiles.mbtilesEPSG);

		double zoomResulotion = MBTiles.getZoomLevelResolution(zoomLevel);
		int row = (int) ((mbtileCoordinate[1] - MBTiles.startY) / (zoomResulotion * MBTiles.tilePixel)) + 1;
		int column = (int) ((mbtileCoordinate[0] - MBTiles.startX) / (zoomResulotion * MBTiles.tilePixel)) + 1;
		return new int[] { column, row };
	}

	public static EnvelopBoundary getBoundary(int zoomLevel, int column, int row, int outEPSG) throws Exception {

		double zoomResulotion = MBTiles.getZoomLevelResolution(zoomLevel);
		double minX = MBTiles.startX + column * zoomResulotion * MBTiles.tilePixel;
		double maxX = MBTiles.startX + (column + 1) * zoomResulotion * MBTiles.tilePixel;
		double minY = MBTiles.startY + row * zoomResulotion * MBTiles.tilePixel;
		double maxY = MBTiles.startY + (row + 1) * zoomResulotion * MBTiles.tilePixel;

		double maxXY[] = GdalGlobal.CoordinateTranslator(maxX, maxY, MBTiles.mbtilesEPSG, outEPSG);
		double minXY[] = GdalGlobal.CoordinateTranslator(minX, minY, MBTiles.mbtilesEPSG, outEPSG);

		return  new EnvelopBoundary(maxXY[0], maxXY[1], minXY[0], minXY[1]);
	}
}
