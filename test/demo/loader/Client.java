package demo.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import demo.loader.RpcClassLoader;


public class Client {

	public static void main(String[] args) {
		try {
			RpcClassLoader classLoader =
					new RpcClassLoader("tcp://localhost:9090");
			Class<?> taskInterfaceType = classLoader.loadClass("ITask");
			classLoader.loadClass("ITaskManager");
			Class<?> taskImplementationType = classLoader.loadClass("Task");
			Class<?> taskManagerImplementationType = classLoader.loadClass("TaskManager");
			Object taskManager = taskManagerImplementationType.newInstance();
			Object task = taskImplementationType.newInstance();
			Method executeMethod = taskManagerImplementationType.getMethod("execute", taskInterfaceType);
			executeMethod.invoke(taskManager, task);
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException
				| NoSuchMethodException
				| SecurityException
				| IllegalArgumentException
				| InvocationTargetException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
