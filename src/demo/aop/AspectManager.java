package demo.aop;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;

public class AspectManager {

	private static final String POINTCUTS_FILE = "pointcutsFile";
	private static AspectManager instance;
	private List<Pointcut> pointcuts;
	private Map<String, List<Pointcut>> methodPointcuts;
	
	private AspectManager() {
		methodPointcuts = new HashMap<String, List<Pointcut>>();
	}
	
	private static synchronized void createInstance() {
		instance = new AspectManager();
	}
	
	public synchronized void initialize(ClassPool pool) throws Exception {
		if (pointcuts == null) {
			pointcuts = new ArrayList<Pointcut>();
			String propertiesKey = System.getProperty(POINTCUTS_FILE);
			if (propertiesKey != null && Files.exists(Paths.get(propertiesKey))) {
				for (String line : Files.readAllLines(Paths.get(propertiesKey))) {
					if (line.trim().length() > 0) {
						pointcuts.add((Pointcut) pool.get(line.trim()).toClass().newInstance());
					}
				}
			}
		}
	}

	public static AspectManager getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	public boolean apply(String type, String method) {
		List<Pointcut> pointcuts = new ArrayList<Pointcut>();
		for (Pointcut pointcut : this.pointcuts) {
			if (pointcut.apply(type, method)) {
				pointcuts.add(pointcut);
			}
		}
		if (pointcuts.size() > 0) {
			String key = type + ":" + method;
			methodPointcuts.put(key, pointcuts);
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public void executeBeforeAdvices(Object target, Object[] arguments) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		String method = element.getMethodName();
		String type = element.getClassName();
		for (Pointcut pointcut : getMethodPointcuts(type, method)) {
			try {
				pointcut.beforeAdvice(method, target, arguments);
			} catch (Throwable t) {
				continue;
			}
		}
	}
	
	public void executeAfterAdvices(Object target, Object result, Object[] arguments) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		String method = element.getMethodName();
		String type = element.getClassName();
		for (Pointcut pointcut : getMethodPointcuts(type, method)) {
			try {
				pointcut.afterAdvice(method, target, result, arguments);
			} catch (Throwable t) {
				continue;
			}
		}
	}
	
	private List<Pointcut> getMethodPointcuts(String type, String method) {
		String key = type + ":" + method;
		return methodPointcuts.containsKey(key) ?
				methodPointcuts.get(key) : new ArrayList<Pointcut>();
	}
		
}
