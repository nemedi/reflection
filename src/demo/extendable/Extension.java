package demo.extendable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public interface Extension<T> {

	public class ExtensionHandler<T> implements InvocationHandler {

		private T instance;

		private ExtensionHandler(T instance) {
			this.instance = instance;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if ("base".equals(method.getName())
					&& method.getParameterCount() == 0) {
				return instance;
			} else {
				Class<?> type = method.getDeclaringClass();
				MethodHandles.Lookup lookup = MethodHandles.lookup()
					.in(type);
				Field allowedModesField = lookup.getClass().getDeclaredField("allowedModes");
				makeFieldModifiable(allowedModesField);
				allowedModesField.set(lookup, -1);
				return lookup
					.unreflectSpecial(method, type)
					.bindTo(proxy)
					.invokeWithArguments(args);
			}
		}

		private static void makeFieldModifiable(Field field) throws Exception {
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField
					.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		}

	}

	default T base() {
		return null;
	}

	static <E extends Extension<T>, T> E create(Class<E> type, T instance) {
		if (type.isInterface()) {
			ExtensionHandler<T> handler = new ExtensionHandler<T>(instance);
			List<Class<?>> interfaces = new ArrayList<Class<?>>();
			interfaces.add(type);
			Class<?> baseType = type.getSuperclass();
			while (baseType != null && baseType.isInterface()) {
				interfaces.add(baseType);
				baseType = baseType.getSuperclass();
			}
			Object proxy = Proxy.newProxyInstance(
					Extension.class.getClassLoader(),
					interfaces.toArray(new Class<?>[interfaces.size()]),
					handler);
			return type.cast(proxy);
		} else {
			return null;
		}
	}

}
