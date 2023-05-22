package demo.loader;



public class TaskManager implements ITaskManager {

	@Override
	public void execute(ITask task) {
		task.execute();
	}

}
