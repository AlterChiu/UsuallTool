package https.Rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dom4j.DocumentException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import usualTool.AtXmlReader;

public class AtDoGet implements AtREST {
	private static String encode = AtREST.ENCODE_UTF8;
	private Map<String, String> urlProperties = new TreeMap<>();
	private String url = "";

	public AtDoGet(String baseURL) {
		this.url = baseURL;
	}

	@Override
	public void addKey(String key, String value) {
		this.urlProperties.put(key, value);
	}

	@Override
	public String getStringRespond() {
		String content = null;
		try {
			content = AtREST.convert.byteToString(getResponse(getFullURL()), AtDoGet.encode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	@Override
	public AtXmlReader getXmlRespond() throws DocumentException {
		String content = null;
		try {
			content = AtREST.convert.byteToString(getResponse(getFullURL()), AtDoGet.encode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AtXmlReader(content);
	}

	@Override
	public JsonElement getJsonRespond() {
		String content = null;
		try {
			content = AtREST.convert.byteToString(getResponse(getFullURL()), AtDoGet.encode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JsonParser().parse(content);
	}

	@Override
	public byte[] getByte() {
		byte[] outByte = null;
		try {
			outByte = getResponse(getFullURL());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outByte;
	}

	@Override
	public void setEncode(String encode) {
		AtDoGet.encode = encode;
	}

	private String getFullURL() {
		// create url properties
		List<String> urlProperties = new ArrayList<>();
		for (String key : this.urlProperties.keySet()) {
			urlProperties.add(key + "=" + this.urlProperties.get(key));
		}

		// create full url
		StringBuilder fullURL = new StringBuilder();
		fullURL.append(this.url + "?");
		fullURL.append(String.join("&", urlProperties));

		return fullURL.toString();
	}

	public static byte[] getResponse(String fullURL) throws IOException {

		// get restful respond
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient client = builder.build();
		HttpUriRequest httpGet = new HttpGet(fullURL.toString());
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// get byte
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		response.getEntity().writeTo(baos);

		// close http
		try {
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

}
