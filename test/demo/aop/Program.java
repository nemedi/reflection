package demo.aop;



public class Program {

	public static void main(String[] args) throws Exception {
		Service service = new Service();
		String result = service.sayHello("Gyula");
		System.out.println(result);
	}

}
