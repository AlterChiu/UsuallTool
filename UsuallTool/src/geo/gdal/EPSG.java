package geo.gdal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EPSG {
	private static Map<Integer, String> proj4Mapping = new HashMap<>();

	public static String getProj4(int epsg) throws IOException {

		// first run, initialize
		if (EPSG.proj4Mapping.size() == 0)
			initializeMapping();

		// check for epsgCode
		if (EPSG.proj4Mapping.containsKey(epsg)) {
			return EPSG.proj4Mapping.get(epsg);
		} else {
			new Exception("*WARN* not EPSG supported, " + epsg + "\r\nreturn WGS84 epsg codeing");
			return EPSG.proj4Mapping.get(4326);
		}
	}

	private static void initializeMapping() throws IOException {

		InputStream epsgInputSream = EPSG.class.getResourceAsStream("/geo/gdal/resource/epsg");
		BufferedReader br = new BufferedReader(new InputStreamReader(epsgInputSream));

		String line;
		while ((line = br.readLine()) != null) {
			if (!line.contains("#")) {

				String[] temptLine = line.split(" +");
				String name = temptLine[0].replace("<", "").replace(">", "");
				List<String> proj4 = new ArrayList<>();
				for (int index = 1; index < temptLine.length - 1; index++) {
					proj4.add(temptLine[index]);
				}

				EPSG.proj4Mapping.put(Integer.parseInt(name), String.join(" ", proj4));
			}
		}
		br.close();
	}
}
