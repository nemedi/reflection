package demo.pointcuts;

import java.text.MessageFormat;

public class Service implements IService {

	@Override
	public String sayHello(String name) throws Exception {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Argument cannot be empty.");
		}
		return MessageFormat.format("Hello {0}!", name);
	}

}
