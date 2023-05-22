Aspect = A module which has a set of APIs providing cross-cutting requirements. For example, a logging module would be called AOP aspect for logging. An application can have any number of aspects depending on the requirement.
Join point = This represents a point in your application where you can plug-in AOP aspect. You can also say, it is the actual place in the application where an action will be taken using Spring AOP framework.
Advice = This is the actual action to be taken either before or after the method execution. This is actual piece of code that is invoked during program execution by Spring AOP framework.
Pointcut = This is a set of one or more joinpoints where an advice should be executed. You can specify pointcuts using expressions or patterns as we will see in our AOP examples.
Introduction = An introduction allows you to add new methods or attributes to existing classes.
Target object = The object being advised by one or more aspects, this object will always be a proxied object. Also referred to as the advised object.
Weaving = Weaving is the process of linking aspects with other application types or objects to create an advised object. This can be done at compile time, load time, or at runtime.


// http://www.javabeat.net/introduction-to-java-agents/
// http://stackoverflow.com/questions/13032918/can-java-classloaders-rewrite-the-bytecode-of-only-their-copy-of-system-class


// http://cglib.sourceforge.net/howto.html
// http://cglib.sourceforge.net/xref/samples/Beans.html
// http://blog.frankel.ch/the-power-of-proxies-in-java
// http://www.javased.com/?api=net.sf.cglib.proxy.Enhancer
// http://jnb.ociweb.com/jnb/jnbNov2005.html
// http://www.javased.com/index.php?source_dir=geronimo-xbean/xbean-classloader/src/test/java/org/apache/xbean/classloader/MultiParentClassLoaderTest.java
// http://blog.novoj.net/2009/04/18/spring-cglib-dynamic-aop-proxies-proper-pointcut-equals-method-is-simply-essential/
// http://www.programcreek.com/java-api-examples/index.php?api=net.sf.cglib.proxy.Enhancer
// http://mydailyjava.blogspot.de/2013/11/cglib-missing-manual.html

-javaagent=<pathToJar> -DpointcutsFile=aop.properties