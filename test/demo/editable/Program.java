package demo.editable;

public class Program {

	public static void main(String[] args) {
		IContact contact = new Contact();
		contact = contact.asEditable();
		contact.beginTransaction();
		contact.setFirstName("Iulian");
		contact.setLastName("Ilie-Nemedi");
		System.out.println("Before rollback: " + contact);
		contact.rollbackTransaction();
		System.out.println("After rollback: " + contact);
		contact.setFirstName("Gyula");
		contact.setLastName("Nemedi");
		System.out.println("Before commit: " + contact);
		contact.commitTransaction();
		System.out.println("After commit: " + contact);
	}

}
