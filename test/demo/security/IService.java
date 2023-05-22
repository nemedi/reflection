package demo.security;

import demo.security.RequiresToBe;

public interface IService {

	@RequiresToBe("administrator")
	void execute();
}
