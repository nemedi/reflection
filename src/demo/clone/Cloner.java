package demo.clone;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import demo.PrimitiveTypesUtility;

public class Cloner {

	public static <T> T clone(T object) throws CloneException {
		try {
			return clone(object, new HashMap<Object, Object>());
		} catch (ReflectiveOperationException e) {
			throw new CloneException(e.getLocalizedMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T clone(T object, Map<Object, Object> cloneTable)
			throws ReflectiveOperationException {
		if(object == null) {
			return object;
		}
		if (cloneTable.containsKey(object)) {
			return (T) cloneTable.get(object);
		}
		Class<?> type = object.getClass();
		if (PrimitiveTypesUtility.isPrimitive(type)) {
			return object;
		}
		Object clone = null;
		if (String.class.equals(type)) {
			char[] data = ((String) object).toCharArray();
			clone = String.copyValueOf(data);
		}
		else if (type.isArray()) {
			int length = Array.getLength(object);
			clone = Array.newInstance(type.getComponentType(), length);
			if (clone != null) {
				cloneTable.put(object, clone);
			}
			for (int i = 0; i < length; i++) {
				Object item = clone(Array.get(object, i), cloneTable);
				Array.set(clone, i, item);
			}
		}
		else if (object instanceof Cloneable){
			Method method = type.getMethod("clone");
			boolean accessible = method.isAccessible();
			if (!accessible) {
				method.setAccessible(true);
			}
			clone = method.invoke(object);
			if (!accessible) {
				method.setAccessible(false);
			}
			if (clone != null) {
				cloneTable.put(object, clone);
			}
		} else if (object instanceof Collection<?>) {
			Collection<Object> collection = (Collection<Object>) object;
			Constructor<?> constructor = type.getDeclaredConstructor();
			boolean accessible = constructor.isAccessible();
			if (!accessible) {
				constructor.setAccessible(true);
			}
			clone = constructor.newInstance();
			Collection<Object> cloneCollection = (Collection<Object>) clone;
			cloneTable.put(object, clone);
			if (!accessible) {
				constructor.setAccessible(false);
			}
			for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
				Object item = iterator.next();
				Object cloneItem = clone(item, cloneTable);
				cloneCollection.add(cloneItem);
			}
		} else {
			Constructor<?> constructor = type.getDeclaredConstructor();
			boolean accessible = constructor.isAccessible();
			if (!accessible) {
				constructor.setAccessible(true);
			}
			clone = constructor.newInstance();
			if (clone != null) {
				cloneTable.put(object, clone);
			}
			if (!accessible) {
				constructor.setAccessible(false);
			}
			Field[] fields = type.getDeclaredFields();
			for(Field field : fields) {
				accessible = field.isAccessible();
				if(!accessible) {
					field.setAccessible(true);
				}
				Object value = field.get(object);
				field.set(clone, clone(value, cloneTable));
				if(!accessible)
					field.setAccessible(false);
			}
		}
		return (T) clone;
	}
}
