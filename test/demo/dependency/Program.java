package demo.dependency;

import demo.dependency.Injector;
import demo.dependency.Module;

public class Program {

	public static void main(String[] args) {
		try {
			Module module = new MyModule();
			Injector injector = new Injector(module);
			Client client = injector.getInstance(Client.class);
			client.doSomething();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
