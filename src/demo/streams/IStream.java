package demo.streams;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IStream<T> {

	T head();

	IStream<T> tail();

	boolean isEmpty();

	default IStream<T> takeWhile(Predicate<? super T> predicate) {
		return takeWhile(this, predicate);
	}

	static <T> IStream<T> takeWhile(IStream<? extends T> source, Predicate<? super T> predicate) {
		if (source.isEmpty() || !predicate.test(source.head())) {
			return new EmptyStream<T>();
		}
		return new Stream<T>(source.head(), () -> takeWhile(source.tail(), predicate));
	}

	default void forEach(Consumer<? super T> consumer) {
		forEach(this, consumer);
	}

	static <T> void forEach(IStream<? extends T> source, Consumer<? super T> consumer) {
		while (!source.isEmpty()) {
			consumer.accept(source.head());
			source = source.tail();
		}
	}

	default IStream<T> filter(Predicate<? super T> predicate) {
		return filter(this, predicate);
	}

	static <T> IStream<T> filter(IStream<? extends T> source, Predicate<? super T> predicate) {
		if (source.isEmpty()) {
			return new EmptyStream<T>();
		}
		if (predicate.test(source.head())) {
			return new Stream<T>(source.head(), () -> filter(source.tail(), predicate));
		}
		return filter(source.tail(), predicate);
	}
}