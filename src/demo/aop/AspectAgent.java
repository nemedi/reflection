package demo.aop;

import java.lang.instrument.Instrumentation;

public class AspectAgent {
  public static void premain(String agentArguments, Instrumentation instrumentation) {	
    instrumentation.addTransformer(new AspectClassFileTransformer());
  }	
}