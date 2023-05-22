package demo.streams;

import demo.streams.IStream;
import demo.streams.Stream;

public class Streams {

	public static Stream<Integer> from(int n) {
		return new Stream<Integer>(n, () -> from(n + 1));
	}

	public static IStream<Integer> sieve(IStream<Integer> s) {
		return new Stream<Integer>(s.head(), () -> sieve(s.tail().filter(n -> n % s.head() != 0)));
	}

	public static IStream<Integer> fibonacci() {
		return new Stream<Integer>(0, () -> new Stream<Integer>(1, () -> nextFibonacciPair(0, 1)));
	}

	private static IStream<Integer> nextFibonacciPair(int a, int b) {
		int fibonacci = a + b;
		int previous = b;
		return new Stream<Integer>(fibonacci, () -> nextFibonacciPair(previous, fibonacci));
	}
}
