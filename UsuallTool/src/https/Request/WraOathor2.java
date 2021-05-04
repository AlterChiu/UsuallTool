package https.Request;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonSyntaxException;

public class WraOathor2 {
	public static String OATHOR2_WRA_IOW = "https://iapi.wra.gov.tw/v3/oauth2/token";

	public static Oauthor2 getWraIow(String clientID, String pass)
			throws JsonSyntaxException, UnsupportedOperationException, ClientProtocolException, IOException, URISyntaxException {
		return new WraIoWOauthor2(clientID, pass);
	}

	public static class WraIoWOauthor2 extends Oauthor2 {
		public WraIoWOauthor2(String clientID, String pass) throws JsonSyntaxException, UnsupportedOperationException,
				ClientProtocolException, IOException, URISyntaxException {
			this.url = WraOathor2.OATHOR2_WRA_IOW;
			this.clientId = clientID;
			this.clientPass = pass;
			this.run();
		}
	}
}
