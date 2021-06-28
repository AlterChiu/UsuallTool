package LineBot;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import https.Request.AtRequest;

public class Global {

	public static String url = "https://notify-api.line.me/api/notify";
	public static String AUTH_TEST = "Bearer WOkHscd9eTzdYHKle3SR44KhiRJkJo0TpLNJYMOLBVJ";
	public static String AUTH_AEclub = "Bearer VDBb06Y0zIByWgzSQruDJT7rrjavJ4xHGMkkiIO7Odq";

	public static String sendMes(String author, String mes)
			throws ClientProtocolException, IOException, URISyntaxException {
		AtRequest request = new AtRequest(Global.url);
		request.addHeader("Authorization", author);
		request.addBody("message", mes);

		return request.doPost().getBody();
	}
}
