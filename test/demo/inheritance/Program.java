package demo.inheritance;

import demo.inheritance.InheritanceAdapter;

public class Program {

	public static void main(String[] args) {
		IForm form = new Form();
		IPrintable printable = new Printable();
		IPrintableForm printableForm = InheritanceAdapter
				.combine(IPrintableForm.class, form, printable);
		printableForm.initialize();
		printableForm.print();
	}

}
