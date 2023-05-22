package demo.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

	public static void run(Class<?> type) throws Exception {
		List<Method> beforeClassMethods = new ArrayList<Method>();
		List<Method> afterClassMethods = new ArrayList<Method>();
		List<Method> beforeMethods = new ArrayList<Method>();
		List<Method> afterMethods = new ArrayList<Method>();
		List<Method> testMethods = new ArrayList<Method>();
		for (Method method : type.getDeclaredMethods()) {
			int modifiers = method.getModifiers();
			if (Modifier.isAbstract(modifiers)) {
				continue;
			}
			if (Modifier.isStatic(modifiers)) {
				if (method.getAnnotation(BeforeClass.class) != null) {
					beforeClassMethods.add(method);
				}
				else if (method.getAnnotation(AfterClass.class) != null) {
					afterClassMethods.add(method);
				}
			}
			else {
				if (method.getAnnotation(Before.class) != null) {
					beforeMethods.add(method);
				}
				else if (method.getAnnotation(After.class) != null) {
					afterMethods.add(method);
				}
				else if (method.getAnnotation(Test.class) != null) {
					testMethods.add(method);
				}
			}
		}
		if (testMethods.size() == 0) {
			return;
		}
		List<Exception> exceptions = new ArrayList<Exception>();
		for (Method beforeClassMethod : beforeClassMethods) {
			try {
				beforeClassMethod.invoke(null);
			} catch (Exception e) {
				exceptions.add(e);
			}
		}
		Constructor<?> constructor = type.getDeclaredConstructor();
		boolean accessible = constructor.isAccessible();
		for (Method testMethod : testMethods) {
			if (!accessible) {
				constructor.setAccessible(true);
			}
			Object instance = constructor.newInstance();
			if (!accessible) {
				constructor.setAccessible(false);
			}
			for (Method beforeMethod : beforeMethods) {
				try {
					beforeMethod.invoke(instance);
				} catch (Exception e) {
					exceptions.add(e);
				}
			}
			try {
				testMethod.invoke(instance);
			} catch (Exception e) {
				exceptions.add(e);
			}
			for (Method afterMethod : afterMethods) {
				try {
					afterMethod.invoke(instance);
				} catch (Exception e) {
					exceptions.add(e);
				}
			}

		}
		for (Method afterClassMethod : afterClassMethods) {
			try {
				afterClassMethod.invoke(null);
			} catch (Exception e) {
				exceptions.add(e);
			}
		}
		if (exceptions.size() > 0) {
			throw new TestException(exceptions.toArray(new Exception[exceptions.size()]));
		}
	}
	
	public static void run(String typeName) throws Exception {
		run(Class.forName(typeName));
	}
	
}
