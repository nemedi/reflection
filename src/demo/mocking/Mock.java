package demo.mocking;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import demo.PrimitiveTypesUtility;

public class Mock<T> implements InvocationHandler {
	
	private class ArgumentsKey {
		
		private Object[] arguments;

		private ArgumentsKey(Object[] arguments) {
			this.arguments = arguments;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object object) {
			if (!(object instanceof Mock.ArgumentsKey)) {
				return false;
			}
			ArgumentsKey argumentsKey = (ArgumentsKey) object;
			if (arguments == null && argumentsKey.arguments != null
					|| arguments != null && argumentsKey.arguments == null
					|| arguments.length != argumentsKey.arguments.length) {
				return false;
			}
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i] != null
						&& !arguments[i].equals(argumentsKey.arguments[i])) {
					return false;
				}
			}
			return true;
		}
		
	}

	private Object[] arguments;
	private Method method;
	private Map<Method, Map<ArgumentsKey, MockResult<?>>> results;
	
	private Mock() {
		results = new HashMap<Method, Map<ArgumentsKey, MockResult<?>>>();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T mock(Class<T> type) {
		Class<?>[] interfaces = type.isInterface() ?
				new Class<?>[] {type} : type.getInterfaces();
		Mock<T> mock = new Mock<T>();
		return (T) Proxy.newProxyInstance(type.getClassLoader(),
				interfaces, mock);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Mock<T> with(T proxy) {
		Mock<T> mock = (Mock<T>) Proxy.getInvocationHandler(proxy);
		mock.arguments = new Object[] {};
		return mock;
	}
	
	public <V> MockResultBinding<T, V> when(V context) {
		return new MockResultBinding<T, V>(this);
	}
	 
	public static <V> V any(Class<V> type) {
		return null;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {
		if (this.arguments != null) {
			this.arguments = arguments;
			this.method = method;
			return PrimitiveTypesUtility.getDefaultValue(method.getReturnType());
		}
		ArgumentsKey newArgumentsKey = new ArgumentsKey(arguments);
		ArgumentsKey argumentsKey = null;
		for (ArgumentsKey existingArgumentsKey : results.get(method).keySet()) {
			if (existingArgumentsKey.equals(newArgumentsKey)) {
				argumentsKey = existingArgumentsKey;
				break;
			}
		}
		if (argumentsKey != null) {
			MockResult<?> result = results.get(method).get(argumentsKey);
			if (result.getMethod() != null) {
				return result.getMethod().invoke(arguments);
			}
			else {
				return result.getValue();
			}
		}
		else {
			throw new UnsupportedOperationException(method.getName());
		}
	}
	
	<V> void bindResult(MockResult<V> result) {
		if (method != null && arguments != null) {
			if (!results.containsKey(method)) {
				results.put(method, new HashMap<ArgumentsKey, MockResult<?>>());
			}
			ArgumentsKey argumentsKey = new ArgumentsKey(arguments);
			results.get(method).put(argumentsKey, result);
			method = null;
			arguments = null;
		}
	}
	
}
