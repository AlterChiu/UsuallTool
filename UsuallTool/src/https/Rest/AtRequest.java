package https.Rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;

public class AtRequest {
	private String url;
	private Map<String, String> header;
	private Map<String, String> body;
	private Map<String, String> parameter;

	public static String ContentType_XML = "text/xml ";
	public static String ContentType_JSON = "application/json";
	public static String ContentType_FORM = "application/x-www-form-urlencoded";
	public static String ContentType_DATA = "multipart/form-data";

	public AtRequest(String url) {
		this.header = new HashMap<>();
		this.body = new HashMap<>();
		this.parameter = new HashMap<>();
		this.url = url;

		this.setContentType("application/json");
	}


	public void setBasicAuthor(String account, String password) {
		String auth = account + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));

		this.header.put(HttpHeaders.AUTHORIZATION, "Basic " + new String(encodedAuth));
		System.out.println("Basic " + new String(encodedAuth));
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public void addHeader(String key, String value) {
		this.header.put(key, value);
	}

	public void removeHeader(String key) {
		if (this.header.containsKey(key)) {
			this.header.remove(key);
		}
	}

	public void setBody(Map<String, String> body) {
		this.body = body;
	}

	public void addBody(String key, String value) {
		this.body.put(key, value);
	}

	public void removeBody(String key) {
		if (this.body.containsKey(key)) {
			this.body.remove(key);
		}
	}

	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}

	public void addParameter(String key, String value) {
		this.parameter.put(key, value);
	}

	public void removeParamter(String key) {
		if (this.parameter.containsKey(key)) {
			this.parameter.remove(key);
		}
	}

	public void setContentType(String type) {
		this.header.put("Content-Type", type);
	}

	public String doGet() throws ClientProtocolException, IOException {
		return this.doGet(30);
	}

	public String doGet(int timeoutSecond) throws ClientProtocolException, IOException {
		String getUrl = this.url + this.buildParameters();
		RequestBuilder requestBuilder = RequestBuilder.get(getUrl);

		this.buildHeader(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public String doPost() throws ClientProtocolException, IOException {
		return this.doPost(30);
	}

	public String doPost(int timeoutSecond) throws ClientProtocolException, IOException {
		RequestBuilder requestBuilder = RequestBuilder.post().setUri(this.url);

		this.buildHeader(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

//	public String doPatch() {
//
//	}
//
//	public String doDelete() {
//
//	}
//
//	public String doPut() {
//
//	}
//
//	public String doOptions() {
//
//	}

	private String doRequest(RequestBuilder requestBuilder) throws ClientProtocolException, IOException {

		// handler
		ResponseHandler<String> responseHandler = response -> {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 300 && status < 400) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				new ClientProtocolException("Unexpected response status: " + status);
				return "Unexpected response status: " + status;
			}
		};

		// setting custom http headers on the httpclient
		CloseableHttpClient httpclient = HttpClients.custom().build();

		String responseBody = httpclient.execute(requestBuilder.build(), responseHandler);
		httpclient.close();

		return responseBody;
	}

	private RequestBuilder buildHeader(RequestBuilder requestBuilder) throws UnsupportedEncodingException {
		this.header.keySet().forEach(key -> {
			requestBuilder.setHeader(key, this.header.get(key));
		});
		return requestBuilder;
	}

	private RequestBuilder buildBody(RequestBuilder requestBuilder) throws UnsupportedEncodingException {

		// Form
		if (this.header.get("Content-Type").equals(ContentType_FORM)) {
			List<String> temptLine = new ArrayList<>();
			this.body.keySet().forEach(key -> {
				try {
					temptLine.add(this.urlEncoding(key) + "=" + this.urlEncoding(this.body.get(key)));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			});
			requestBuilder.setEntity(new StringEntity(String.join("&", temptLine)));

			// XML
		} else if (this.header.get("Content-Type").equals(ContentType_XML)) {
			StringBuilder outString = new StringBuilder();
			outString.append("<data>");

			this.body.keySet().forEach(key -> {
				StringBuilder temptString = new StringBuilder();
				temptString.append("<" + key + ">");
				temptString.append(this.body.get(key));
				temptString.append("<//" + key + ">");
				outString.append(temptString.toString());
			});

			outString.append("<//data>");
			requestBuilder.setEntity(new StringEntity(outString.toString()));

			// other for Json
		} else {
			JsonObject outJson = new JsonObject();
			this.body.keySet().forEach(key -> {
				outJson.addProperty(key, this.body.get(key));
			});
			requestBuilder.setEntity(new StringEntity(outJson.toString()));
		}

		return requestBuilder;
	}

	private RequestBuilder buildTimeout(RequestBuilder requestBuilder, int timeoutSecond) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeoutSecond * 1000)
				.setConnectionRequestTimeout(timeoutSecond * 1000).setSocketTimeout(timeoutSecond * 1000).build();

		requestBuilder.setConfig(requestConfig);

		return requestBuilder;
	}

	private String buildParameters() {
		List<String> temptLine = new ArrayList<>();
		this.parameter.keySet().forEach(key -> {
			temptLine.add(key + "=" + this.parameter.get(key));
		});

		return "?" + String.join("&", temptLine);
	}

	private String urlEncoding(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}
}
