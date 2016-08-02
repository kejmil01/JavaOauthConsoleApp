import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import spark.Spark;

/* Google's original project - https://github.com/googlesamples/oauth-apps-for-windows/tree/master/OAuthConsoleApp
 * 
 * @author kejmil01, @date 8/1/16
 */
public class Main {

	// client configuration
	private static final String clientID = "581786658708-elflankerquo1a6vsckabbhn25hclla0.apps.googleusercontent.com";
	private static final String clientSecret = "3f6NggMbPtrmIBpgx-MK2xXK";
	private static final String authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth";
	private static final String oauthEndpoint = "https://www.googleapis.com/oauth2/v4/";
	private static final String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

	private static final String code_challenge_method = "S256";

	private static boolean doWork = true;

	public static void main(String[] args) {
		PortHolder portHolder = new PortHolder();
		String state = Base64Helper.generateRandomNoPaddingBase64(32);
		String code_verifier = Base64Helper.generateRandomNoPaddingBase64(32);
		try {
			String code_challenge = Base64Helper.encodeNoPadding(SHA256Encoder.encode(code_verifier));

			String ip = InetAddress.getLoopbackAddress().getHostAddress();
			portHolder.reserveFreeRandomPort();
			String redirectURI = String.format("http://%s:%d/", ip, portHolder.getPort());
			String encodedRedirectURI = URLEncoder.encode(redirectURI, "UTF-8");

			LoopbackServer serverThread = new LoopbackServer(ip, portHolder.getPort());
			serverThread.setListener(new LoopbackServer.ServerListener() {

				@Override
				public void onResponse(String code, String incoming_state) {
					try {
						if (!incoming_state.equals(state)) {
							System.out.println("Received request with invalid state - " + incoming_state);
						}
						{
							Retrofit retrofit = new Retrofit.Builder()
									.addConverterFactory(JacksonConverterFactory.create()).baseUrl(oauthEndpoint)
									.build();
							GoogleAuthAPI api = retrofit.create(GoogleAuthAPI.class);
							try {
								Response<GoogleAuthResponse> r = api.exchangeToken(code, redirectURI, clientID,
										code_verifier, clientSecret, "", "authorization_code").execute();
								if (r != null) {
									System.out.println("Access token: " + r.body().access_token);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} finally {
						doWork = false;
					}
				}
			});

			// Free port and run loopback server on it
			portHolder.freePort();
			serverThread.run();

			// Create the OAuth 2.0 authorization request
			String format = "%s?response_type=code&scope=openid profile&redirect_uri=%s&client_id=%s&state=%s&code_challenge=%s&code_challenge_method=%s";
			String authorizationRequest = String.format(format, authorizationEndpoint, encodedRedirectURI, clientID,
					state, code_challenge, code_challenge_method);
			String encodedAuthorizationRequest = authorizationRequest.replace(" ", "%20");

			// Send request to the browser
			Desktop.getDesktop().browse(URI.create(encodedAuthorizationRequest));

			System.out.println(encodedAuthorizationRequest + "\n");

			while (doWork) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// SHA256 Exc
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// SHA256 Exc - ASCII
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				portHolder.freePort();
				Spark.stop();
				System.out.println("Press any key to end");
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
