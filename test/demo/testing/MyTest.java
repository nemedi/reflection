package demo.testing;

import demo.testing.After;
import demo.testing.AfterClass;
import demo.testing.Before;
import demo.testing.BeforeClass;
import demo.testing.Test;

public class MyTest {
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println("setUpClass");
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		System.out.println("tearDownClass");
	}
	
	@Before
	public void setUp() throws Exception {
		System.out.println("setUp");
	}
	
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}
	
	@Test
	public void test() throws Exception {
		System.out.println("test");
	}
	
}
