package demo.remoting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import demo.formatter.json.JsonFormatter;

public class RpcSkeleton {

	private Class<?> type;
	private Object instance;
	private String session;

	public RpcSkeleton(Class<?> type)
			throws ReflectiveOperationException {
		this.type = type;
		Constructor<?> constructor = this.type.getDeclaredConstructor();
		boolean accessible = constructor.isAccessible();
		if (!accessible) {
			constructor.setAccessible(true);
		}
		this.instance = constructor.newInstance();
		if (!accessible) {
			constructor.setAccessible(false);
		}
		this.session = UUID.randomUUID().toString();
	}

	public RpcSkeleton(Object instance) {
		this.instance = instance;
		this.type = instance.getClass();
	}
	
	public String getSession() {
		return session;
	}

	public void process(RpcRequest request, RpcResponse response) {
		try {
			response.setSession(session);
			Method method = getMethod(request.getMethod());
			Class<?>[] parameterTypes = method.getParameterTypes();
			Object[] arguments = request.getArguments();
			for (int i = 0; i < arguments.length; i++)
				if (parameterTypes[i].isArray()) {
					arguments[i] = JsonFormatter.deserialize(arguments[i].toString(),
							parameterTypes[i]);
				}
				else {
					arguments[i] = JsonFormatter.deserialize(arguments[i].toString(),
							parameterTypes[i]);
				}
			Object result = method.invoke(this.instance, arguments);
			response.setResult(result);
		} catch (Exception e) {
			response.setFault(e.getLocalizedMessage());
		}
	}

	private Method getMethod(String method) {
		Method[] methods = type.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(method)) {
				return methods[i];
			}
		}
		return null;
	}
}
