package https.Rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import usualTool.AtXmlReader;

public class AtDoGet implements AtREST {
	private String encode = AtREST.ENCODE_UTF8;
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
		return getUrlComponent();
	}

	@Override
	public AtXmlReader getXmlRespond() throws DocumentException {
		return new AtXmlReader(getUrlComponent());
	}

	@Override
	public JsonElement getJsonRespond() {
		return new JsonParser().parse(getUrlComponent());
	}

	@Override
	public void setEncode(String encode) {
		this.encode = encode;
	}

	private String getUrlComponent() {

		// create url properties
		List<String> urlProperties = new ArrayList<>();
		for (String key : this.urlProperties.keySet()) {
			urlProperties.add(key + "=" + this.urlProperties.get(key));
		}

		// create full url
		StringBuilder fullURL = new StringBuilder();
		fullURL.append(this.url + "?");
		fullURL.append(String.join("&", urlProperties));

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

		// build respond String
		List<String> respond = new ArrayList<>();
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String entityStr = null;
				try {
					entityStr = EntityUtils.toString(entity, this.encode);
					respond.add(entityStr);
				} catch (ParseException | IOException e) {
					e.printStackTrace();
				}
			}
		}

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

		return String.join("\r\n", respond);
	}
}
