package demo.query;

public final class QueryOrder<M, V> {

	private QueryOrderTypes type;
	private QueryField<M, V> field;

	public QueryOrder(QueryField<M, V> field, QueryOrderTypes type) {
		this.field = field;
		this.type = type;
	}

	public Object getType() {
		return type;
	}

	public QueryField<M, V> getField() {
		return field;

	}

}
