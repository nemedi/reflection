package demo.remoting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.URI;

import demo.formatter.json.JsonFormatter;
import demo.formatter.json.JsonFormatterException;

public class RpcProxy<T> implements InvocationHandler {

	private String host;
	private int port;
	private String service;
	private String session;
	private T instance;

	public RpcProxy(URI endpoint) {
		this.host = endpoint.getHost();
		this.port = endpoint.getPort();
		this.service = endpoint.getPath();
		if (this.service.startsWith("/")) {
			this.service = this.service.substring(1);
		}
	}

	private Object invoke(String method, Object[] arguments, Class<?> resultType)
			throws RpcException {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			RpcRequest request = new RpcRequest(service, method, arguments, session);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			String data = JsonFormatter.serialize(request);
			writer.println(data);
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			data = reader.readLine();
			RpcResponse response = JsonFormatter.deserialize(data, RpcResponse.class); 
			if (response.getFault() != null) {
				throw new RpcException(response.getFault());
			}
			session = response.getSession();
			Object result = response.getResult();
			if (resultType != void.class) {
				result = JsonFormatter.deserialize((String) result, resultType);
			}
			return result;
		}
		catch (IOException | JsonFormatterException e) {
			throw new RpcException(e);
		}
		finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				throw new RpcException(e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> type, URI endpoint) {
		RpcProxy<T> proxy = new RpcProxy<T>(endpoint);
		proxy.instance = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, proxy);
		return proxy.instance;
	}
	
	public static <T> T create(Class<T> type, String endpoint) {
		return create(type, URI.create(endpoint));
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {
		return invoke(method.getName(), arguments, method.getReturnType());
	}
	
	public void destroy() throws RpcException {
		invoke(RpcRequest.DESTROY_METHOD, new Object[]{}, void.class);
	}
	
	@Override
	protected void finalize() throws Throwable {
		destroy();
	}
	
}