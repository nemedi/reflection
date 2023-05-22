package demo.streams;

import static demo.streams.Streams.*;

public class Program {

	public static void main(String[] args) {
		System.out.println("Fibonacci (<100):");
		fibonacci()
			.takeWhile(n -> n < 100)
			.forEach(System.out::println);
		
		System.out.println("Sieve of Eratosthenes (<100):");
		sieve(from(2))
			.takeWhile(n -> n < 100)
			.forEach(System.out::println);
	}

}
