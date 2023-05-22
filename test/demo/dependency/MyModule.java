package demo.dependency;

import demo.dependency.Binder;
import demo.dependency.Module;

public class MyModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(Service.class).to(MyService.class);
	}

}
