package demo;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class PrimitiveTypesUtility {
	
	private static Map<Class<?>, Class<?>> primitiveTypes;
	
	static {
		primitiveTypes = new HashMap<Class<?>, Class<?>>();
		primitiveTypes.put(Character.TYPE, Character.class);
		primitiveTypes.put(Boolean.TYPE, Boolean.class);
		primitiveTypes.put(Byte.TYPE, Byte.class);
		primitiveTypes.put(Short.TYPE, Short.class);
		primitiveTypes.put(Integer.TYPE, Integer.class);
		primitiveTypes.put(Long.TYPE, Long.class);
		primitiveTypes.put(Float.TYPE, Float.class);
		primitiveTypes.put(Double.TYPE, Double.class);
	}
	
    @SuppressWarnings("serial")
	private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<Class<?>, Object>() {
        
    	@SuppressWarnings("unused")
		private boolean booleanValue;
        
    	@SuppressWarnings("unused")
    	private byte byteValue;
        
    	@SuppressWarnings("unused")
    	private short shortValue;
        
    	@SuppressWarnings("unused")
    	private int integerValue;
        
    	@SuppressWarnings("unused")
    	private long longValue;
        
    	@SuppressWarnings("unused")
    	private float floatValue;
        
    	@SuppressWarnings("unused")
    	private double doubleValue;
        
    	@SuppressWarnings("unused")
    	private char characterValue;
    	
        {
            for (final Field field : getClass().getDeclaredFields()) {
                try {
                    put(field.getType(), field.get(this));
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    };
    
	public static Object getDefaultValue(Class<?> type) {
		if (type.isArray()) {
			return Array.newInstance(type.getComponentType(), 0);
		}
        return DEFAULT_VALUES.get(type);
    }

	public static boolean isPrimitive(Class<?> type) {
		return type != null
				&& (type.isPrimitive()
						|| primitiveTypes.values().contains(type));
	}
	
	public static Class<?> getWrapperType(Class<?> type) {
		for (Class<?> primitiveType : primitiveTypes.keySet()) {
			if (primitiveType.equals(type)
					|| primitiveTypes.get(primitiveType).equals(type)) {
				return primitiveTypes.get(primitiveType);
			}
		}
		return null;
	}
	
	public static Object parseValue(String text, Class<?> type)
			throws ParseException,
				NoSuchMethodException,
				SecurityException,
				InstantiationException,
				IllegalAccessException,
				IllegalArgumentException,
				InvocationTargetException
			 {
		if (text == null || type == null || text.isEmpty()) {
			return null;
		}
		else if (Object.class.equals(type)) {
			return "null".equalsIgnoreCase(text) ? null : text;
		}
		if (String.class.equals(type)) {
			return "null".equalsIgnoreCase(text.trim()) ? null : text;
		}
		else if (Character.class.equals(type)) {
			return text.charAt(0);
		}
		else if (Date.class.equals(type)) {
			if (type.equals(Date.class)){
				int start = text.indexOf("(");
				int end = text.lastIndexOf(")");
				return new Date(Long.parseLong(
						text.substring(start + "(".length(), end)));
			}
			else {
				return DateFormat.getDateInstance(DateFormat.SHORT).parse(text);
			}
		}
		else {
			Class<?> wrapperType = getWrapperType(type);
			if (wrapperType == null) {
				return null; 
			}
			Constructor<?> constructor = wrapperType.getConstructor(String.class);
			return constructor.newInstance(text);
		}
	}
}
