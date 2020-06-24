package https.Rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
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
		String content = null;
		try {
			content = AtREST.convert.byteToString(getResponse(), this.encode);
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
			content = AtREST.convert.byteToString(getResponse(), this.encode);
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
			content = AtREST.convert.byteToString(getResponse(), this.encode);
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
			outByte = getResponse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outByte;
	}
	
	@Override
	public void setEncode(String encode) {
		this.encode = encode;
	}

	private byte[] getResponse() throws IOException {

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
