package demo.remoting;

public class RpcRequest {
	
	public static final String DESTROY_METHOD = ".destroy";
	
	private String service;
	private String method;
	private Object[] arguments;
	private String session;
	
	private RpcRequest() {
	}
	
	public RpcRequest(String service, String method, Object[] arguments, String session) {
		this();
		this.service = service;
		this.method = method;
		this.arguments = arguments;
		this.session = session;
	}

	public String getService() {
		return this.service;
	}

	public String getMethod() {
		return this.method;
	}

	public Object[] getArguments() {
		return this.arguments;
	}
	
	public String getSession() {
		return session;
	}

}
