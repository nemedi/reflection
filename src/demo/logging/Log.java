package demo.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	public static void error(String message) {
		log(message);
	}

	public static void warning(String message) {
		log(message);
	}
	
	public static void info(String message) {
		log(message);
	}

	private synchronized static void log(String message) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(new Date());
		String severityLevel = stackTraceElements[2].getMethodName().toUpperCase();
		String typeName = stackTraceElements[3].getClassName();
		String methodName = stackTraceElements[3].getMethodName();
		String fileName = stackTraceElements[3].getFileName();
		String lineNumber = Integer.toString(stackTraceElements[3].getLineNumber());
		StringBuilder builder = new StringBuilder();
		builder
			.append(timestamp)
			.append("\t")
			.append(severityLevel)
			.append("\t")
			.append(typeName)
			.append("\t")
			.append(methodName)
			.append("\t")
			.append(fileName)
			.append("\t")
			.append(lineNumber)
			.append("\t")
			.append(message);
		System.out.println(builder.toString());
	}
}
