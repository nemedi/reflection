package demo.security;

import demo.security.IAuthorizationProvider;

public class AuthorizationProvider implements IAuthorizationProvider {

	@Override
	public boolean hasRole(String user, String role) {
		if ("administrator".equalsIgnoreCase(role)) {
			return "Iulian".equalsIgnoreCase(user);
		}
		else {
			return false;
		}
	}

}
