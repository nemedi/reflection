package demo.mocking;

public class MockResult<V> {

	private V value;
	private MockMethod<V> method;
	
	public MockResult(V value) {
		this.value = value;
	}
	
	public MockResult(MockMethod<V> method) {
		this.method = method;
	}
	
	public V getValue() {
		return value;
	}
	
	public MockMethod<V> getMethod() {
		return method;
	}
}
