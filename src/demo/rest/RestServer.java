package demo.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class RestServer {
	
	private HttpServer server;

	public void start(int port, Class<?>...types) throws Exception {
		System.setErr(new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				//System.out.write(b);
			}
		}));
		InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
		server = HttpServer.create(address, 0);
		server.createContext("/", new RestHandler(types));
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}
	
	public void stop() {
		if (server != null) {
			server.stop(0);
			server = null;
		}
	}
	
}
