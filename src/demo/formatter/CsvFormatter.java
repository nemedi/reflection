package demo.formatter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import demo.PrimitiveTypesUtility;

public class CsvFormatter {

	@SuppressWarnings("unchecked")
	public static <T> T[] deserialize(InputStream stream, Class<T> type)
			throws CsvFormatterException {
		try {
			List<T> items = new ArrayList<T>();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream))) {
				String line = reader.readLine();
				String[] fieldNames = line.split(",");
				Field[] fields = new Field[fieldNames.length];
				boolean[] accessibles = new boolean[fields.length];
				for (int i = 0; i < fieldNames.length; i++) {
					fieldNames[i] = Character.toLowerCase(fieldNames[i].charAt(0))
							+ fieldNames[i].substring(1);
					fields[i] = type.getDeclaredField(fieldNames[i]);
					accessibles[i] = fields[i].isAccessible();
					if (!accessibles[i]) {
						fields[i].setAccessible(true);
					}
				}
				while (true) {
					line = reader.readLine();
					if (line == null) {
						break;
					}
					String[] values = line.split(",");
					if (values.length != fields.length) {
						continue;
					}
					T item = type.newInstance();
					for (int i = 0; i < values.length; i++) {
						Object value = parse(values[i], fields[i].getType());
						fields[i].set(item, value);
					}
					items.add(item);
				}
				for (int i = 0; i < accessibles.length; i++) {
					if (!accessibles[i]) {
						fields[i].setAccessible(false);
					}
				}
			}
			Object array = Array.newInstance(type, items.size());
			return items.toArray((T[]) array);
		} catch (ReflectiveOperationException
				| IOException
				| ParseException e) {
			throw new CsvFormatterException(e);
		}
	}
	
	public static <T> T[] deserialize(String path, Class<T> type)
			throws CsvFormatterException {
		try {
			return deserialize(new FileInputStream(path), type);
		} catch (FileNotFoundException e) {
			throw new CsvFormatterException(e);
		}
	}
	
	public static <T> void serialize(OutputStream stream, T[] items)
			throws CsvFormatterException {
		try {
			if (stream == null
					|| items == null
					|| items.length == 0) {
				return;
			}
			Class<?> type = items[0].getClass();
			Field[] fields = type.getDeclaredFields();
			boolean[] accessibles = new boolean[fields.length];
			try (PrintWriter writer = new PrintWriter(stream)) {
				for (int i = 0; i < fields.length; i++) {
					accessibles[i] = fields[i].isAccessible();
					if (!accessibles[i]) {
						fields[i].setAccessible(true);
					}
					if (i > 0) {
						writer.print(",");
					}
					String fieldName = fields[i].getName();
					fieldName = Character.toUpperCase(fieldName.charAt(0))
							+ fieldName.substring(1);
					writer.print(fieldName);
				}
				writer.println();
				for (T item : items) {
					for (int i = 0; i < fields.length; i++) {
						Object value = fields[i].get(item);
						if (value == null) {
							value = "";
						}
						if (i > 0) {
							writer.print(",");
						}
						writer.print(value);
					}
					writer.println();
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new CsvFormatterException(e);
		}
	}
	
	public static <T> void serialize(String path, T[] items)
			throws CsvFormatterException {
		try {
			serialize(new FileOutputStream(path), items);
		} catch (FileNotFoundException e) {
			throw new CsvFormatterException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T parse(String value, Class<T> type)
			throws NoSuchMethodException,
				SecurityException,
				InstantiationException,
				IllegalAccessException,
				IllegalArgumentException,
				InvocationTargetException,
				ParseException {
		return (T) PrimitiveTypesUtility.parseValue(value, type);
	}
	
}
