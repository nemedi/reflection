package demo.security;

import demo.security.AuthorizationManager;

public class Program {

	public static void main(String[] args) {
		AuthorizationManager.setProvider(new AuthorizationProvider());
		IService service = new Service();
		service = AuthorizationManager.adapt(service);
		service.execute();
	}

}
