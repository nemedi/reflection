package demo.mapping;

import java.lang.reflect.Method;

public class OrmFieldInvoker {
	
	public static <TItem> Object getValue(java.lang.reflect.Field field,
			TItem item) throws ReflectiveOperationException {
		Method method = OrmFieldInvoker.getMethod(item.getClass(), "get"
				+ OrmFieldInvoker.convertNameToPascalCase(field.getName()),
				new Class[] {});
		if (method != null)
			return method.invoke(item, new Object[] {});
		else {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			Object value = field.get(item);
			if (!accessible)
				field.setAccessible(false);
			return value;
		}
	}

	public static <TItem> void setValue(java.lang.reflect.Field field,
			TItem item, Object value) throws ReflectiveOperationException {
		Method method = OrmFieldInvoker.getMethod(item.getClass(), "set"
				+ OrmFieldInvoker.convertNameToPascalCase(field.getName()),
				new Class[] { field.getType() });
		if (method != null)
			method.invoke(item, new Object[] { value });
		else {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);
			field.set(item, value);
			if (!accessible)
				field.setAccessible(false);
		}
	}

	private static String convertNameToPascalCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private static Method getMethod(Class<?> type, String name,
			Class<?>[] parameterTypes) {
		Method method = null;
		try {
			method = type.getMethod(name, parameterTypes);
		} catch (ReflectiveOperationException exception) {
			method = null;
		}
		return method;
	}
	
}
