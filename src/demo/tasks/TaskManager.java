package demo.tasks;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TaskManager<T, V>
	extends UnicastRemoteObject
	implements ITaskManager<T, V> {
	
	protected TaskManager() throws RemoteException {
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public T execute(ITask<T, V> task, V... arguments) throws RemoteException {
		try {
			return task.execute(arguments);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}


}
