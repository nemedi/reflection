package demo.streams;

public class EmptyStream<T> implements IStream<T> {
	
	public T head() {
		throw new UnsupportedOperationException("Empty stream");
	}

	public IStream<T> tail() {
		throw new UnsupportedOperationException("Empty stream");
	}

	public boolean isEmpty() {
		return true;
	}
}