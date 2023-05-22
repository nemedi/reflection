package demo.mocking;

public class MockResultBinding<T, V> {
	
	private Mock<T> mock;

	MockResultBinding(Mock<T> mock) {
		this.mock = mock;
	}

	public void thenReturn(V value) {
		mock.bindResult(new MockResult<V>(value));
	}
	
	public void thenReturn(MockMethod<V> method) {
		mock.bindResult(new MockResult<V>(method));
	}
}
