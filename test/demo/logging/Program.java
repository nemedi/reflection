package demo.logging;

import demo.logging.Log;

public class Program {

	public static void main(String[] args) {
		Log.info("Application has started.");
		Service service = new Service();
		service.setup();
		service.start();
		while (!service.isRunning()) {
		}
		service.stop();
		Log.info("Application has finished.");
	}

}
