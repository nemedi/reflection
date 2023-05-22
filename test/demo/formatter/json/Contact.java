package demo.formatter.json;

public class Contact {

	private static int count;
	
	private int id;
	private String firstName;
	private String lastName;
	private String[][] text;
	
	private Contact() {
		id = ++count;
	}
	

	public Contact(String firstName, String lastName) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		text = new String[][] {new String[] {"a", "b", "c"}};
	}
	
	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public String[][] getText() {
		return text;
	}

}
