package demo.editable;

import java.lang.reflect.Field;

public class EditableField {

	private Field field;
	private Object object;
	private Object oldValue;
	
	public EditableField(Field field, Object object) throws EditableException {
		this.field = field;
		this.object = object;
		this.oldValue = getValue();
	}
	
	private Object getValue() throws EditableException {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			Object value = field.get(object);
			if (!accessible) {
				field.setAccessible(false);
			}
			return value;
		} catch (SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			throw new EditableException(e.getLocalizedMessage(), e);
		}
	}

	private void setValue(Object value) throws EditableException {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			field.set(object, value);
			if (!accessible) {
				field.setAccessible(false);
			}
		} catch (SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			throw new EditableException(e.getLocalizedMessage(), e);
		}
	}

	public void commit() throws EditableException {
		oldValue = getValue();
	}
	
	public void rollback() throws EditableException { 
		setValue(oldValue);
	}
	
	public boolean isChanged() throws EditableException {
		Object newValue = getValue();
		return oldValue == null && newValue != null
			|| oldValue != null && newValue == null
			|| oldValue != null && newValue != null && !oldValue.equals(newValue);
	}
	
}
