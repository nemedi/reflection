package demo.mailer;

import java.util.ResourceBundle;

public class Settings {

	public static final String ID;
	public static final String HOST;
	public static final String LOGIN;
	public static final String PASSWORD;
	
	static {
		ResourceBundle bundle = ResourceBundle.getBundle(Settings.class.getName().toLowerCase());
		ID = bundle.getString("id");
		HOST = bundle.getString("host");
		LOGIN = bundle.getString("login");
		PASSWORD = bundle.getString("password");
	}
}
