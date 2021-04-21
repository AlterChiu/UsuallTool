package testFolder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.ClientProtocolException;
import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import database.AtDbBasicControl;
import database.AtSqlResult;
import geo.baseMap.MBTiles.MBTiles;
import geo.baseMap.MBTiles.MBTilesControl;
import geo.baseMap.wms.WMSBasicControl;
import geo.common.CoordinateTranslate;
import geo.gdal.GdalGlobal;
import geo.gdal.GdalGlobal_DataFormat;
import geo.gdal.RasterReader;
import geo.gdal.SpatialFeature;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import geo.gdal.raster.Gdal_RasterMerge;
import geo.gdal.raster.Gdal_RasterTranslateFormat;
import https.Request.AtRequest;
import https.Request.WraOathor2;
import https.Request.WraOathor2.WraIoWOauthor2;
import https.Request.AtRequest.Response;
import https.Request.Oauthor2;
import usualTool.AtFileFunction;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;

public class testAtCommon {
	public static void main(String[] args) throws Exception {

	}

	public static byte[] getBytes(byte[] bytes, int start, int end) {
		List<Byte> byteArray = new ArrayList<>();
		for (int index = start; index < end; index++) {
			byteArray.add(bytes[index]);
		}
		return ArrayUtils.toPrimitive(byteArray.parallelStream().toArray(Byte[]::new));
	}
}
