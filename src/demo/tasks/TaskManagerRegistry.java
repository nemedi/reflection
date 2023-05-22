package demo.tasks;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.text.MessageFormat;

public class TaskManagerRegistry {
	
	private int port;
	
	public void start(int port) throws RemoteException {
		this.port = port;
		LocateRegistry.createRegistry(port);
	}

	public <T, V> void publish(String name, ITaskManager<T, V> manager)
			throws RemoteException, MalformedURLException, UnknownHostException {
		Naming.rebind(getEndpoint(name), manager);
	}
	
	public void unpublish(String name)
			throws RemoteException, MalformedURLException, NotBoundException {
		Naming.unbind(name);
	}
	
	private String getEndpoint(String name) throws UnknownHostException {
		return MessageFormat.format("rmi://{0}:{1}/{2}",
				InetAddress.getLocalHost().getHostAddress(),
				port + "",
				name);
	}
}
