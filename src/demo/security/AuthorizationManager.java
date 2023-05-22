package demo.security;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AuthorizationManager<M> implements InvocationHandler {
	
	private static IAuthorizationProvider provider;
	
	private M model;

	public AuthorizationManager(M model) {
		this.model = model;
	}
	
	public static void setProvider(IAuthorizationProvider provider) {
		AuthorizationManager.provider = provider;
	}

	@SuppressWarnings("unchecked")
	public static <M> M adapt(M model) {
		Class<?> type = model.getClass();
		AuthorizationManager<M> manager = new AuthorizationManager<M>(model);
		return (M) Proxy.newProxyInstance(type.getClassLoader(),
				type.getInterfaces(), manager);
	}

	@Override
	public Object invoke(Object proxy, Method method,
			Object[] args) throws Throwable {
		
		RequiresToBe annotation = method.getAnnotation(RequiresToBe.class);
		
		if (annotation != null && annotation.value() != null) {
			String[] roles = annotation.value();
			String user = System.getProperty("user.name");
			boolean hasRole = false;
			for (String role : roles) {
				if (provider.hasRole(user, role)) {
					hasRole = true;
					break;
				}
			}
			if (!hasRole) {
				throw new IllegalAccessException(method.getName());
			}
		}
		return method.invoke(model, args);
	}
}
