package demo.editable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class EditableTransaction<M> {
	
	private List<EditableField> fields;
	
	EditableTransaction(M model) throws EditableException {
		this.fields = new ArrayList<EditableField>();
		if (model != null) {
			Queue<Object> objects = new LinkedList<Object>();
			objects.add(model);
			while (objects.size() > 0) {
				Object object = objects.poll();
				if (object == null) {
					continue;
				}
				Field[] fields = object.getClass().getDeclaredFields();
				for (Field field : fields) {
					this.fields.add(new EditableField(field, object));
					Object value = null;
					try {
						boolean accessible = field.isAccessible();
						if (!accessible) {
							field.setAccessible(true);
						}
						value = field.get(object);
						if (!accessible) {
							field.setAccessible(false);
						}
					} catch (Exception e) {
						value = null;
					}
					if (value != null) {
						if (value.getClass().isArray()) {
							int length = Array.getLength(value);
							for (int i = 0; i < length; i++) {
								objects.add(Array.get(value, i));
							}
						} else if (value instanceof Map<?, ?>) {
								Object[] items = ((Map<?, ?>) value).values().toArray();
								for (Object item : items) {
									objects.add(item);
								}
						} else if (value instanceof Collection<?>) {
							Object[] items = ((Collection<?>) value).toArray();
							for (Object item : items) {
								objects.add(item);
							}
						} else {
							objects.add(value);
						}
					}
				}
			}
		}
	}
	
	public void commit() throws EditableException {
		for (EditableField field : this.fields) {
			field.commit();
		}
	}
	
	public void rollback() throws EditableException {
		for (EditableField field : this.fields) {
			field.rollback();
		}
	}
}
