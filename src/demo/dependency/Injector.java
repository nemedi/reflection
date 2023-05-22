package demo.dependency;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Injector {

	private Binder binder;
	
	public Injector(Module module) {
		this.binder = new Binder();
		module.configure(this.binder);
	}
	
	private <T> T getDependency(Class<T> type)
		throws ReflectiveOperationException {
		Binding<T> binding = this.binder.getBinding(type);
		if (binding.getInstance() != null) {
			return binding.getInstance();
		}
		else {
			return this.getInstance(binding.getType());
		}
	}

	public <T> T getInstance(Class<T> type)
		throws ReflectiveOperationException {
		Constructor<T> constructor = type.getDeclaredConstructor();
		boolean accessible = constructor.isAccessible();
		if (!accessible) {
			constructor.setAccessible(true);
		}
		T object = constructor.newInstance();
		if (!accessible) {
			constructor.setAccessible(false);
		}
		for (Field field : type.getDeclaredFields()) {
			if (field.getAnnotation(Inject.class) != null) {
				accessible = field.isAccessible();
				if (!accessible) {
					field.setAccessible(true);
				}
				field.set(object, this.getDependency(field.getType()));
				if (!accessible) {
					field.setAccessible(false);
				}
			}
		}
		return object;
	}
} 
