import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/*
 * @author kejmil01, @date 8/1/16
 */
public interface GoogleAuthAPI {
	@FormUrlEncoded
	@Headers("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
	@POST("token")	
	Call<GoogleAuthResponse> exchangeToken(@Field("code") String code, @Field("redirect_uri") String redirect_uri,
			@Field("client_id") String client_id, @Field("code_verifier") String code_verifier, 
			@Field("client_secret") String client_secret, @Field("scope") String scope,
			@Field("grant_type") String grant_type);
}
