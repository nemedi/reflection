package demo.remoting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import demo.remoting.RpcServer;

public class Server {

	public static void main(String[] args) {
		try {
			RpcServer server = new RpcServer(8080);
			server.publish("service", Service.class);
			server.start();
			System.out.println("Server is listening, type 'exit' to stop it.");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String command = null;
				try {
					command = reader.readLine();
				} catch (Exception e) {
					break;
				}
				if (command == null || "exit".equalsIgnoreCase(command)) {
					break;
				}
			}
			server.stop();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
