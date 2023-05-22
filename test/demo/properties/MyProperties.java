package demo.properties;

import demo.properties.Properties;

public class MyProperties extends Properties {

	public static String FirstName;
	public static String LastName;
	public static int Age;
	
	static {
		initialize();
	}
	
}
