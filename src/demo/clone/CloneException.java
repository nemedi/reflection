package demo.clone;

public class CloneException extends Exception {

	private static final long serialVersionUID = 1L;

	public CloneException(String message, Exception e) {
		super(message, e);
	}

}
