package demo.query;

import java.lang.reflect.Field;

public final class QueryField<M, V> {

	private QueryModel<M> model;
	private Field field;

	public QueryField(QueryModel<M> model, String name) throws QueryException {
		try {
			this.model = model;
			this.field = this.model.getType().getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new QueryException(e);
		}
	}

	public QueryCondition<M, V> like(V value) {
		return new QueryCondition<M, V>(this, value, QueryConditionTypes.LIKE);
	}

	public QueryCondition<M, V> startsWith(V value) {
		return new QueryCondition<M, V>(this, value, QueryConditionTypes.STARTS_WITH);
	}
	
	public QueryCondition<M, V> endsWith(V value) {
		return new QueryCondition<M, V>(this, value, QueryConditionTypes.ENDS_WITH);
	}
	
	public QueryCondition<M, V> lessThan(V value) {
		return new QueryCondition<M, V>(this, value,
				QueryConditionTypes.GREATER_THAN);
	}

	public QueryCondition<M, V> lessOrEqualsTo(V value) {
		return new QueryCondition<M, V>(this, value,
				QueryConditionTypes.LESS_OR_EQUALS_TO);
	}

	public QueryCondition<M, V> equalsTo(V value) {
		return new QueryCondition<M, V>(this, value,
				QueryConditionTypes.EQUALS_TO);
	}

	public QueryCondition<M, V> notEqualsTo(V value) {
		return new QueryCondition<M, V>(this, value,
				QueryConditionTypes.NOT_EQUALS_TO);
	}

	public QueryCondition<M, V> greaterOrEqualsTo(V value) {
		return new QueryCondition<M, V>(this, value,
				QueryConditionTypes.GREATER_OR_EQUALS_TO);
	}

	public QueryCondition<M, V> greaterThan(V value) {
		return new QueryCondition<M, V>(this, value,
				QueryConditionTypes.GREATER_THAN);
	}

	public QueryOrder<M, V> ascending() {
		return new QueryOrder<M, V>(this, QueryOrderTypes.ASCENDING);
	}

	public QueryOrder<M, V> descending() {
		return new QueryOrder<M, V>(this, QueryOrderTypes.DESCENDING);
	}

	public String getName() {
		return field.getName();
	}

	@SuppressWarnings("unchecked")
	public V getValue(M item) throws QueryException {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible) {
				field.setAccessible(true);
			}
			Object value = field.get(item);
			if (!accessible) {
				field.setAccessible(false);
			}
			return (V) value;
		} catch (ReflectiveOperationException e) {
			throw new QueryException(e);
		}
	}

}
