package demo.browser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class ClassBrowser {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String INDENTATION = "   ";
	
	public static String getClassDescription(Class<?> type) {
		if (type == null) {
			return "";
		}
		final StringBuilder builder = new StringBuilder();
		writeAnnotations(type.getAnnotations(), "", builder);
		writeModifiers(type.getModifiers(), builder);
		if (type.isInterface()) {
			builder.append("interface ");
		}
		else if (type.isEnum()) {
			builder.append("enum ");
		}
		else {
			builder.append("class ");
		}
		builder.append(type.getSimpleName());
		if (type.getSuperclass() != null
				&& !Object.class.equals(type.getSuperclass())) {
			builder.append(" extends ");
			writeName(type.getSuperclass().getName(), builder);
		}
		writeInterfaces(type, builder);
		builder.append("{");
		builder.append(LINE_SEPARATOR);
		builder.append(LINE_SEPARATOR);
		for (Field field : type.getDeclaredFields()) {
			writeField(field, builder);
		}
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			writeConstructor(constructor, builder);
		}
		for (Method method : type.getDeclaredMethods()) {
			writeMethod(method, builder);
		}
		builder.append("}");
		return builder.toString();
	}
	
	private static void writeModifiers(int modifiers, StringBuilder builder) {
		if (Modifier.isPublic(modifiers)) {
			builder.append("public ");
		}
		else if (Modifier.isProtected(modifiers)) {
			builder.append("protected ");
		}
		else if (Modifier.isPrivate(modifiers)) {
			builder.append("private ");
		}
		if (Modifier.isAbstract(modifiers)){
			builder.append("abstract ");
		}
		if (Modifier.isStatic(modifiers)) {
			builder.append("static ");
		}
		if (Modifier.isFinal(modifiers)) {
			builder.append("final ");
		}
		if (Modifier.isVolatile(modifiers)) {
			builder.append("volatile ");
		}
		if (Modifier.isTransient(modifiers)) {
			builder.append("transient ");
		}
	}
	
	private static void writeInterfaces(Class<?> type, StringBuilder builder) {
		Class<?>[] types = type.getInterfaces();
		if (types.length > 0) {
			builder.append("implements ");
			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					builder.append(", ");
				}
				writeName(types[i].getName(), builder);
			}
		}
		builder.append(" ");
	}
	
	private static void writeField(Field field, StringBuilder builder) {
		writeAnnotations(field.getDeclaredAnnotations(), INDENTATION, builder);
		builder.append(INDENTATION);
		writeModifiers(field.getModifiers(), builder);
		builder.append(" ");
		writeName(field.getType().getName(), builder);
		builder.append(" ");
		builder.append(field.getName());
		builder.append(";");
		builder.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
	}
	
	private static void writeConstructor(Constructor<?> constructor,
			StringBuilder builder) {
		writeAnnotations(constructor.getDeclaredAnnotations(), INDENTATION, builder);
		builder.append(INDENTATION);
		writeModifiers(constructor.getModifiers(), builder);
		builder.append(constructor.getDeclaringClass().getSimpleName());
		builder.append("(");
		writeParameters(constructor.getParameters(), builder);
		builder.append(");");
		builder.append(LINE_SEPARATOR);
		builder.append(LINE_SEPARATOR);
	}

	private static void writeMethod(Method method, StringBuilder builder) {
		writeAnnotations(method.getDeclaredAnnotations(), INDENTATION, builder);
		builder.append(INDENTATION);
		writeModifiers(method.getModifiers(), builder);
		writeName(method.getReturnType().getName(), builder);
		builder.append(" ");
		builder.append(method.getName());
		builder.append("(");
		writeParameters(method.getParameters(), builder);
		builder.append(");");
		builder.append(LINE_SEPARATOR);
		builder.append(LINE_SEPARATOR);
	}
	
	private static void writeParameters(Parameter[] parameters, StringBuilder builder) {
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			writeName(parameters[i].getType().getName(), builder);
			builder.append(" ");
			builder.append(parameters[i].getName());
		}
	}
	
	private static void writeName(String name, StringBuilder builder) {
		builder.append(name.startsWith("java.lang.") ?
				name.substring("java.lang.".length()) : 
				name);
	}
	
	private static void writeAnnotations(Annotation[] annotations, final String indentation, StringBuilder builder) {
		for (Annotation annotation : annotations) {
			builder.append(indentation);
			builder.append("@");
			writeName(annotation.annotationType().getName(), builder);
			Method[] methods = annotation.annotationType().getDeclaredMethods();
			if (methods.length > 0) {
				builder.append("(");
				for (int i = 0; i < methods.length; i++) {
					if (!"value".equals(methods[i].getName())) {
						if (i > 0) {
							builder.append(", ");
						}
						builder.append(methods[i].getName());
						builder.append(" = ");
					}
					String quote = String.class.equals(methods[i].getReturnType()) ? "\"" : "";
					builder.append(quote);
					try {
						builder.append(methods[i].invoke(annotation,
								new Object[] {}));
					} catch (Exception e) {
						builder.append("");
					}
					builder.append(quote);
				}
				builder.append(")");
			}
			builder.append(LINE_SEPARATOR);
		}
	}
	
}
