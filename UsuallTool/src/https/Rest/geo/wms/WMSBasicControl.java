package https.Rest.geo.wms;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.client.ClientProtocolException;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import https.Rest.AtRequest;
import usualTool.AtXmlReader;

public class WMSBasicControl {

	public static String WMS_TAIWAN = "http://maps.nlsc.gov.tw/S_Maps/wms";
	public static String LAYER_TAIWAN_GRAY = "EMAP01";
	public static String LAYER_TAIWAN_5000DEM = "B5000";
	public static String LAYER_TAIWAN_CITY = "CITY";
	public static String LAYER_TAIWAN_5000BOUND = "MB5000";
	public static String LAYER_TAIWAN_PHOTO = "PHOTO2";
	public static String LAYER_TAIWAN_ROAD = "ROAD";
	public static String LAYER_TAIWAN_TOWN = "TOWN";
	public static String LAYER_TAIWAN_VILLAGE = "Village";
	public static String LAYER_TAIWAN_EMAP = "EMAP"; // default
	public static String LAYER_TAIWAN_EN = "EMAP8";
	public static String LAYER_TAIWAN_NEW = "EMAP98";

	private Map<String, String> propertyMap = new HashMap<>();
	private String wmsUrl = WMS_TAIWAN;
	private String capabilityUrl;

	private AtXmlReader capability = null;
	private List<Layer> layerList = new ArrayList<>();

	public WMSBasicControl(String wmsUrl) throws UnsupportedEncodingException, IOException {
		// initial
		this.wmsUrl = wmsUrl;
		this.propertyMap = settingProperties(wmsUrl);

		// get capability
		StringBuilder capabilityUrl = new StringBuilder();
		capabilityUrl.append(this.wmsUrl);
		capabilityUrl.append("?");
		capabilityUrl.append("VERSION=" + this.propertyMap.get("VERSION"));
		capabilityUrl.append("&REQUEST=GetCapabilities");
		capabilityUrl.append("&Service=WMS");
		this.capabilityUrl = capabilityUrl.toString();

		// get layers
		try {
			this.capability = new AtXmlReader(new String(new AtRequest(this.capabilityUrl).doGet().getBytes()));
			this.capability.getNodes("Layer").get(0).selectNodes("./Layer").forEach(node -> {
				this.layerList.add(new Layer(node));
			});

		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public List<Layer> getLayers() {
		return this.layerList;
	}

	public byte[] getTileMap(int width, int height)
			throws UnsupportedOperationException, ClientProtocolException, IOException {
		this.propertyMap.put("WIDTH", width + "");
		this.propertyMap.put("HEIGHT", height + "");

		AtRequest doGet = new AtRequest(this.wmsUrl);
		this.propertyMap.keySet().forEach(key -> {
			doGet.addParameter(key, this.propertyMap.get(key));
		});

		return doGet.doGet().getBytes();
	}

	public byte[] getTileMap() throws UnsupportedOperationException, ClientProtocolException, IOException {
		return getTileMap(1920, 1080);
	}

	public void saveAsPng(int width, int height, String saveAdd) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(this.getTileMap(width, height));
		BufferedImage bImage = ImageIO.read(bis);
		ImageIO.write(bImage, "png", new File(saveAdd));
	}

	public void saveAsPng(String saveAdd) throws IOException {
		this.saveAsPng(1920, 1080, saveAdd);
	}

	public WMSBasicControl setBound(double minX, double maxX, double minY, double maxY, int epgs) {
		StringBuilder boundString = new StringBuilder();
		boundString.append(minX + ",");
		boundString.append(minY + ",");
		boundString.append(maxX + ",");
		boundString.append(maxY + "");
		this.propertyMap.put("BBOX", boundString.toString());

		this.propertyMap.put("SRS", "EPSG:" + epgs);
		return this;
	}

	public WMSBasicControl setStyle(String styleName) {
		this.propertyMap.put("STYLES", styleName);
		return this;
	}

	public WMSBasicControl setLayer(String layerName) {
		this.propertyMap.put("LAYERS", layerName);
		return this;
	}

	public WMSBasicControl setResolution(int resolution) {
		this.propertyMap.put("MAP_RESOLUTION", resolution + "");
		return this;
	}

	// <++++++++++++++++++++++++++++++++++++++++>
	// <+++++++++++++Private functions++++++++++++++++>
	// <++++++++++++++++++++++++++++++++++++++++>

	private static Map<String, String> settingProperties(String wms) {
		Map<String, Map<String, String>> outMap = new HashMap<>();
		outMap.put(WMS_TAIWAN, WMS_TAIWAN_PROPERTIES());
		return outMap.get(wms);
	}

	private static Map<String, String> WMS_TAIWAN_PROPERTIES() {
		Map<String, String> outMap = new HashMap<>();
		outMap.put("URL", "https://wms.nlsc.gov.tw/wms");
		outMap.put("SERVICE", "WMS");
		outMap.put("VERSION", "1.1.1");
		outMap.put("REQUEST", "GetMap");
		outMap.put("BBOX", "-180,-90,180,90");
		outMap.put("SRS", "EPSG:4326");
		outMap.put("WIDTH", "1920");
		outMap.put("HEIGHT", "1080");
		outMap.put("LAYERS", "EMAP");
		outMap.put("STYLES", "");
		outMap.put("FORMAT", "image/png");
		outMap.put("DPI", "300");
		outMap.put("MAP_RESOLUTION", "300");
		outMap.put("FORMAT_OPTIONS", "dpi:300");
		outMap.put("TRANSPARENT", "TRUE");
		return outMap;
	}

	public class Layer {
		private List<Integer> epsgList = new ArrayList<>();
		private List<String> stytle = new ArrayList<>();
		private Map<Integer, String> srsBoundary = new HashMap<>();
		private String name = "";
		private String title = "";

		public Layer(Node layerNode) {

			// get epsg
			layerNode.selectNodes("./SRS").forEach(
					srs -> this.epsgList.add(Integer.parseInt(srs.getText().toUpperCase().replace("EPSG:", ""))));

			// get title
			this.title = layerNode.selectNodes("./Title").get(0).getText();

			// get name
			this.name = layerNode.selectNodes("./Name").get(0).getText();

			// get style
			layerNode.selectNodes("./Style/Name").forEach(style -> this.stytle.add(style.getText()));

			// get bound in each epsg
			layerNode.selectNodes("./BoundingBox").forEach(bound -> {
				int boundEPSG = Integer.parseInt(bound.valueOf("@SRS").toUpperCase().replace("EPSG:", ""));
				double minX = Double.parseDouble(bound.valueOf("@minx"));
				double maxX = Double.parseDouble(bound.valueOf("@maxx"));
				double minY = Double.parseDouble(bound.valueOf("@miny"));
				double maxY = Double.parseDouble(bound.valueOf("@maxy"));

				StringBuilder boundString = new StringBuilder();
				boundString.append(minX + ",");
				boundString.append(minY + ",");
				boundString.append(maxX + ",");
				boundString.append(maxY + "");

				this.srsBoundary.put(boundEPSG, boundString.toString());
			});
		}

		public List<Integer> getEPSG() {
			return this.epsgList;
		}

		public List<String> getStyle() {
			return this.stytle;
		}

		public Map<Integer, String> getBoundString() {
			return this.srsBoundary;
		}

		public String getName() {
			return this.name;
		}

		public String getTitle() {
			return this.title;
		}
	}
}
