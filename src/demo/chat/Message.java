package demo.chat;

public class Message {
	
	public static final int FORBIDDEN = 0;
	public static final int LOGIN = 1;
	public static final int ACCEPT = 2;
	public static final int DENY = 3;
	public static final int ADD = 4;
	public static final int SEND = 5;
	public static final int RECEIVE = 6;
	public static final int LOGOUT = 7;
	public static final int EXIT = 8;
	public static final int REMOVE = 9;
	public static final String ALL = "*";
	
	private int type;
	private String[] arguments;
	
	public Message(int type, String...arguments) {
		this.type = type;
		this.arguments = arguments;
	}

	public int getType() {
		return type;
	}
	
	public String[] getArguments() {
		return arguments;
	}
}
