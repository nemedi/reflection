package demo.remoting;

public class RpcException extends Exception {
	
	protected static final long serialVersionUID = 1;

	public RpcException(String message) {
		super(message);
	}

	public RpcException(Exception e) {
		super(e);
	}
}
