package demo.remoting;

import demo.remoting.RpcProxy;

public class Client {

	public static void main(String[] args) {
		Contract service = RpcProxy.create(Contract.class, "tcp://localhost:8080/service");
		String message = service.format("{0} {1} {2}{3}", "Hello", "from", "server", "!");
		System.out.println(message);
	}

}
