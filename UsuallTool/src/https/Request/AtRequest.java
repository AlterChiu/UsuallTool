package https.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonObject;

public class AtRequest {
	private String url;
	private Map<String, String> header;
	private Map<String, List<String>> body;
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

		this.setContentType(ContentType_FORM);
	}

	public void setBasicAuthor(String account, String password) {
		String auth = account + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));

		this.header.put(HttpHeaders.AUTHORIZATION, "Basic " + new String(encodedAuth));
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

	public void setBody(Map<String, List<String>> body) {
		this.body = body;
	}

	public void addBody(String key, String value) {
		List<String> values = Optional.ofNullable(this.body.get(key)).orElse(new ArrayList<String>());
		values.add(value);
		this.body.put(key, values);
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

	public void setCookie(String cookie) {
		this.header.put("Cookie", cookie);
	}

	public void setCookie(Response response) {
		this.header.put("Cookie", response.getCookie());
	}

	public void setContentType(String type) {
		this.header.put("Content-Type", type);
	}

	public Response doGet() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doGet(30);
	}

	public Response doGet(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.get(this.url);
		this.buildParameters(requestBuilder);
		this.buildHeader(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public Response doPost() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doPost(30);
	}

	public Response doPost(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.post().setUri(this.url);

		this.buildParameters(requestBuilder);
		this.buildHeader(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public Response doPatch(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.patch().setUri(this.url);

		this.buildParameters(requestBuilder);
		this.buildHeader(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public Response doPatch() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doPatch(30);
	}

	public Response doDelete(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.delete().setUri(this.url);

		this.buildHeader(requestBuilder);
		this.buildParameters(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public Response doDelete() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doDelete(30);
	}

	public Response doPut(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.put().setUri(this.url);

		this.buildHeader(requestBuilder);
		this.buildParameters(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public Response doPut() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doPut(30);
	}

	public Response doOptions(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.options().setUri(this.url);

		this.buildHeader(requestBuilder);
		this.buildParameters(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildTimeout(requestBuilder, timeoutSecond);

		return this.doRequest(requestBuilder);
	}

	public Response doOptions() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doOptions(30);
	}

	private Response doRequest(RequestBuilder requestBuilder) throws ClientProtocolException, IOException {
		return new Response(requestBuilder);
	}

	private RequestBuilder buildHeader(RequestBuilder requestBuilder) throws UnsupportedEncodingException {
		this.header.keySet().forEach(key -> {
			requestBuilder.setHeader(key, this.header.get(key));
		});
		return requestBuilder;
	}

	private RequestBuilder buildBody(RequestBuilder requestBuilder) throws UnsupportedEncodingException {

		// Json
		if (this.header.get("Content-Type").equals(ContentType_JSON)) {
			JsonObject outJson = new JsonObject();
			this.body.keySet().forEach(key -> {
				outJson.addProperty(key, this.body.get(key).get(0));
			});
			requestBuilder.setEntity(new StringEntity(outJson.toString()));
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

			// other or Form
		} else {
			List<NameValuePair> bodies = new ArrayList<>();
			this.body.keySet().forEach(key -> {
				List<String> values = this.body.get(key);

				values.forEach(value -> {
					bodies.add(new BasicNameValuePair(key, value));
				});
			});
			requestBuilder.setEntity(new UrlEncodedFormEntity(bodies));
		}

		return requestBuilder;
	}

	private RequestBuilder buildTimeout(RequestBuilder requestBuilder, int timeoutSecond) {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeoutSecond * 1000)
				.setConnectionRequestTimeout(timeoutSecond * 1000).setSocketTimeout(timeoutSecond * 1000).build();

		requestBuilder.setConfig(requestConfig);

		return requestBuilder;
	}

	private RequestBuilder buildParameters(RequestBuilder requestBuilder) throws URISyntaxException {

		if (this.parameter.size() == 0) {
			return requestBuilder;
		}

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		this.parameter.keySet().forEach(key -> {
			nameValuePairs.add(new BasicNameValuePair(key, this.parameter.get(key)));
		});

		URI uri = new URIBuilder(requestBuilder.getUri()).addParameters(nameValuePairs).build();
		requestBuilder.setUri(uri);

		try {
			System.out.println(requestBuilder.getUri().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return requestBuilder;
	}

	public class Response implements AutoCloseable {
		CloseableHttpResponse response;
		CloseableHttpClient httpclient;
		private int status;
		private Map<String, String> header = new HashMap<>();
		private HttpEntity entity;

		public Response(RequestBuilder requestBuilder) throws IOException {

			// create client
			this.httpclient = HttpClients.custom().build();
			this.response = httpclient.execute(requestBuilder.build());

			this.status = this.checkStatus(response);
			this.header = this.setHeaders(response);
			this.entity = response.getEntity();
			response.getEntity();
		}

		public String getHeader(String key) {
			return this.getHeader().get(key);
		}

		public Map<String, String> getHeader() {
			return this.header;
		}

		private Map<String, String> setHeaders(CloseableHttpResponse response) {
			Map<String, String> outMap = new HashMap<>();
			for (Header header : response.getAllHeaders()) {
				String name = header.getName();
				String value = header.getValue();

				// for multiple name/value
				String temptValue = Optional.ofNullable(outMap.get(name) + ";").orElse("");
				temptValue = temptValue + value;

				outMap.put(name, temptValue);
			}
			return outMap;
		}

		public String getBody() throws UnsupportedOperationException, IOException {
			return IOUtils.toString(this.entity.getContent(), "UTF-8");
		}

		public byte[] getBytes() throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			this.entity.writeTo(baos);
			return baos.toByteArray();
		}

		private int checkStatus(CloseableHttpResponse response) {
			this.status = response.getStatusLine().getStatusCode();
			if (status < 200 || status > 400) {
				new Exception("unExcept error while requesting: status code " + status).printStackTrace();
			}
			return this.status;
		}

		public String getCookie() {
			return Optional.ofNullable(this.header.get("Set-Cookie")).orElse(null);
		}

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			this.header.clear();
			this.response.close();
			this.httpclient.close();
		}

	}
}
