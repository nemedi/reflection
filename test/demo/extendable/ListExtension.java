package demo.extendable;

import java.util.List;
import java.util.function.Consumer;

import demo.extendable.Extension;

public interface ListExtension<T> extends Extension<List<T>> {

	default void foreach(Consumer<T> consumer) {
		for (T item : base()) {
			consumer.accept(item);
		}
	}
	
}
