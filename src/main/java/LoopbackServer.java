import spark.Spark;

/*
 * @author kejmil01, @date 8/1/16
 */
public class LoopbackServer  {
	
	private ServerListener listener;
	private int port;
	private String ip;
	
	LoopbackServer(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}
	
	public void setListener(ServerListener listener)
	{
		this.listener = listener;
	}
	
	public void run()
	{
		Spark.ipAddress(ip);
		Spark.port(port);
		Spark.get("/", (req, res) -> {
			String code = req.queryMap("code").value();
			String incoming_state = req.queryMap("state").value();
			if(listener != null)
			{
				listener.onResponse(code, incoming_state);
			}
			return "TODO: write some fancy response";
        });
	}
	
	public interface ServerListener
	{
		public void onResponse(String code, String incoming_state);
	}
}
