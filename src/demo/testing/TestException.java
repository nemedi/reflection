package demo.testing;

public class TestException extends Exception {

	private static final long serialVersionUID = 1L;
	private Exception[] exceptions;
	
	public TestException(Exception...exceptions) {
		this.exceptions = exceptions;
	}
	
	public Exception[] getInnerExceptions() {
		return exceptions;
	}
	
	@Override
	public String getLocalizedMessage() {
		StringBuilder builder = new StringBuilder();
		if (exceptions != null) {
			String lineSeparator = System.getProperty("line.separator");
			for (Exception exception : exceptions) {
				if (builder.length() > 0) {
					builder.append(lineSeparator);
				}
				builder.append(exception.getLocalizedMessage());
			}
		}
		return builder.toString();
	}

}
