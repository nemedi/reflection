package demo.tasks;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import demo.tasks.TaskManager;
import demo.tasks.TaskManagerRegistry;

public class Server {

	public static void main(String[] args) {
		try {
			TaskManagerRegistry registry = new TaskManagerRegistry();
			registry.start(8080);
			registry.publish("shell", new TaskManager<List<String>, String>());
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String line = scanner.nextLine();
					if (line == null || "exit".equalsIgnoreCase(line)) {
						break;
					}
				}
			}
		} catch (RemoteException | MalformedURLException | UnknownHostException e) {
			System.out.println(e.getMessage());
		}
		
	}

}
