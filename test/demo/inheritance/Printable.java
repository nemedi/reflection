package demo.inheritance;

public class Printable implements IPrintable {

	@Override
	public void print() {
		System.out.println("This object was printed.");
	}

}
