package demo.logging;

import demo.logging.Log;

public class Service implements Runnable {
	
	private volatile boolean running;

	public Service() {
		Log.info("Service was created.");
	}
	
	public void setup() {
		Log.info("Service was setup.");
	}
	
	public void start() {
		new Thread(this).start();
		Log.info("Service is starting.");
	}
	
	public void run() {
		Log.info("Service was started.");
		running = true;
		while (running) {
			Log.info("Service is running.");
		}
		Log.info("Service was stopped.");
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void stop() {
		running = false;
		Log.info("Service is stopping.");
	}
	
	@Override
	protected void finalize() throws Throwable {
		Log.info("Service was destroyed.");
	}
}
