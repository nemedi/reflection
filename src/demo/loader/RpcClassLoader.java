package demo.loader;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import demo.remoting.RpcProxy;
import demo.remoting.RpcServer;

public class RpcClassLoader extends ClassLoader {
	
	private static final String SERVICE = "classService";

	private Map<String, Class<?>> types =
			new HashMap<String, Class<?>>();
	private IClassService proxy;

	public RpcClassLoader(String endpoint) {
		super(RpcClassLoader.class.getClassLoader());
		if (!endpoint.endsWith("/")) {
			endpoint += "/";
		}
		this.proxy = RpcProxy.create(IClassService.class, endpoint + SERVICE);
	}
	
	public static void registerClassService(RpcServer server) {
		server.publish(SERVICE, ClassService.class);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name == null) {
			return null;
		}
		if (types.containsKey(name)
				&& types.get(name) != null) {
			return types.get(name);
		}
		try {
			return findSystemClass(name);
		} catch (Exception e) {
		}
		try {
			byte[] buffer = Base64.getDecoder().decode(
					proxy.getBytecode(name));
			Class<?> type = defineClass(null, buffer, 0, buffer.length);
			types.put(name, type);
			return type;
		} catch (Exception e) {
			return null;
		}
	}

}
