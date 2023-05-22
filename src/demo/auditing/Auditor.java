package demo.auditing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Auditor<T> implements InvocationHandler {

	private T model;

	private Auditor(T model) {
		this.model = model;
	}

	@SuppressWarnings("unchecked")
	public static <T> T adapt(T model) {
		Class<?> type = model.getClass();
		Auditor<T> auditor = new Auditor<T>(model);
		return (T) Proxy.newProxyInstance(type.getClassLoader(),
				type.getInterfaces(), auditor);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getAnnotation(Auditable.class) != null) {
			StringBuilder builder = new StringBuilder();
			String timestamp = new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SSS")
				.format(new Date());
			String userName = System.getProperty("user.name");
			String typeName = method.getDeclaringClass().getName();
			String methodName = method.getName();
			builder
				.append(timestamp)
				.append("\t")
				.append(userName)
				.append("\t")
				.append(typeName)
				.append("\t")
				.append(methodName);
			System.out.println(builder.toString());
		}
		return method.invoke(model, args);
	}
}
