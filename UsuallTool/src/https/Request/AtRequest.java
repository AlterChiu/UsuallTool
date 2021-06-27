package https.Request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonObject;

public class AtRequest {
	private String url;
	private Map<String, String> header;
	private Map<String, List<String>> body;
	private Map<String, String> parameter;
	private Map<String, AtCookie> cookies;

	public static String ContentType_XML = "text/xml ";
	public static String ContentType_HTML = "text/html ";
	public static String ContentType_JSON = "application/json";
	public static String ContentType_FORM = "application/x-www-form-urlencoded";
	public static String ContentType_DATA = "multipart/form-data";

	public AtRequest(String url) {
		this.header = new LinkedHashMap<>();
		this.body = new LinkedHashMap<>();
		this.parameter = new LinkedHashMap<>();
		this.cookies = new LinkedHashMap<>();
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

	public String getHeader(String key) {
		return this.header.get(key);
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

	public void setCookie(AtResponse atResponse) {
		this.cookies = atResponse.getCookies();
	}

	public void addCookie(AtResponse AtResponse, String cookieKey) {
		if (AtResponse.getCookies().containsKey(cookieKey)) {
			this.cookies.put(cookieKey, AtResponse.getCookies().get(cookieKey));
		}
	}

	public void addCookie(String cookie) {
		try {
			AtCookie atCookie = new AtCookie(cookie);
			this.cookies.put(atCookie.getKey(), atCookie);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addCookie(Header header) {
		try {
			AtCookie atCookie = new AtCookie(header);
			this.cookies.put(atCookie.getKey(), atCookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setContentType(String type) {
		this.header.put("Content-Type", type);
	}

	public AtResponse doGet() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doGet(30);
	}

	public AtResponse doGet(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.get(this.url);
		this.buildTimeout(requestBuilder, timeoutSecond);
		return this.doRequest(requestBuilder);
	}

	public AtResponse doPost() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doPost(30);
	}

	public AtResponse doPost(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.post().setUri(this.url);
		this.buildTimeout(requestBuilder, timeoutSecond);
		return this.doRequest(requestBuilder);
	}

	public AtResponse doPatch(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.patch().setUri(this.url);
		this.buildTimeout(requestBuilder, timeoutSecond);
		return this.doRequest(requestBuilder);
	}

	public AtResponse doPatch() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doPatch(30);
	}

	public AtResponse doDelete(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.delete().setUri(this.url);
		this.buildTimeout(requestBuilder, timeoutSecond);
		return this.doRequest(requestBuilder);
	}

	public AtResponse doDelete() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doDelete(30);
	}

	public AtResponse doPut(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.put().setUri(this.url);
		this.buildTimeout(requestBuilder, timeoutSecond);
		return this.doRequest(requestBuilder);
	}

	public AtResponse doPut() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doPut(30);
	}

	public AtResponse doOptions(int timeoutSecond) throws ClientProtocolException, IOException, URISyntaxException {
		RequestBuilder requestBuilder = RequestBuilder.options().setUri(this.url);
		this.buildTimeout(requestBuilder, timeoutSecond);
		return this.doRequest(requestBuilder);
	}

	public AtResponse doOptions() throws ClientProtocolException, IOException, URISyntaxException {
		return this.doOptions(30);
	}

	private AtResponse doRequest(RequestBuilder requestBuilder)
			throws ClientProtocolException, IOException, URISyntaxException {

		this.buildHeader(requestBuilder);
		this.buildParameters(requestBuilder);
		this.buildBody(requestBuilder);
		this.buildCookies(requestBuilder);

		for (Header header : requestBuilder.getHeaders("Cookie")) {
			System.out.println(header.getValue());
		}
		return new AtResponse(requestBuilder);
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
			List<String> bodies = new ArrayList<>();
			this.body.keySet().forEach(key -> {
				List<String> values = this.body.get(key);

				values.forEach(value -> {
					try {
						bodies.add(UrlEncoding(key) + "=" + UrlEncoding(value));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			});
			requestBuilder.setEntity(new StringEntity(String.join("&", bodies)));
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

//		try {
//			System.out.println(requestBuilder.getUri().toURL());
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return requestBuilder;
	}

	private RequestBuilder buildCookies(RequestBuilder requestBuilder) {
		this.cookies.forEach((key, value) -> {
			try {
				requestBuilder.addHeader(new BasicHeader("Cookie", value.getUrlKeyValue()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		});

		return requestBuilder;
	}

	public static String UrlEncoding(String value) throws UnsupportedEncodingException {
		return UrlEncoding(value, "UTF-8");
	}

	public static String UrlEncoding(String value, String encode) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, encode);
	}

}
