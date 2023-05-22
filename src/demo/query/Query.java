package demo.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Query<M> {
	
	private List<M> collection;
	private List<QueryCondition<M, ?>> conditions;
	private List<QueryOrder<M, ?>> orders;

	private Query(List<M> collection) {
		this.collection = collection;
		this.conditions = new ArrayList<QueryCondition<M, ?>>();
		this.orders = new ArrayList<QueryOrder<M, ?>>();
	}

	public static <M> Query<M> from(List<M> collection) {
		return new Query<M>(collection);
	}
	
	public static <M> Query<M> from(M[] collection) {
		return new Query<M>(Arrays.asList(collection));
	}

	@SuppressWarnings("unchecked")
	public Query<M> where(QueryCondition<M, ?>... conditions) {
		this.conditions.addAll(Arrays.asList(conditions));
		return this;
	}

	@SuppressWarnings("unchecked")
	public Query<M> orderBy(QueryOrder<M, ?>... orders) {
		this.orders.addAll(Arrays.asList(orders));
		return this;
	}

	public List<M> list() throws QueryException {
		List<M> results = new ArrayList<M>();
		for (M item : collection) {
			boolean valid = true;
			for (QueryCondition<M, ?> condition : conditions) {
				if (!condition.check(item)) {
					valid = false;
					break;
				}
			}
			if (valid) {
				results.add(item);
			}
		}
		Collections.sort(results, new QueryOrderComparator<M, Object>(orders));
		return results;
	}

	@SuppressWarnings("unchecked")
	public QueryResults<M> select(QueryField<M, ?>... fields) throws QueryException {
		List<Map<QueryField<M, ?>, Object>> results = new ArrayList<Map<QueryField<M, ?>, Object>>();
		for (M i : list()) {
			Map<QueryField<M, ?>, Object> result = new HashMap<QueryField<M, ?>, Object>();
			for (QueryField<M, ?> field : fields) {
				Object value = field.getValue(i);
				result.put(field, value);
			}
			results.add(result);
		}
		return new QueryResults<M>(results, fields);
	}
}
