package demo.query;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import demo.notifier.CallFinder;

public class QueryModel<M> {

	private Class<M> type;
	private Map<String, QueryField<M, ?>> fields;

	protected QueryModel(Class<M> type) {
		this.type = type;
		this.fields = new HashMap<String, QueryField<M, ?>>();
	}

	@SuppressWarnings("unchecked")
	protected final <V> QueryField<M, V> field(String name) throws QueryException {
		if (name == null) {
			return null;
		}
		if (!fields.containsKey(name)) {
			fields.put(name, new QueryField<M, V>(this, name));
		}
		return (QueryField<M, V>) fields.get(name);
	}
	
	protected final <V> QueryField<M, V> thisField() throws QueryException {
		try {
			Method method = CallFinder.getCallerOf(QueryModel.class.getName(), "thisField");
			if (method != null) {
				return field(method.getName());
			}
			else {
				return null;
			}
		} catch (ReflectiveOperationException e) {
			throw new QueryException(e);
		}
	}

	public final Class<M> getType() {
		return type;
	}
}
