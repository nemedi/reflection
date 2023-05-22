package demo.mocking;

import static demo.mocking.Mock.any;
import static demo.mocking.Mock.mock;
import static demo.mocking.Mock.with;

import java.util.List;
import java.util.Random;

import demo.mocking.MockMethod;

public class Program {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		List<Integer> evenNumbers = mock(List.class);
		MockMethod<Boolean> addMockMethod = new MockMethod<Boolean>() {
			@Override
			public Boolean invoke(Object... arguments) {
				return arguments != null
						&& arguments.length == 1
						&& arguments[0] instanceof Integer
						&& ((Integer) arguments[0]) % 2 == 0;
			}
		};
		with(evenNumbers)
			.when(evenNumbers.add(any(Integer.class)))
			.thenReturn(addMockMethod);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int value = random.nextInt(100);
			boolean result = evenNumbers.add(value);
			System.out.println("add(" + value + ") = " + result);
		}
	}

}
