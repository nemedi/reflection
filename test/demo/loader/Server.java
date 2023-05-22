package demo.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import demo.loader.ClassService;
import demo.loader.RpcClassLoader;
import demo.remoting.RpcServer;

public class Server {

	public static void main(String[] args) {
		try {
			ClassService.setDirectory("E:/Temp");
			RpcServer server = new RpcServer(8080);
			RpcClassLoader.registerClassService(server);
			server.start();
			System.out.println("Server is listening, type 'exit' to stop it.");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String command = reader.readLine();
				if ("exit".equalsIgnoreCase(command)) {
					break;
				}
			}
			server.stop();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
