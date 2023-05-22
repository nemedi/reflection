package demo.rest;

import static demo.rest.RestResponse.badRequest;
import static demo.rest.RestResponse.internalServerError;
import static demo.rest.RestResponse.ok;
import static demo.rest.RestResponse.serviceUnavailable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import demo.PrimitiveTypesUtility;
import demo.formatter.json.JsonFormatter;
import demo.formatter.json.JsonFormatterException;
import javassist.Modifier;

public class RestHandler implements HttpHandler {

	private Map<String, Class<?>> mappings;

	public RestHandler(Class<?>...types) throws Exception {
		this.mappings = new HashMap<String, Class<?>>();
		for (Class<?> type : types) {
			RestResource restResource = type.getAnnotation(RestResource.class);
			if (restResource != null) {
				checkConfiguration(type);
				mappings.put(trimSlash(restResource.value()), type);
			}
		}
	}
	
	private void checkConfiguration(Class<?> type) throws Exception {
		if (Arrays
				.asList(type.getDeclaredMethods())
				.stream()
				.filter(method -> {
					RestEndpoint restEndpoint = method.getAnnotation(RestEndpoint.class);
					if (restEndpoint == null) {
						return false;
					}
					String body = restEndpoint.body();
					List<String> queries = Arrays.asList(restEndpoint.queries());
					List<String> parameters = Arrays
						.asList(method.getParameters())
						.stream()
						.map(parameter -> parameter.getName())
						.collect(Collectors.toList());
					if (parameters
						.stream()
						.filter(name -> !name.equals(body) &&
								!queries.contains(name))
						.collect(Collectors.toList()).size() > 0) {
						return true;
					}
					if (queries
							.stream()
							.filter(name -> !parameters.contains(name))
							.collect(Collectors.toList())
							.size() > 0
							|| body.length() > 0 && !parameters.contains(body)) {
						return true;
					}
					return false;
				})
				.collect(Collectors.toList()).size() > 0) {
			throw new Exception("Invalid REST configuration for type: " + type.getName());
		}
	}

	private String trimSlash(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String path = trimSlash(exchange.getRequestURI().getPath());
		RestResponse response = null;
		if (!mappings.containsKey(path)) {
			response = serviceUnavailable();
		} else {
			Class<?> type = mappings.get(path);
			List<Method> methods = Arrays
				.asList(type.getDeclaredMethods())
				.stream()
				.filter(method -> match(method, exchange))
				.collect(Collectors.toList());
			if (methods.size() != 1) {
				response = badRequest();
			} else {
				try {
					Method method = methods.get(0);
					Object resource = null;
					if (!Modifier.isStatic(method.getModifiers())) {
						resource = type.newInstance();
					}
					Object result = method.invoke(resource, getArguments(method, exchange));
					if (result instanceof RestResponse) {
						response = (RestResponse) result;
					} else {
						response = ok(result);
					}
				} catch (Exception e) {
					response = internalServerError(e.getMessage());
				}
			}
		}
		if (response == null) {
			response = badRequest();
		}
		try {
			if (response.getEntity() != null) {
				byte[] buffer = JsonFormatter.serialize(response.getEntity()).getBytes();
				exchange.sendResponseHeaders(response.getStatus(), buffer.length);
				try (DataOutputStream stream = new DataOutputStream(exchange.getResponseBody())) {
					stream.write(buffer);
				}
			} else {
				exchange.sendResponseHeaders(response.getStatus(), 0);
			}
			exchange.getResponseBody().close();
		} catch (JsonFormatterException e) {
			exchange.sendResponseHeaders(internalServerError(null).getStatus(), 0);
			exchange.getResponseBody().close();
		}
	}

	private boolean match(Method method, HttpExchange exchange) {
		RestEndpoint restEndpoint = method.getAnnotation(RestEndpoint.class);
		if (restEndpoint == null
				|| !restEndpoint.method().equalsIgnoreCase(exchange.getRequestMethod())) {
			return false;
		}
		String restEndpointBody = restEndpoint.body();
		List<String> restEndpointQueries = Arrays.asList(restEndpoint.queries());
		String contentLength = exchange.getRequestHeaders().getFirst("content-length");
		boolean hasBody = contentLength != null && Integer.parseInt(contentLength) > 0;
		Map<String, String> queries = parseQuery(exchange);
		if (hasBody && restEndpointBody.length() == 0) {
			return false;
		}
		if (restEndpointBody.length() > 0 && !hasBody) {
			return false;
		}
		if (queries
				.entrySet()
				.stream()
				.map(entry -> entry.getKey())
				.filter(name -> !restEndpointQueries.contains(name))
				.collect(Collectors.toList())
				.size() > 0) {
			return false;
		}
		if (restEndpointQueries
				.stream()
				.filter(name -> !queries.containsKey(name))
				.collect(Collectors.toList()).size() > 0) {
			return false;
		}
		return true;
	}

	private Object[] getArguments(Method method, HttpExchange exchange) throws IOException, JsonFormatterException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		List<Object> arguments = new ArrayList<Object>();
		RestEndpoint restEndpoint = method.getAnnotation(RestEndpoint.class);
		String body = null;
		String contentLengtHeader = exchange.getRequestHeaders().getFirst("content-length");
		if (contentLengtHeader != null
				&& restEndpoint.body().length() > 0) {
			int contentLength = Integer.parseInt(contentLengtHeader);
			byte[] buffer = new byte[contentLength];
			try (DataInputStream stream = new DataInputStream(exchange.getRequestBody())) {
				stream.readFully(buffer, 0, contentLength);
			}
			body = new String(buffer);
		}
		Map<String, String> queries = parseQuery(exchange);
		for (Parameter parameter : method.getParameters()) {
			if (parameter.getName().equals(restEndpoint.body())) {
				arguments.add(JsonFormatter.deserialize(body, parameter.getType()));
			} else {
				arguments.add(PrimitiveTypesUtility.parseValue(
						queries.get(parameter.getName()), parameter.getType()));
				
			}
		}
		return arguments.toArray();
	}
	
	private Map<String, String> parseQuery(HttpExchange exchange) {
		Map<String, String> queries = new HashMap<String, String>();
		if (exchange.getRequestURI().getQuery() != null
				&& exchange.getRequestURI().getQuery().length() > 0) {
			for (String part : exchange.getRequestURI().getQuery().split("&")) {
				String[] values = part.split("=");
				queries.put(values[0], values[1]);
			}
		}
		return queries;
	}

}
