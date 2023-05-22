package demo.dependency;


public class Binding<T> {

	private Class<? extends T> type;
	private T instance;
	
	public void to(Class<? extends T> type) {
		this.type = type;
	}
	
	public void to(T instance) {
		this.instance = instance;
	}
	
	public Class<? extends T> getType() {
		return type;
	}
	
	public T getInstance() {
		return instance;
	}
}
