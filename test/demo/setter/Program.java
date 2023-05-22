package demo.setter;

import demo.setter.FieldSetter;

public class Program {

	public static void main(String[] args) {
		try {
			FieldSetter.set("value", Boolean.FALSE, Boolean.TRUE);
			if (Boolean.FALSE) {
				System.out.println("FALSE is TRUE");
			}
			FieldSetter.set("value", "no", "yes");
			if ("no".equals("yes")) {
				System.out.println("no means yes");
			}
			else {
				System.out.println("no doesn't mean yes");
			}
		} catch (ReflectiveOperationException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
