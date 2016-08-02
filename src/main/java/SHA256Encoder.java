import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * @author kejmil01, @date 8/1/16
 */
public class SHA256Encoder {
	
	public static byte[] encode(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");

		md.update(text.getBytes("ASCII"));
		byte[] digest = md.digest();
		return digest;
	}

}
