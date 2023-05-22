package demo.auditing;

import demo.auditing.Auditable;

public interface IService {

	@Auditable
	void doSomething();
}
