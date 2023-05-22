package demo.formatter.json;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JsonFormatter {

	public static String serialize(Object object) throws JsonFormatterException {
		if (object == null) {
			return "\"null\"";
		}
		if (object instanceof List) {
			object = ((List<?>) object).toArray();
		}
		Class<?> type = object.getClass();
		if (isPrimitive(type)) {
			return object.toString();
		} else if (String.class.equals(type)) {
			return "\"" + ((String) object).replaceAll("\"", "") + "\"";
		} else if (Date.class.equals(type)) {
			return "new Date(" + ((Date) object).getTime() + ")";
		} else if (type.isArray()) {
			return serializeArray(object);
		} else {
			return serializeObject(object);
		}
	}

	private static String serializeObject(Object object) throws JsonFormatterException {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers)
					|| Modifier.isFinal(modifiers)
					|| Modifier.isTransient(modifiers)) {
				continue;
			}
			if (builder.length() > 1) {
				builder.append(", ");
			}
			builder.append("\"" + field.getName() + "\"").append(" : ").append(serializeField(field, object));
		}
		builder.append("}");
		return builder.toString();
	}

	private static String serializeField(Field field, Object object) throws JsonFormatterException {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			Object value = field.get(object);
			value = serialize(value);
			if (!accessible) {
				field.setAccessible(false);
			}
			return (String) value;
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new JsonFormatterException(e);
		}
	}

	private static String serializeArray(Object object) throws JsonFormatterException {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < Array.getLength(object); i++) {
			if (builder.length() > 1) {
				builder.append(", ");
			}
			builder.append(serialize(Array.get(object, i)));
		}
		builder.append("]");
		return builder.toString();
	}
	
	private static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive()
				|| Boolean.class.equals(type)
				|| Byte.class.equals(type)
				|| Short.class.equals(type)
				|| Integer.class.equals(type)
				|| Long.class.equals(type)
				|| Float.class.equals(type)
				|| Double.class.equals(type)
				|| Character.class.equals(type);
	}
	
	private static boolean isValueType(Class<?> type) {
		return isPrimitive(type)
				|| String.class.equals(type)
				|| Date.class.equals(type)
				|| Object.class.equals(type);
	}

	public static <T> T deserialize(String data, Class<T> type)
			throws JsonFormatterException {
		if (type.isArray()) {
			return deserializeArray(data, type);
		} else if (isValueType(type)) {
			return deserializeValue(data, type);
		} else {
			return deserializeObject(data, type);
		}
	}

	private static <T> T deserializeObject(String data, Class<T> type)
			throws JsonFormatterException {
		try {
			Map<String, String> parts = splitObject(data);
			Constructor<T> constructor = type.getDeclaredConstructor();
			boolean accessible = constructor.isAccessible();
			if (!accessible) {
				constructor.setAccessible(true);
			}
			T object = constructor.newInstance();
			if (!accessible) {
				constructor.setAccessible(false);
			}
			for (Entry<String, String> entry : parts.entrySet()) {
				String name = entry.getKey();
				Field field = type.getDeclaredField(name);
				accessible = field.isAccessible();
				if (!accessible) {
					field.setAccessible(true);
				}
				Object value = field.getType().isArray() ? deserializeArray(entry.getValue(), field.getType())
						: deserialize(entry.getValue(), field.getType());
				field.set(object, value);
				if (!accessible) {
					field.setAccessible(false);
				}
			}
			return object;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			throw new JsonFormatterException(e);
		}
	}

	private static <T> T deserializeArray(String data, Class<T> type)
			throws JsonFormatterException {
		String[] parts = splitArray(data);
		Object array = Array.newInstance(type.getComponentType(), parts.length);
		for (int i = 0; i < parts.length; i++) {
			Object value = null;
			if (type.getComponentType().isArray()) {
				value = deserializeArray(parts[i], type.getComponentType());
			} else {
				value = deserialize(parts[i], type.getComponentType());
			}
			Array.set(array, i, value);
		}
		return type.cast(array);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T deserializeValue(String data, Class<T> type) throws JsonFormatterException {
		if (data.startsWith("\"") && data.endsWith("\"")) {
			data = data.substring(1, data.length() - 1);
		}
		return (T) parseValue(data, type);
	}
	
	private static Object parseValue(String data, Class<?> type) throws JsonFormatterException {
		try {
			if (Object.class.equals(type) || String.class.equals(type)) {
				return "null".equals(data) ? null : type.cast(data);
			}
			else if (Character.class.equals(type)) {
				return data.charAt(0);
			}
			else if (Date.class.equals(type)) {
				int start = data.indexOf("(");
				int end = data.lastIndexOf(")");
				if (start > 0 && end > start){
					return new Date(Long.parseLong(
							data.substring(start + "(".length(), end)));
				}
				else {
					return DateFormat.getDateInstance(DateFormat.SHORT).parse(data);
				}
			}
			else {
				if (boolean.class.equals(type)) {
					type = Boolean.class;
				}
				else if (byte.class.equals(type)) {
					type = Byte.class;
				}
				else if (short.class.equals(type)) {
					type = Short.class;
				}
				else if (int.class.equals(type)) {
					type = Integer.class;
				}
				else if (long.class.equals(type)) {
					type = Long.class;
				}
				else if (float.class.equals(type)) {
					type = Float.class;
				}
				else if (double.class.equals(type)) {
					type = Double.class;
				}
				Constructor<?> constructor = type.getConstructor(String.class);
				return constructor.newInstance(data);
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | ParseException e) {
			throw new JsonFormatterException(e);
		}
	}

	private static Map<String, String> splitObject(String data) {
		Map<String, String> items = new HashMap<String, String>();
		if (data.startsWith("{") && data.endsWith("}")) {
			data = data.substring(1, data.length() - 1);
		}
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
			String remaining = data.substring(startIndex, data.length()).trim();
			collectKeyValuePair(remaining, items);
		}
		return items;

	}

	private static String[] splitArray(String data) {
		if (data.startsWith("[") && data.endsWith("]")) {
			data = data.substring(1, data.length() - 1);
		}
		List<String> parts = new ArrayList<String>();
		int startIndex = 0;
		int level = 0;
		boolean outside = true;
		for (int i = 0; i < data.length(); i++) {
			switch (data.charAt(i)) {
			case ',':
				if (level == 0) {
					parts.add(data.substring(startIndex, i).trim());
					startIndex = i + 1;
				}
				break;
			case '{':
			case '[':
				if (outside) {
					level++;
				}
				break;
			case '}':
			case ']':
				if (outside) {
					level--;
				}
				break;
			case '"':
				outside = !outside;
			}
		}
		if (startIndex < data.length()) {
			parts.add(data.substring(startIndex, data.length()).trim());
		}
		return parts.toArray(new String[parts.size()]);
	}

	private static void collectKeyValuePair(String data, Map<String, String> items) {
		int index = data.indexOf(':');
		if (index == -1) {
			return;
		}
		String key = data.substring(0, index).trim();
		String value = data.substring(index + 1).trim();
		if (key.startsWith("\"") && key.endsWith("\"")) {
			key = key.substring(1, key.length() - 1);
		}
		items.put(key, value);
	}

}
