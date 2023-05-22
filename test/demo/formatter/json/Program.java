package demo.formatter.json;

import demo.formatter.json.JsonFormatter;

public class Program {

	public static void main(String[] args) throws Exception {
		Contact contact = new Contact("Iulian", "Ilie-Nemedi");
		String data = JsonFormatter.serialize(contact);
		System.out.println(data);
		contact = JsonFormatter.deserialize(data, Contact.class);
		System.out.println(contact.getFirstName() + " "
				+ contact.getLastName());
	}

}
