import java.io.IOException;
import java.net.ServerSocket;

/*
 * @author kejmil01, @date 8/1/16
 */
public class PortHolder {

	ServerSocket serverSocket = null;

	public void reserveFreeRandomPort() throws IOException {
		freePort();
		serverSocket = new ServerSocket(0);
	}

	public int getPort() {
		if (serverSocket == null) {
			return 0;
		}
		return serverSocket.getLocalPort();
	}

	public void freePort() throws IOException {
		if (serverSocket != null) {
			serverSocket.close();
			serverSocket = null;
		}
	}
}
