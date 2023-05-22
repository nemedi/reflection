package demo.notifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PropertyChangeSupport {

	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	public void addPropertyChangeLister(PropertyChangeListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removePropertyChangeLister(PropertyChangeListener listener) {
		if (listener != null && listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	private void notifyPropertyChanged(Object instance, String property, Object oldValue, Object newValue) {
		for (PropertyChangeListener listener : listeners) {
			try {
				listener.propertyChanged(instance, property, oldValue, newValue);
			}
			catch (Exception e) {
				continue;
			}
		}
	}
	
	public void setThisProperty(Object instance, Object newValue) {
		try {
			Method method = CallFinder.getOuterCallerOf(PropertyChangeSupport.class.getName(), "setThisProperty");
			if (method == null || !method.getName().startsWith("set")) {
				return;
			}
			String name = method.getName();
			name = name.substring("set".length());
			name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
			Field field = method.getDeclaringClass().getDeclaredField(name);
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			Object oldValue = field.get(instance);
			if (!accessible) {
				field.setAccessible(false);
			}
			if (oldValue == null && newValue != null
					|| !oldValue.equals(newValue)) {
				notifyPropertyChanged(instance, name, oldValue, newValue);
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
		
	}

}
