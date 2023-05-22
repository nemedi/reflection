package demo.query;

import java.util.Comparator;
import java.util.List;

public final class QueryOrderComparator<M, V> implements Comparator<M> {

	private List<QueryOrder<M, ?>> orders;

	public QueryOrderComparator(List<QueryOrder<M, ?>> orders) {
		this.orders = orders;
	}

	@Override
	public int compare(M item, M comparedItem) {
		try {
			for (QueryOrder<M, ?> order : orders) {
				int direction = QueryOrderTypes.ASCENDING.equals(order
						.getType()) ? 1 : -1;
				Object value = order.getField().getValue(item);
				Object comparedValue = order.getField().getValue(comparedItem);
				int result = compareItems(value, comparedValue, direction);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		} catch (QueryException e) {
			return 0;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int compareItems(Object value, Object comparedValue, int direction)
			throws QueryException {
		if ((value instanceof Comparable) && (comparedValue instanceof Comparable)) {
			return direction * ((Comparable) value).compareTo(comparedValue);
		}
		throw new QueryException("Uncomparable items.");

	}

}
