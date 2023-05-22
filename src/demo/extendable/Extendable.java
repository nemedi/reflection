package demo.extendable;

public interface Extendable<T> {

	@SuppressWarnings("unchecked")
	default <E extends Extension<T>> E as(Class<E> type) {
		return Extension.create(type, (T) this);
	}
}
