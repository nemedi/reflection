package demo.notifier;

import java.text.MessageFormat;

import demo.notifier.PropertyChangeListener;

public class Program {

	public static void main(String[] args) {
		Contact contact = new Contact();
		PropertyChangeListener listener = new PropertyChangeListener() {
			
			@Override
			public void propertyChanged(Object instance, String property, Object oldValue,
					Object newValue) {
				String message = MessageFormat.format("Property {0} was changed from {1} to {2}.",
						property, oldValue, newValue);
				System.out.println(message);
			}
		};
		contact.getPropertyChangeSupport().addPropertyChangeLister(listener);
		contact.setFirstName("Iulian");
		contact.setLastName("Ilie-Nemedi");
		contact.setEmail("iilie@axway.com");
	}

}
