package demo.editable;

import demo.editable.EditableObject;

public interface IContact extends EditableObject<IContact> {

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

}