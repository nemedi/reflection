package demo.streams;

import java.util.function.Supplier;

public class Stream<T> implements IStream<T> {

	private final T head;
	private final Supplier<IStream<T>> tail;

	public Stream(T head, Supplier<IStream<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

	public T head() {
		return this.head;
	}

	public IStream<T> tail() {
		return this.tail.get();
	}

	public boolean isEmpty() {
		return false;
	}
}
