package demo.setter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldSetter {
	
	public static void set(String fieldName, Object instance, Object value)
			throws ReflectiveOperationException {
		if (instance == null) {
			return;
		}
		Class<?> type = instance.getClass();
		Field field = type.getDeclaredField(fieldName);
		set(field, instance, value);
	}
	
	public static void set(String fieldName, Class<?> type, Object value)
			throws ReflectiveOperationException {
		if (type == null) {
			return;
		}
		Field field = type.getDeclaredField(fieldName);
		set(field, null, value);
	}
	
	public static void set(Field field,
			Object instance,
			Object value)
			throws ReflectiveOperationException {
		Field modifiersField = field.getClass().getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(field);
		modifiers = modifiers & ~Modifier.FINAL;
		modifiersField.set(field, modifiers);
		modifiersField.setAccessible(false);
		boolean accessible = field.isAccessible();
		if (!accessible) {
			field.setAccessible(true);
		}
		if (value instanceof String) {
			value = ((String) value).toCharArray();
		}
		field.set(instance, value);
		if (!accessible) {
			field.setAccessible(false);
		}
	}
}
