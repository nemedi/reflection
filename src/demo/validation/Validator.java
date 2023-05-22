package demo.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.regex.Pattern;

public class Validator<M> implements InvocationHandler {

	private M model;

	private Validator(M model) {
		this.model = model;
	}
	
	@SuppressWarnings("unchecked")
	public static <M> M adapt(M model) {
		Class<?> type = model.getClass();
		Validator<M> validator = new Validator<M>(model);
		Object proxy = Proxy.newProxyInstance(type.getClassLoader(),
				type.getInterfaces(),
				validator);
		return (M) proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method,
			Object[] args) throws Throwable {
		Parameter[] parameters = method.getParameters();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parameters.length; i++) {
			validate(parameters[i], args[i], builder);
		}
		if (builder.length() > 0) {
			throw new IllegalArgumentException(builder.toString());
		}
		return method.invoke(model, args);
	}
	
	private void validate(Parameter parameter, Object value, StringBuilder builder) {
		for (Annotation annotation : parameter.getAnnotations()) {
			if (annotation instanceof NotNull) {
				if (value == null) {
					builder.append("Parameter ")
						.append(parameter.getName())
						.append(" cannot be null. ");
				}
			}
			else if (annotation instanceof Regex) {
				String pattern = ((Regex) annotation).value();
				if (!Pattern.matches(pattern, value.toString())) {
					builder.append("Parameter ")
					.append(parameter.getName())
					.append(" with value ")
					.append(value)
					.append(" doesn't match pattern ")
					.append(pattern)
					.append(". ");
				}
			}
		}
	}
}
