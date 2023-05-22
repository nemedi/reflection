package demo.testing;

import demo.testing.TestRunner;

public class Program {

	public static void main(String[] args) {
		try {
			TestRunner.run(MyTest.class);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
