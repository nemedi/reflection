package demo.dependency;

import java.util.HashMap;
import java.util.Map;

public class Binder {

	private Map<Class<?>, Binding<?>> bindings =
		new HashMap<Class<?>, Binding<?>>();
	
	public <T> Binding<T> bind(Class<T> type) {
		Binding<T> binding = new Binding<T>();
		this.bindings.put(type, binding);
		return binding;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Binding<T> getBinding(Class<T> type) {
		return (Binding<T>) this.bindings.get(type);
	}
}
