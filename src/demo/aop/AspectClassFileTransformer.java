package demo.aop;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class AspectClassFileTransformer implements ClassFileTransformer {
	
	public byte[] transform(ClassLoader loader, String className,
			Class<?> redefiningClass, ProtectionDomain domain, byte[] bytes)
			throws IllegalClassFormatException {
		ClassPool pool = ClassPool.getDefault();
		CtClass type = null;
		try {
			AspectManager.getInstance().initialize(pool);
			type = pool.makeClass(new ByteArrayInputStream(bytes));
			for (CtMethod method : type.getDeclaredMethods()) {
				if (!method.isEmpty()) {
					changeMethod(method);
				}
			}
			bytes = type.toBytecode();
		} catch (Exception e) { 
			throw new IllegalClassFormatException(e.getMessage());
		} finally {
			if (type != null) {
				type.detach();
			}
		}
		return bytes;
	}

	private void changeMethod(CtMethod method) throws NotFoundException,
			CannotCompileException {
		if (AspectManager.getInstance().apply(method.getDeclaringClass().getName(), method.getName())) {
			method.insertBefore(AspectManager.class.getName()
					+ ".getInstance().executeBeforeAdvices($0, $args);");
			method.insertAfter(AspectManager.class.getName()
					+ ".getInstance().executeAfterAdvices($0, $_, $args);");
		}
	}
	
}


