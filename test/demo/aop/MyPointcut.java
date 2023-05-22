package demo.aop;

import demo.aop.Pointcut;

public class MyPointcut implements Pointcut {

	@Override
	public void beforeAdvice(String method, Object target, Object... arguments)
			throws Throwable {
		System.out.println("before: " + method);
	}

	@Override
	public void afterAdvice(String method, Object target, Object result,
			Object... arguments) throws Throwable {
		System.out.println("after: " + method);
	}

	@Override
	public boolean apply(String type, String method) {
		return "com.axway.trainings.reflection.aop.Service".equals(type);
	}

}
