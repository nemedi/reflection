package demo.auditing;

import demo.auditing.Auditor;

public class Program {

	public static void main(String[] args) {
		IService service = new Service();
		service = Auditor.adapt(service);
		service.doSomething();
	}
}
