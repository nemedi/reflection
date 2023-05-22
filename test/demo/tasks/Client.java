package demo.tasks;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import demo.tasks.ITaskManager;

public class Client {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			ITaskManager<List<String>, String> manager =
					(ITaskManager<List<String>, String>) Naming.lookup("rmi://192.168.100.4:8080/shell");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					try {
						String command = scanner.nextLine();
						if (command == null || "exit".equalsIgnoreCase(command)) {
							break;
						}
						List<String> lines = manager.execute(new Task(), command.split("\\|"));
						if (lines != null) {
							for (String line : lines) {
								System.out.println(line);
							}
						}
					} catch (RemoteException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println(e.getMessage());
		}

	}

}
