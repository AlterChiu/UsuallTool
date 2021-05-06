package https.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class AtResponse implements AutoCloseable {
	CloseableHttpResponse response;
	CloseableHttpClient httpclient;
	private int status;
	private Map<String, String> header = new HashMap<>();
	private HttpEntity entity;
	private String body;
	private Map<String, Header> cookies = new LinkedHashMap<>();

	public AtResponse(RequestBuilder requestBuilder) throws IOException {
		// create client
		this.httpclient = HttpClients.custom().build();
		this.response = httpclient.execute(requestBuilder.build());

		this.entity = response.getEntity();
		this.status = this.checkStatus(response);
		this.header = this.setHeaders(response);
		this.body = this.setBody(response);
	}

	public String getHeader(String key) {
		return this.header.get(key);
	}

	public Map<String, String> getHeaders() {
		return this.header;
	}

	private Map<String, String> setHeaders(CloseableHttpResponse response) {
		Map<String, String> outMap = new HashMap<>();
		for (Header header : response.getAllHeaders()) {
			String name = header.getName();
			String value = header.getValue();

			if (name.equals("Set-Cookie")) {
				String cookiKey = value.split(";")[0].split("=")[0];
				this.cookies.put(cookiKey, header);
			} else {
				String temptValue = Optional.ofNullable(outMap.get(name)).orElse("");
				temptValue = value + ";" + temptValue;
				outMap.put(name, temptValue);
			}
		}

		// for setCookie
		if (outMap.containsKey("Set-Cookie")) {
			List<String> cookieList = new ArrayList<>();
			String cookieString[] = outMap.get("Set-Cookie").split(";");
			for (String temptCooki : cookieString) {
				String prefix = temptCooki.trim();

				if (!prefix.startsWith("path") && !prefix.startsWith("Http") && !prefix.startsWith("expires")
						&& !prefix.startsWith("SameSite")) {
					cookieList.add(prefix);
				}
			}
			outMap.put("Set-Cookie", String.join("; ", cookieList));
		}

		return outMap;
	}

	public String getBody() {
		return this.body;
	}

	private String setBody(CloseableHttpResponse response) throws UnsupportedOperationException, IOException {
		return IOUtils.toString(this.entity.getContent(), "UTF-8");
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.entity.writeTo(baos);
		return baos.toByteArray();
	}

	private int checkStatus(CloseableHttpResponse response) {
		this.status = response.getStatusLine().getStatusCode();
		return this.status;
	}

	public int getStatus() {
		return this.status;
	}

	public String getCookieString(String key) {
		return this.cookies.get(key).getValue();
	}

	public Map<String, Header> getCookies() {
		return this.cookies;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		this.header.clear();
		this.response.close();
		this.httpclient.close();
	}

}
