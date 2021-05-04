package https.Request;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public abstract class Oauthor2 {

	protected String url;
	protected String clientId;
	protected String clientPass;
	protected String oatuorType;
	protected String accessToken;
	protected String refreshToken;

	protected void run() throws JsonSyntaxException, UnsupportedOperationException, ClientProtocolException,
			IOException, URISyntaxException {
		AtRequest getToken = new AtRequest(this.url);
		getToken.setContentType(AtRequest.ContentType_DATA);
		getToken.addBody("client_id", this.clientId);
		getToken.addBody("client_secret", this.clientPass);
		getToken.addBody("grant_type", "client_credentials");

		JsonObject tokenJson = new JsonParser().parse(getToken.doPost().getBody()).getAsJsonObject();
		this.accessToken = tokenJson.get("access_token").getAsString();
		this.refreshToken = tokenJson.get("refresh_token").getAsString();
		this.oatuorType = tokenJson.get("token_type").getAsString();
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public String getAuthorizeToken() {
		return this.oatuorType + " " + this.accessToken;
	}
}
