package demo.singleton;

import java.lang.reflect.Constructor;

import demo.singleton.Singleton;

public class Program {

	public static void main(String[] args) {
		try {
			Singleton.getInstance();
			System.out.println("Singleton instance acquired.");
			Constructor<?> constructor = Singleton.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		} catch (Throwable e) {
			System.out.println(e.getCause().getLocalizedMessage());
		}
	}

}