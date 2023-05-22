package demo.editable;

import java.text.MessageFormat;

public class Contact implements IContact {

	private String firstName;
	private String lastName;

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} {1}", firstName, lastName);
	}

}
