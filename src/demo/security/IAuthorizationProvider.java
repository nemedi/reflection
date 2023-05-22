package demo.security;

public interface IAuthorizationProvider {

	boolean hasRole(String user, String role);
}
