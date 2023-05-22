package demo.formatter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.PrimitiveTypesUtility;

public class JsonFormatter {
	
	public static String serialize(Object object)
			throws JsonFormatterException {
		if (object == null)
			return "null";
		Class<?> type = object.getClass();
		if (String.class.equals(type)) {
			return "\""
					+ ((String) object).replaceAll("\"", "")
					+ "\"";
		}
		else if (Date.class.equals(type)) {
			return "new Date(" + ((Date) object).getTime() + ")";
		}
		else if (isPrimitive(type)) {
			return object.toString();
		}
		else if (type.isArray()) {
			return serializeArray(object);
		}
		else {
			StringBuilder builder = new StringBuilder("{");
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers)
						|| Modifier.isFinal(modifiers)) {
					continue;
				}
				if (builder.length() > 1) {
					builder.append(", ");
				}
				builder.append("\""
						+ field.getName()
						+ "\""
						+ " : "
						+ serializeField(field, object));
			}
			builder.append("}");
			return builder.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String data, Class<T> type)
			throws JsonFormatterException {
		if (data.startsWith("[")
				&& data.endsWith("]")) {
			return (T) deserializeArray(data, type);
		}
		else {
			return (T) deserializeObject(type, data);
		}
	}

	private static String serializeField(Field field, Object object)
			throws JsonFormatterException {
		try {
			if (object == null) {
				return "null";
			}
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			Object fieldValue = field.get(object);
			if (!accessible) {
				field.setAccessible(false);
			}
			if (fieldValue == null) {
				return "null";
			}
			if (String.class.equals(field.getType())) {
				return "\""
						+ ((String) fieldValue).replaceAll("\"", "\\\"")
						+ "\"";
			}
			else if (Date.class.equals(field.getType())) {
				return "new Date("
						+ ((Date) fieldValue).getTime()
						+ ")";
			}
			else if (isPrimitive(field.getType())) {
				return fieldValue.toString();
			}
			else {
				return serialize(fieldValue);
			}
		} catch (ReflectiveOperationException e) {
			throw new JsonFormatterException(e);
		}
	}

	private static String serializeArray(Object obj)
			throws JsonFormatterException {
		if (obj == null) {
			return "null";
		}
		Class<?> type = obj.getClass();
		if (type.isArray()) {
			StringBuilder builder = new StringBuilder("[");
			for (int i = 0; i < Array.getLength(obj); i++) {
				if (builder.length() > 1) {
					builder.append(", ");
				}
				builder.append(serialize(Array.get(obj, i)));
			}
			builder.append("]");
			return builder.toString();
		}
		else {
			return "null";
		}
	}

	private static Object deserializeObject(Class<?> type, String data)
			throws JsonFormatterException {
		try {
			data = data.trim();
			Object instance = null;
			if (Object.class.equals(type)) {
				return data;
			}
			else if (data.startsWith("{")
					&& data.endsWith("}")) {
				Map<String, String> items = splitObject(data);
				Constructor<?> constructor = type.getDeclaredConstructor();
				boolean accessible = constructor.isAccessible();
				if (!accessible) {
					constructor.setAccessible(true);
				}
				instance = constructor.newInstance();
				if (!accessible) {
					constructor.setAccessible(false);
				}
				for (String key : items.keySet()) {
					Field field = type.getDeclaredField(key);
					int modifiers = field.getModifiers();
					if (Modifier.isStatic(modifiers)
							|| Modifier.isFinal(modifiers)) {
						continue;
					}
					accessible = field.isAccessible();
					if (!accessible)
						field.setAccessible(true);
					if (field.getType().isArray()) {
						Object value = deserializeArray(items.get(key),
								field.getType().getComponentType());
						if (Array.getLength(value) == 0) {
							value = Array.newInstance(field.getType().getComponentType(), 0);
						}
						field.set(instance, value);
					} else
						field.set(instance,
								deserializeObject(field.getType(),
										items.get(key)));
					if (!accessible)
						field.setAccessible(false);
				}
			} else {
				String dataWithoutDelimiters = JsonFormatter
						.removeStringDelimiters(data);
				instance = PrimitiveTypesUtility.parseValue(dataWithoutDelimiters, type);
			}
			return instance;
		} catch (ReflectiveOperationException
				| ParseException e) {
			throw new JsonFormatterException(e);
		}
	}

	private static Object[] deserializeArray(String data, Class<?> type)
			throws JsonFormatterException {
		data = data.trim();
		List<Object> instances = new ArrayList<Object>();
		if (data.startsWith("[") && data.endsWith("]")) {
			String[] items = splitArray(data);
			for (String item : items) {
				if (type.isArray()) {
					instances.add(deserializeArray(item, type));
				}
				else {
					instances.add(deserializeObject(type, item));
				}
			}
		}
		return instances.toArray((Object[]) Array.newInstance(type, instances
				.size()));
	}

	private static Map<String, String> splitObject(String data) {
		if (data.startsWith("{")
				&& data.endsWith("}")) {
			Map<String, String> items = new HashMap<String, String>();
			data = data.substring(1, data.length() - 1);
			int startIndex = 0;
			int level = 0;
			int direction = 1;
			for (int i = 0; i < data.length(); i++) {
				switch (data.charAt(i)) {
				case ',':
					if (level == 0) {
						String substring = data.substring(startIndex, i).trim();
						collectKeyValuePair(substring, items);
						startIndex = i + 1;
					}
					break;
				case '{':
				case '[':
					level++;
					break;
				case '}':
				case ']':
					level--;
					break;
				case '\"':
					if (i == 0 || data.charAt(i - 1) != '\\') {
						level += direction;
						direction = -direction;
					}
					break;
				}
			}
			if (startIndex < data.length()) {
				String remaining = data.substring(startIndex, data.length())
						.trim();
				collectKeyValuePair(remaining, items);
			}
			return items;
		}
		else {
			return null;
		}
	}
	
	private static void collectKeyValuePair(String data, Map<String, String> items) {
		int index = data.indexOf(':');
		if (index == -1) {
			return;
		}
		String key = data.substring(0, index).trim();
		String value = data.substring(index + 1).trim();
		if (key.startsWith("\"")
			&& key.endsWith("\"")) {
			key = key.substring(1, key.length() - 1);
		}
		items.put(key, value);
	}

	private static String[] splitArray(String data) {
		if (data.startsWith("[")
				&& data.endsWith("]")) {
			List<String> items = new ArrayList<String>();
			data = data.substring(1, data.length() - 1);
			int startIndex = 0;
			int level = 0;
			for (int i = 0; i < data.length(); i++) {
				switch (data.charAt(i)) {
				case ',':
					if (level == 0) {
						items.add(data.substring(startIndex, i).trim());
						startIndex = i + 1;
					}
					break;
				case '{':
				case '[':
					level++;
					break;
				case '}':
				case ']':
					level--;
					break;
				}
			}
			if (startIndex < data.length()) {
				items.add(data.substring(startIndex, data.length()).trim());
			}
			return items.toArray(new String[items.size()]);
		}
		else {
			return null;
		}
	}

	private static String removeStringDelimiters(String data) {
		if (data.startsWith("\"")) {
			data = data.substring(1);
		}
		if (data.endsWith("\"")) {
			data = data.substring(0, data.length() - 1);
		}
		return data.replaceAll("\\\\", "");
	}
	
	private static boolean isPrimitive(Class<?> type) {
		Class<?>[] primitiveTypes = new Class<?>[] {
				Boolean.class,
				Character.class,
				Byte.class,
				Short.class,
				Integer.class,
				Long.class,
				Float.class,
				Double.class
			};
		return type.isPrimitive()
				|| Arrays.asList(primitiveTypes).contains(type);
	}
}
