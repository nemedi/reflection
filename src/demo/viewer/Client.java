package demo.viewer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.swt.graphics.Image;

public class Client {

	public interface ClientListener {
		void onRefresh(Image image);
	}
	
	private ClientListener listener;
	private String remoteHost;
	private int remotePort;
	private int localPort;
	private DatagramSocket datagramSocket;
	
	public Client(ClientListener listener) {
		this.listener = listener;
	}
	
	public void connect(String remoteHost, int remotePort, int localPort) throws IOException {
		disconnect();
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.localPort = localPort;
		InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), localPort);
		datagramSocket = new DatagramSocket(address);		
		ClientWorker worker = new ClientWorker();
		new Thread(worker).start();
		register(true);
	}
	
	public void disconnect() throws IOException {
		if (datagramSocket != null && !datagramSocket.isClosed()) {
			register(false);
			datagramSocket.close();
		}
		datagramSocket = null;
	}
	
	private void register(boolean registered) throws IOException {
		Request request = new Request(registered? Request.ADD : Request.REMOVE,
				InetAddress.getLocalHost(), localPort);
		InetSocketAddress address = new InetSocketAddress(remoteHost, remotePort);
		Transport.send(request, new DatagramSocket(), address);
	}

	private class ClientWorker implements Runnable {
	
		@Override
		public void run() {
			while (datagramSocket != null && !datagramSocket.isClosed()) {
				try {
					Response response = Transport.receive(datagramSocket);
					listener.onRefresh(response.getImage());
				} catch (IOException | ClassNotFoundException e) {
					continue;
				}
			}
			
		}
		
	}
	
}
