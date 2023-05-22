package demo.remoting;

public class RpcResponse {

	private Object result;
	private String fault;
	private String session;
	
	public RpcResponse() {
	}
	
	public Object getResult() {
		return this.result;
	}
	
	public void setResult(Object result) {
		this.result = result;
	}

	public String getFault() {
		return this.fault;
	}
	
	public void setFault(String fault) {
		this.fault = fault;
	}

	public String getSession() {
		return session;
	}
	
	public void setSession(String session) {
		this.session = session;
	}

}
