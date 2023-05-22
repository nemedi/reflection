package demo.tasks;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITaskManager<T, V> extends Remote {

	@SuppressWarnings("unchecked")
	T execute(ITask<T, V> task, V...arguments) throws RemoteException;
}
