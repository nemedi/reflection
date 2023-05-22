package demo.viewer;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Request implements Serializable {
	
	public static final int ADD = 1;
	public static final int REMOVE = 2;

	private static final long serialVersionUID = 1L;
	private int type;
	private String host;
	private int port;
	private transient InetSocketAddress address;
	
	public Request(int type, InetAddress address, int port) {
		this.type = type;
		this.host = address.toString();
		this.port = port;
	}
	
	public int getType() {
		return type;
	}
	
	public InetSocketAddress getAddress() {
		if (address == null) {
			address = new InetSocketAddress(host, port);
		}
		return address;
	}
}
