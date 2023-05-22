package demo.notifier;


public interface PropertyChangeListener {

	void propertyChanged(Object instance, String property, Object oldValue, Object newValue);
}
