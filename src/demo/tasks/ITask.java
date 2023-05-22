package demo.tasks;

import java.io.Serializable;

public interface ITask<T, V> extends Serializable {

	@SuppressWarnings("unchecked")
	T execute(V...arguments) throws Exception;
}
