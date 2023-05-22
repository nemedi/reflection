package demo.remoting;

import java.text.MessageFormat;

public class Service implements Contract {

	@Override
	public String format(String pattern, Object... arguments) {
		return MessageFormat.format(pattern, arguments);
	}

}
