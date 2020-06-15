package https.Rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import usualTool.AtXmlReader;

public class AtDoPost implements AtREST {
	private String encode = AtREST.ENCODE_UTF8;
	private Map<String, String> urlProperties = new TreeMap<>();
	private String url = "";

	public AtDoPost(String baseURL) {
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

		// setting httpClient
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient client = builder.build();
		HttpPost httpPost = new HttpPost(this.url);

		// setting post properties
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		this.urlProperties.keySet().forEach(key -> {
			nvps.add(new BasicNameValuePair(key, this.urlProperties.get(key)));
		});

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, encode));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// get respond
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// respond success
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
		}

		return String.join("\r\n", respond);
	}
}
