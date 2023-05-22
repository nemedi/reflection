package demo.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import demo.tasks.ITask;

public class Task implements ITask<List<String>, String>, Runnable {

	private static final long serialVersionUID = 1L;
	private List<String> lines;
	private BufferedReader reader;

	@Override
	public List<String> execute(String... arguments) throws Exception {
		for (String argument : arguments) {
			ProcessBuilder builder = new ProcessBuilder(argument.trim().split("\\s+"));
			Process process = builder.start();
			if (lines != null) {
				try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
					for (String line : lines) {
						writer.println(line);
					}
				}
			}
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Thread thread = new Thread(this);
			thread.start();
			thread.join();
			process.waitFor();
		}
		return lines;
	}

	@Override
	public void run() {
		lines = new ArrayList<String>();
		while (true) {
			try {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				lines.add(line);
			} catch (IOException e) {
				break;
			}
		}
	}

}
