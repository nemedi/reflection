package demo.rest;

import java.util.Scanner;

public class Program {

	public static void main(String[] args) {
		try {
			RestServer server = new RestServer();
			server.start(Settings.PORT, NoteResource.class);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
