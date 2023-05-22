package demo.dependency;

import demo.dependency.Inject;

public class Client {

	@Inject
	private Service service;
	
	public void doSomething() {
		this.service.doSomething();
	}
}
