package demo.aop;


public interface Pointcut {
	
	boolean apply(String type, String method);
	
	default void beforeAdvice(String method, Object target, Object...arguments) throws Throwable {
	}
	
	default void afterAdvice(String method, Object target, Object result, Object...arguments) throws Throwable {
	}
	
}
