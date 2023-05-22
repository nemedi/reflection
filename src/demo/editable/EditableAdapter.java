package demo.editable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditableAdapter<M> implements InvocationHandler {
	
	private EditableTransactionManager<M> transactionManager;
	
	private EditableAdapter(M model) {
		this.transactionManager = new EditableTransactionManager<M>(model);
	}

	@SuppressWarnings("unchecked")
	public static <M> M adapt(M model) {
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		interfaces.addAll(Arrays.asList(model.getClass().getInterfaces()));
		interfaces.add(EditableObject.class);
		EditableAdapter<M> adapter = new EditableAdapter<M>(model);
		Object proxy = Proxy.newProxyInstance(model.getClass().getClassLoader(),
				interfaces.toArray(new Class<?>[interfaces.size()]),
				adapter);
		return (M) proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (EditableObject.class.equals(method.getDeclaringClass())) {
			method = transactionManager.getClass()
					.getMethod(method.getName(), method.getParameterTypes());
			return method.invoke(this.transactionManager, args);
		}
		else {
			M model = transactionManager.getModel();
			return method.invoke(model, args);
		}
	}
	
}
