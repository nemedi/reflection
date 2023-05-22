package demo.pointcuts;

import demo.pointcuts.AspectInjector;

public class Program {

	public static void main(String[] args) {
		try {
			IService service = new Service();
			service = AspectInjector.inject(service, ServiceAspect.class);
			String result = service.sayHello(null);
			System.out.println(result);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
