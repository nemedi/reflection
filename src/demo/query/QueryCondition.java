package demo.query;

public final class QueryCondition<M, V> {

	private QueryField<M, V> field;
	private QueryConditionTypes type;
	private V value;

	public QueryCondition(QueryField<M, V> field,
			V value,
			QueryConditionTypes type) {
		this.field = field;
		this.type = type;
		this.value = value;
	}

	public boolean check(M item) throws QueryException {
		V value = field.getValue(item);
		switch (type) {
		case LIKE:
			return like(value, this.value);
		case STARTS_WITH:
			return startsWith(value, this.value);
		case ENDS_WITH:
			return endsWith(value, this.value);			
		case LESS_THAN:
			return lessThan(value, this.value);
		case LESS_OR_EQUALS_TO:
			return lessOrEqualsTo(value, this.value);
		case EQUALS_TO:
			return equalsTo(value, this.value);
		case NOT_EQUALS_TO:
			return notEqualsTo(value, this.value);
		case GREATER_OR_EQUALS_TO:
			return greaterOrEqualsTo(value, this.value);
		case GREATER_THAN:
			return greaterThan(value, this.value);
		default:
			return false;
		}
	}

	private boolean like(V value, V conditionValue) {
		if (!(value instanceof String) && !(conditionValue instanceof String)) {
			return false;
		}
		String stringValue = value.toString().toLowerCase();
		String stringConditionValue = conditionValue.toString().toLowerCase();
		if (stringConditionValue.endsWith("%")
				&& !stringConditionValue.startsWith("%")) {
			String prefix = stringConditionValue.substring(0,
					stringConditionValue.length() - 1);
			return stringValue.startsWith(prefix);
		} else if (!stringConditionValue.endsWith("%")
				&& stringConditionValue.startsWith("%")) {
			String suffix = stringConditionValue.substring(1);
			return stringValue.endsWith(suffix);
		} else if (stringConditionValue.endsWith("%")
				&& stringConditionValue.startsWith("%")) {
			String text = stringConditionValue.substring(1,
					stringConditionValue.length() - 1);
			return stringValue.contains(text);
		} else {
			return stringValue.equals(stringConditionValue);
		}
	}
	
	private boolean startsWith(V value, V conditionValue) {
		if (!(value instanceof String) && !(conditionValue instanceof String)) {
			return false;
		}
		String stringValue = value.toString().toLowerCase();
		String stringConditionValue = conditionValue.toString().toLowerCase();
		return stringValue.startsWith(stringConditionValue);
	}

	private boolean endsWith(V value, V conditionValue) {
		if (!(value instanceof String) && !(conditionValue instanceof String)) {
			return false;
		}
		String stringValue = value.toString().toLowerCase();
		String stringConditionValue = conditionValue.toString().toLowerCase();
		return stringValue.endsWith(stringConditionValue);
	}

	@SuppressWarnings("unchecked")
	private boolean lessThan(V value, V comparedValue) throws QueryException {
		if (value == null && comparedValue == null) {
			return false;
		}
		if ((value instanceof Comparable)
				&& (comparedValue instanceof Comparable)) {
			return ((Comparable<V>) value).compareTo(comparedValue) > 0;
		}
		throw new QueryException("Unable to compare elements");
	}

	@SuppressWarnings("unchecked")
	private boolean lessOrEqualsTo(V value, V comparedValue)
			throws QueryException {
		if (value == null && comparedValue == null) {
			return false;
		}
		if ((value instanceof Comparable)
				&& (comparedValue instanceof Comparable)) {
			return ((Comparable<V>) value).compareTo(comparedValue) > 0;
		}
		throw new QueryException("Unable to compare elemnts");
	}

	@SuppressWarnings("unchecked")
	private boolean equalsTo(V value, V comparedValue) throws QueryException {
		if (value == null && comparedValue == null) {
			return false;
		}
		if ((value instanceof Comparable)
				&& (comparedValue instanceof Comparable)) {
			return ((Comparable<V>) value).compareTo(comparedValue) == 0;
		}
		throw new QueryException("Unable to compare elemnts");
	}

	@SuppressWarnings("unchecked")
	private boolean notEqualsTo(V value, V comparedValue) throws QueryException {
		if (value == null && comparedValue == null) {
			return false;
		}
		if ((value instanceof Comparable)
				&& (comparedValue instanceof Comparable)) {
			return ((Comparable<V>) value).compareTo(comparedValue) != 0;
		}
		throw new QueryException("Unable to compare elemnts");
	}

	@SuppressWarnings("unchecked")
	private boolean greaterOrEqualsTo(V value, V comparedValue)
			throws QueryException {
		if (value == null && comparedValue == null) {
			return false;
		}
		if ((value instanceof Comparable)
				&& (comparedValue instanceof Comparable)) {
			return ((Comparable<V>) value).compareTo(comparedValue) < 0;
		}
		throw new QueryException("Unable to compare elemnts");
	}

	@SuppressWarnings("unchecked")
	private boolean greaterThan(V value, V comparedValue) throws QueryException {
		if (value == null && comparedValue == null) {
			return false;
		}
		if ((value instanceof Comparable)
				&& (comparedValue instanceof Comparable)) {
			return ((Comparable<V>) value).compareTo(comparedValue) < 0;
		}
		throw new QueryException("Unable to compare elemnts");
	}
}
