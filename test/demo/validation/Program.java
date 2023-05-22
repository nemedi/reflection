package demo.validation;

import demo.validation.Validator;

public class Program {

	public static void main(String[] args) {
		try {
			IPerson person = new Person();
			person = Validator.adapt(person);
			person.setGender('G');
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}
