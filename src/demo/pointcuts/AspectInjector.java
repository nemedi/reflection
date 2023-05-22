package demo.pointcuts;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectInjector<M> implements InvocationHandler {
	
	private Object model;
	private Map<Method, Method> beforePointcuts;
	private Map<Method, Method> afterPointcuts;
	private Map<Method, Method> insteadPointcuts;
	private Map<Method, Method> throwingPointcuts;
	
	private AspectInjector(M model, Class<?> aspect) {
		this.model = model;
		this.beforePointcuts = new HashMap<Method, Method>();
		this.afterPointcuts = new HashMap<Method, Method>();
		this.insteadPointcuts = new HashMap<Method, Method>();
		this.throwingPointcuts = new HashMap<Method, Method>();
		Class<?> type = model.getClass();
		Map<String, Map<Method, Method>> pointcuts = new HashMap<String, Map<Method, Method>>();
		pointcuts.put("before", beforePointcuts);
		pointcuts.put("after", afterPointcuts);
		pointcuts.put("instead", insteadPointcuts);
		pointcuts.put("throwing", throwingPointcuts);
		for (Class<?> contract : type.getInterfaces()) {
			for (Method method : contract.getMethods()) {
				String name = method.getName();
				name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
				for (Method advice : aspect.getMethods()) {
					int modifiers = advice.getModifiers();
					if (!Modifier.isStatic(modifiers)) {
						continue;
					}
					for (String prefix : pointcuts.keySet()) {
						if (advice.getName().equals(prefix + name)) {
							pointcuts.get(prefix).put(method, advice);
							break;
						}
					}
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <M> M inject(M model, Class<?> aspect) {
		Class<?> type = model.getClass();
		AspectInjector<M> injector = new AspectInjector<M>(model, aspect);
		Object proxy = Proxy.newProxyInstance(type.getClassLoader(),
				type.getInterfaces(), injector);
		return (M) proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (beforePointcuts.containsKey(method)) {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(model);
			parameters.addAll(Arrays.asList(args));
			Method advice = beforePointcuts.get(method);
			advice.invoke(null, parameters.toArray(new Object[parameters.size()]));
		}
		Object result = null;
		try {
			if (insteadPointcuts.containsKey(method)) {
				List<Object> parameters = new ArrayList<Object>();
				parameters.add(model);
				parameters.addAll(Arrays.asList(args));
				Method advice = insteadPointcuts.get(method);
				result = advice.invoke(null, parameters.toArray(new Object[parameters.size()]));
			}
			else {
				result = method.invoke(model, args);
			}
		}
		catch (Exception e) {
			if (throwingPointcuts.containsKey(method)) {
				List<Object> parameters = new ArrayList<Object>();
				parameters.add(model);
				parameters.addAll(Arrays.asList(args));
				parameters.add(e);
				Method advice = throwingPointcuts.get(method);
				advice.invoke(null, parameters.toArray(new Object[parameters.size()]));
			}
			throw e;
		}
		finally {
			if (afterPointcuts.containsKey(method)) {
				List<Object> parameters = new ArrayList<Object>();
				parameters.add(model);
				parameters.addAll(Arrays.asList(args));
				parameters.add(result);
				Method advice = afterPointcuts.get(method);
				advice.invoke(null, parameters.toArray(new Object[parameters.size()]));
			}
		}
		return result;
	}
}
