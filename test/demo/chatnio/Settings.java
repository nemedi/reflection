package demo.chatnio;

import java.util.ResourceBundle;

public class Settings {

	public static final String HOST;
	public static final int PORT;
	
	static {
		ResourceBundle bundle = ResourceBundle.getBundle(Settings.class.getName().toLowerCase());
		HOST = bundle.getString("host");
		PORT = Integer.parseInt(bundle.getString("port"));
	}
}
