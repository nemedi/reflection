package demo.pointcuts;

import java.text.MessageFormat;

public class ServiceAspect {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator"); 

	public static void beforeSayHello(IService service, String name) {
		System.out.format("before sayHello(%s)%s", name, LINE_SEPARATOR);
	}
	
	public static void afterSayHello(IService service, String name, String result) {
		System.out.format("after sayHello(%s) = %s%s", name, result, LINE_SEPARATOR);
	}
	
	public static String _insteadSayHello(IService service, String name) {
		String result = MessageFormat.format("Szervusz {0}!", name);
		System.out.format("instead sayHello(%s) = %s%s", name, result, LINE_SEPARATOR);
		return result;
	}
	
	public static void throwingSayHello(IService service, String name, Exception e) {
		System.out.format("throwing sayHello(%s)%s", name, LINE_SEPARATOR);
	}

}
