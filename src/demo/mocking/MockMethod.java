package demo.mocking;

public interface MockMethod<V> {

	V invoke(Object...arguments);
}
