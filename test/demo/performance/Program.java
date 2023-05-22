package demo.performance;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.text.MessageFormat;

public class Program {
	
	public static void main(String[] args) {
		try {
			
			final int n = 100000;
			final String pattern = "{0}ms using {1} call";
			long duration;
			String message;
			System.out.println(n + " invocations in:");

			duration = testDirectCall(n);
			message = MessageFormat.format(pattern, duration, "direct");
			System.out.println(message);
			
			duration = testBasicReflectionCall(n);
			message = MessageFormat.format(pattern, duration, "basic reflection");
			System.out.println(message);
			
			duration = testLookupReflectionCall(n);
			message = MessageFormat.format(pattern, duration, "lookup reflection");
			System.out.println(message);

		} catch (Throwable e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public static int multiply(int i, int j) {
		return i * j;
	}

	public static int subtract(int i, int j) {
		return i - j;
	}
	
	public static long testDirectCall(int n) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			multiply(3, 2);
			subtract(10, 5);
		}
		return System.currentTimeMillis() - start;
	}
	
	public static long testBasicReflectionCall(int n) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			Method multiply = Program.class.getDeclaredMethod("multiply", int.class, int.class);
			Method subtract = Program.class.getDeclaredMethod("subtract", int.class, int.class);
			for (int i = 0; i < n; i++) {
				multiply.invoke(null, 3, 2);
				subtract.invoke(null, 10, 5);
			}
			return System.currentTimeMillis() - start;
		} catch (ReflectiveOperationException e) {
			throw new Throwable(e);
		}
	}
	
	public static long testLookupReflectionCall(int n) throws Throwable {
		long start = System.currentTimeMillis();
		final Lookup lookup = MethodHandles.lookup();
		for (int i = 0; i < n; i++) {
			MethodHandle multiply = lookup.findStatic(Program.class,
					"multiply",
					MethodType.methodType(int.class, int.class, int.class));
			multiply.invoke(3, 2);
			MethodHandle subtract = lookup.findStatic(Program.class,
					"subtract",
					MethodType.methodType(int.class, int.class, int.class));
			subtract.invoke(10, 5);
		}
		return System.currentTimeMillis() - start;
	}

}
