package demo.singleton;

public class Singleton {

	private static final Byte TOKEN = 0;

	private static Singleton instance;

	private Singleton() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (!Singleton.class.getName().equals(stackTraceElements[2].getClassName())
				|| !"getInstance".equals(stackTraceElements[2].getMethodName())) {
			throw new InstantiationError("Singleton class cannot be instantiated directly.");
		}
	}

	public static Singleton getInstance() {
		if (instance == null) {
			synchronized (TOKEN) {
				if (instance == null) {
					instance = new Singleton();
				}
			}
		}
		return instance;
	}

}
