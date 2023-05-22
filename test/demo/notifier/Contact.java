package demo.notifier;

import demo.notifier.PropertyChangeSupport;

public class Contact {

	private String firstName;
	private String lastName;
	private String email;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport();
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		propertyChangeSupport.setThisProperty(this, firstName);
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		propertyChangeSupport.setThisProperty(this, lastName);
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		propertyChangeSupport.setThisProperty(this, email);
	}
	
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}
	
}
