package demo.query;

public final class QueryException extends Exception {

	private static final long serialVersionUID = 1L;

	public QueryException(Exception e) {
		super(e);
	}
	
	public QueryException(String message) {
		super(message);
	}

}
