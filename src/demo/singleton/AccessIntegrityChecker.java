package demo.singleton;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AccessIntegrityChecker {

	public static void check() {
		try {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			String className = stackTraceElements[2].getClassName();
			int index = 3;
			while (index < stackTraceElements.length
					&& className.equals(stackTraceElements[index].getClassName())) {
				index++;
			}
			if (index == stackTraceElements.length) {
				return;
			}
			String methodName = stackTraceElements[index - 1].getMethodName();
			Class<?> type = Class.forName(className);
			List<Method> methods = new ArrayList<Method>();
			methods.addAll(Arrays.asList(type.getDeclaredMethods()));
			methods.addAll(Arrays.asList(type.getMethods()));
			for (Method method : methods) {
				if (methodName.equals(method)) {
					int modifiers = method.getModifiers();
					if (Modifier.isPrivate(modifiers)) {
						throw new IllegalAccessException(methodName);
					}
				}
			}
		} catch (ClassNotFoundException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
