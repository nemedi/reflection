package demo.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

import demo.PrimitiveTypesUtility;
import demo.setter.FieldSetter;

public class Properties {

	protected static void initialize() {
		try {
			Class<?> type = getType();
			ResourceBundle resourceBundle =
					ResourceBundle.getBundle(type.getName());
			for (Field field : type.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (!Modifier.isPublic(modifiers)
						|| !Modifier.isStatic(modifiers)
						|| !resourceBundle.containsKey(field.getName()))
					continue;
				String text = resourceBundle.getString(field.getName());
				if (text == null)
					continue;
				Object value = PrimitiveTypesUtility.parseValue(text, field.getType());
				FieldSetter.set(field, null, value);
			}
		} catch (Exception e) {
		}
	}

	private static Class<?> getType()
			throws ReflectiveOperationException {
		String type = Properties.class.getName();
		String method = "initialize";
		StackTraceElement[] stackTraceElements =
				Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTraceElements.length - 1; i++) {
			if (type.equals(stackTraceElements[i].getClassName())
					&& method.equals(stackTraceElements[i].getMethodName())) {
				if (type.equals(stackTraceElements[i + 1].getClassName())) {
					method = stackTraceElements[i + 1].getMethodName();
				}
				else {
					return Class.forName(stackTraceElements[i + 1].getClassName());
				}
			}
		}
		return null;
	}

}
