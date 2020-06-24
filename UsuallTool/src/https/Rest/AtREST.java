package https.Rest;

import java.io.UnsupportedEncodingException;

import org.dom4j.DocumentException;

import com.google.gson.JsonElement;

import usualTool.AtXmlReader;

public interface AtREST {
	public static String ENCODE_UTF8 = "UTF-8";
	public static String ENCODE_ANSI = "Cp1252";
	public static String ENCODE_Unicode = "Unicode";
	public static String ENCODE_BIG5 = "big5";
	public static String ENCODE_ASCII = "ASCII";

	public void addKey(String key, String value);

	public void setEncode(String encode);

	public String getStringRespond();

	public AtXmlReader getXmlRespond() throws DocumentException;

	public JsonElement getJsonRespond();

	public byte[] getByte();

	public static class convert {
		public static String byteToString(byte[] byteArray, String encode) throws UnsupportedEncodingException {
			return new String(byteArray, encode);
		}
	}
}
