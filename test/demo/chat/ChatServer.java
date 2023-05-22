package demo.chat;

import java.io.IOException;
import java.util.Scanner;

import demo.chat.Server;

public class ChatServer {

	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start(Settings.PORT);
			System.out.println("Server is running, type 'exit' to stop it.");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String command = scanner.nextLine();
					if (command == null || "exit".equalsIgnoreCase(command.trim())) {
						break;
					}
				}
			}
			server.stop();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
