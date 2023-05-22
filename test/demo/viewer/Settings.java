package demo.viewer;

import java.util.ResourceBundle;

public class Settings {

	public static final String REMOTE_HOST;
	public static final int REMOTE_PORT;
	public static final int LOCAL_PORT;
	
	static {
		ResourceBundle bundle = ResourceBundle.getBundle(Settings.class.getName().toLowerCase());
		REMOTE_HOST = bundle.getString("remoteHost");
		REMOTE_PORT = Integer.parseInt(bundle.getString("remotePort"));
		LOCAL_PORT = Integer.parseInt(bundle.getString("localPort"));
	}
}
