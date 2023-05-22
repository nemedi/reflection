package demo.inheritance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InheritanceAdapter<T> implements InvocationHandler {
	
	private T model;
	private Object[] implementers;

	public InheritanceAdapter(Object...implementers) {
		this.implementers = implementers;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {
		if (implementers != null) {
			for (Object implementer : implementers) {
				try {
					method = implementer.getClass()
							.getDeclaredMethod(method.getName(), method.getParameterTypes());
					return method.invoke(implementer, arguments);
				} catch (Throwable t) {
					continue;
				}
			}
		}
		return method.invoke(model, arguments);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T combine(Class<T> type, Object...implementers) {
		List<Class<?>> types = new ArrayList<Class<?>>();
		types.add(type);
		for (Object implementer : implementers) {
			types.addAll(Arrays.asList(implementer.getClass().getInterfaces()));
		}
		InheritanceAdapter<T> handler = new InheritanceAdapter<T>(implementers);
		return (T) Proxy.newProxyInstance(InheritanceAdapter.class.getClassLoader(),
				types.toArray(new Class<?>[types.size()]), handler);
	}
}
