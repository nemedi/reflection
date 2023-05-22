package demo.notifier;

import java.lang.reflect.Method;

public class CallFinder {

	public static Method getCallerOf(String typeName, String methodName)
			throws ReflectiveOperationException {
		if (typeName == null || methodName == null)
			return null;
		StackTraceElement[] stackTraceElements = Thread.currentThread()
				.getStackTrace();
		for (int i = 0; i < stackTraceElements.length - 1; i++) {
			if (typeName.equals(stackTraceElements[i].getClassName())
					&& methodName.equals(stackTraceElements[i].getMethodName())) {
				Method[] methods = Class.forName(
						stackTraceElements[i + 1].getClassName())
						.getDeclaredMethods();
				for (Method method : methods) {
					if (method.getName().equals(
							stackTraceElements[i + 1].getMethodName())) {
						return method;
					}
				}
			}
		}
		return null;
	}

	public static Method getOuterCallerOf(String typeName, String methodName)
			throws ReflectiveOperationException {
		if (typeName == null || methodName == null)
			return null;
		StackTraceElement[] stackTraceElements = Thread.currentThread()
				.getStackTrace();
		for (int i = 0; i < stackTraceElements.length - 1; i++) {
			if (typeName.equals(stackTraceElements[i].getClassName())
					&& methodName.equals(stackTraceElements[i].getMethodName())) {
				if (typeName.equals(stackTraceElements[i + 1].getClassName()))
					methodName = stackTraceElements[i + 1].getMethodName();
				else {
					Method[] methods = Class.forName(
							stackTraceElements[i + 1].getClassName())
							.getDeclaredMethods();
					for (Method method : methods) {
						if (method.getName().equals(
								stackTraceElements[i + 1].getMethodName())) {
							return method;
						}
					}
				}
			}
		}
		return null;
	}

}
