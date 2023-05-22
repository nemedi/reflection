package demo.query;

import java.util.List;
import java.util.Map;

public final class QueryResults<M> {

	private QueryField<M, ?>[] fields;
	private List<Map<QueryField<M, ?>, Object>> results;

	public QueryField<M, ?>[] getFields() {
		return fields;
	}

	public QueryResults(List<Map<QueryField<M, ?>, Object>> results,
			QueryField<M, ?>[] fields) {
		this.results = results;
		this.fields = fields;
	}

	public int count() {
		return results.size();
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(int rowIndex, QueryField<M, V> field) {
		Map<QueryField<M, ?>, Object> item = results.get(rowIndex);
		return (V) item.get(field);
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(int rowIndex, int fieldIndex) {
		Map<QueryField<M, ?>, Object> item = results.get(fieldIndex);
		return (V) item.get(rowIndex);
	}

}
