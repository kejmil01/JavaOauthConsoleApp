
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;

/*
 * @author kejmil01, @date 8/1/16
 */
public class Base64Helper {

	public static String generateRandomNoPaddingBase64(int length) {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		return encodeNoPadding(bytes);
	}

	public static String encodeNoPadding(byte[] bytes) {
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
	}

}
