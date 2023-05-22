package demo.remoting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import demo.formatter.json.JsonFormatter;

public class RpcServer implements Runnable {
	
	private int port;
	private ServerSocket socket;
	private Map<String, RpcService> services;
	
	public RpcServer(int port) {
		this.port = port;
		this.services = new HashMap<String, RpcService>();
	}
	
	public void publish(String service, Class<?> type) {
		if (service != null
				&& type != null
				&& !services.containsKey(service)) {
			services.put(service, new RpcService(type));
		}
	}
	
	public void publish(String service, Object instance) {
		if (service != null
				&& instance != null
				&& !services.containsKey(service)) {
			services.put(service, new RpcService(instance));
		}
	}
	
	public void unpublish(String service) {
		if (service != null && services.containsKey(service)) {
			services.remove(service);
		}
	}
	
	public void start() throws IOException {
		stop();
		new Thread(this).start();
	}
	
	public void stop() throws IOException {
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		stop();
	}
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(port);
			while (!socket.isClosed()) {
				process(socket.accept());
				Thread.sleep(200);
			}
		}
		catch (IOException | InterruptedException e) {
		}
	}
	
	private void process(final Socket socket) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				try {
					BufferedReader reader =
							new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter writer = new PrintWriter(socket.getOutputStream());
					while (!socket.isClosed()) {
						String data = reader.readLine();
						if (data == null || data.isEmpty()) {
							break;
						}
						RpcRequest request = JsonFormatter.deserialize(data, RpcRequest.class);
						RpcResponse response = new RpcResponse();
						String service = request.getService();
						if (services.containsKey(service)) {
							services.get(service).process(request, response);
						}
						else {
							response.setFault("Service unavailable.");
						}
						data = JsonFormatter.serialize(response);
						writer.println(data);
						writer.flush();
						Thread.sleep(200);
					}
				} catch (Exception e) {
				}
			}
		};
		
		new Thread(runnable).start();
	}
}
