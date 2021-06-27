package https.Request;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class AtCookie {
	private Header header;

	public AtCookie(Header header) throws Exception {
		this.header = header;
		this.process(header.getValue());
	}

	public AtCookie(String cookie) throws Exception {
		this.header = new BasicHeader("Cookie", cookie);
		this.process(cookie);
	}

	public void process(String cookie) throws Exception {
		String[] properties = cookie.split(";");

		try {
			String[] temptValues = properties[0].trim().split("=");
			this.key = temptValues[0];
			this.value = temptValues[1];
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("*ERROR* no name value for cookie, \r\n" + cookie);
		}

		for (int index = 1; index < properties.length; index++) {
			String[] temptValues = properties[0].trim().split("=");

			String key = temptValues[0].toUpperCase();
			if (key.equals("DOMAIN")) {
				this.domain = temptValues[1];
			} else

			if (key.equals("PATH")) {
				this.path = temptValues[1];
			} else

			if (key.equals("EXPIRES")) {
				this.expires = temptValues[1];
			} else

			if (key.equals("HTTPONLY")) {
				this.httpOnly = "true";
			} else

			if (key.equals("SECURE")) {
				this.secure = "true";
			}
		}
	}

	public Header getHeader() {
		return this.header;
	}

	private String key;

	public String getKey() {
		return this.key;
	}

	private String value;

	public String getValue() {
		return this.value;
	}

	private String path;

	public String getPath() {
		return Optional.ofNullable(this.path).orElse("/");
	}

	private String domain;

	public String getDomain() {
		return Optional.ofNullable(this.domain).orElse("");
	}

	private String expires;

	public String getExpires() {
		return Optional.ofNullable(this.expires).orElse("");
	}

	private String httpOnly;

	public String getHttpOnly() {
		return Optional.ofNullable(this.httpOnly).orElse("false");
	}

	private String secure;

	public String getSecure() {
		return Optional.ofNullable(this.secure).orElse("false");
	}

	public String getUrlKeyValue(String encode) throws UnsupportedEncodingException {
		return this.key + "=" + AtRequest.UrlEncoding(this.value, encode);
	}

	public String getUrlKeyValue() throws UnsupportedEncodingException {
		return this.getUrlKeyValue("UTF-8");
	}

}
