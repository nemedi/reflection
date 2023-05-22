package demo.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import demo.notifier.CallFinder;

public class OrmConnection implements AutoCloseable {
	
	public static final String ORM_DRIVER = "orm.driver";
	public static final String ORM_URL = "orm.url";
	public static final String ORM_DEBUG = "orm.debug";
	
	private String url;
	private String provider;
	private boolean debug;

	private static Hashtable<Class<?>, OrmQuery> queries = new Hashtable<Class<?>, OrmQuery>();

	private Connection connection;

	private static Hashtable<Class<?>, Class<?>> TYPE_WRAPPERS = new Hashtable<Class<?>, Class<?>>();

	static {
		OrmConnection.TYPE_WRAPPERS.put(byte.class, Byte.class);
		OrmConnection.TYPE_WRAPPERS.put(int.class, Integer.class);
		OrmConnection.TYPE_WRAPPERS.put(long.class, Long.class);
		OrmConnection.TYPE_WRAPPERS.put(float.class, Float.class);
		OrmConnection.TYPE_WRAPPERS.put(double.class, Double.class);
		OrmConnection.TYPE_WRAPPERS.put(boolean.class, Boolean.class);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void open() throws SQLException {
		try {
			Method method = CallFinder.getOuterCallerOf(OrmConnection.class.getName(), "open");
			if (method != null) {
				ResourceBundle resourceBundle =
						ResourceBundle.getBundle(method.getDeclaringClass().getName());
				open(resourceBundle.getString(ORM_DRIVER).trim(),
						resourceBundle.getString(ORM_URL).trim());
				if (resourceBundle.containsKey(ORM_DEBUG))
					setDebug(Boolean.parseBoolean(
							resourceBundle.getString(ORM_DEBUG).trim().toLowerCase()));
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public void open(String driver, String url) throws SQLException {
		try {
			this.url = url;
			provider = this.url.toLowerCase();
			provider = provider.substring(provider.indexOf("jdbc:")	+ "jdbc:".length());
			provider = provider.substring(0, provider.indexOf(":")).trim();
			connection = Driver.class.cast(Class.forName(driver).newInstance())
					.connect(this.url, null);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public <T> T[] select(Class<T> type)
			throws SQLException {
		return select(type, null);
	}

	public <T> T[] select(Class<T> type, String condition, Object... arguments)
			throws SQLException {
		OrmQuery query = getQuery(type);
		return executeSelect(type, query.getSelectCommand(condition), arguments);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] executeSelect(Class<T> type, String command, Object... arguments)
			throws SQLException {
		try {
			PreparedStatement statement = prepareStatement(command, arguments);
			ResultSet results = statement.executeQuery();
			List<T> items = new ArrayList<T>();
			collectResults(type, statement, results, items, false);
			return items.toArray((T[]) Array.newInstance(type, items.size()));
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public <T> T selectFirst(Class<T> type)
			throws SQLException {
		return selectFirst(type, null);
	}

	public <T> T selectFirst(Class<T> type, String condition, Object... arguments)
			throws SQLException {
		OrmQuery query = getQuery(type);
		return executeSelectFirst(type, query.getSelectCommand(condition), arguments);
	}

	public <T> T executeSelectFirst(Class<T> type, String command, Object... arguments)
			throws SQLException {
		try {
			PreparedStatement statement = prepareStatement(command, arguments);
			ResultSet results = statement.executeQuery();
			List<T> items = new ArrayList<T>();
			collectResults(type, statement, results, items, true);
			return items.get(0);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public <T> int count(Class<T> type)
			throws SQLException {
		return count(type, null);
	}

	public <T> int count(Class<T> type, String condition, Object... arguments)
			throws SQLException {
		OrmQuery query = getQuery(type);
		return selectFirst(int.class, query.getCountCommand(condition),	arguments);
	}

	private <T> void collectResults(Class<T> type, PreparedStatement statement,
			ResultSet results, List<T> items, boolean onlyFirst)
				throws SQLException, ReflectiveOperationException {
		if (results != null) {
			if (Character.class.equals(type)) {
				while (results.next()) {
					items.add(type.cast(results.getString(1).charAt(0)));
				}
			}
			else if (String.class.equals(type)) {
				while (results.next()) {
					items.add(type.cast(results.getString(1)));
				}
			}
			else {
				boolean notFound = true;
				for (Class<?> valueType : TYPE_WRAPPERS.keySet()) {
					Class<?> referenceType = TYPE_WRAPPERS.get(valueType);
					if (referenceType.equals(type)
							|| valueType.equals(type)) {
						notFound = false;
						String key = valueType.getSimpleName();
						key = key.substring(0, 1).toUpperCase() + key.substring(1);
						while (results.next()) {
							Object value = results.getClass()
									.getMethod("get" + key, int.class)
									.invoke(results, 1);
							items.add(type.cast(value));
						}
						break;
					}
				}
				if (notFound) {
					ResultSetMetaData metadata = statement.getMetaData();
					Hashtable<String, Field> mappings = new Hashtable<String, Field>();
					Field[] fields = type.getDeclaredFields();
					for (int i = 1; i <= metadata.getColumnCount(); i++) {
						for (int j = 0; j < fields.length; j++) {
							if (metadata.getColumnLabel(i).toLowerCase().equals(
											fields[j].getName().toLowerCase())
									&& !mappings.containsKey(metadata.getColumnLabel(i).toLowerCase())) {
								mappings.put(metadata.getColumnLabel(i).toLowerCase(), fields[j]);
							}
						}
					}
					Constructor<T> constructor = type.getDeclaredConstructor();
					boolean isConstructorAccessible = constructor.isAccessible();
					if (!isConstructorAccessible) {
						constructor.setAccessible(true);
					}
					while (results.next()) {
						T item = constructor.newInstance();
						for (int i = 1; i <= metadata.getColumnCount(); i++) {
							if (mappings.containsKey(metadata.getColumnLabel(i).toLowerCase())) {
								Object value = null;
								Field field = mappings.get(metadata.getColumnLabel(i).toLowerCase());
								if (Character.class.equals(field.getType())) {
									value = results.getString(i).charAt(0);
								}
								else if (String.class.equals(field.getType())) {
									value = results.getString(i);
								}
								for (Class<?> valueType : TYPE_WRAPPERS.keySet()) {
									Class<?> referenceType = TYPE_WRAPPERS.get(valueType);
									if (valueType.equals(field.getType())
											|| referenceType.equals(field.getType())) {
										String key = valueType.getSimpleName();
										key = key.substring(0, 1).toUpperCase() + key.substring(1);
										value = results.getClass()
												.getMethod("get" + key, int.class)
												.invoke(results, i);
										break;
									}
								}
								boolean isFieldAccessible = field.isAccessible();
								if (!isFieldAccessible) {
									field.setAccessible(true);
								}
								field.set(item, value);
								if (!isFieldAccessible) {
									field.setAccessible(false);
								}
							}
						}
						if (!isConstructorAccessible) {
							constructor.setAccessible(true);
						}
						items.add(item);
						if (onlyFirst) {
							break;
						}
					}
				}
			}
			results.close();
		}
	}

	public <T> void insert(T record) throws SQLException {
		try {
			Class<?> type = record.getClass();
			OrmQuery query = getQuery(type);
			update(query.getInsertCommand(), query.getInsertCommandArguments(record));
			if (!OrmPrimaryKeyTypes.NONE.equals(query.getPrimaryKeyType())
					&& !OrmPrimaryKeyTypes.GUID.equals(query.getPrimaryKeyType())) {
				OrmFieldInvoker.setValue(query.getPrimaryKeyField(),
						record,
						selectFirst(int.class, query.getSelectIdentityCommand()));
			}
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public <T> void update(T record) throws SQLException {
		try {
			Class<?> type = record.getClass();
			OrmQuery query = getQuery(type);
			update(query.getUpdateCommand(), query.getUpdateCommandArguments(record));
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public int update(String command, Object... arguments)
			throws SQLException {
		try {
			PreparedStatement statement = prepareStatement(command, arguments);
			return statement.executeUpdate();
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public <T> void delete(T record) throws SQLException {
		try {
			Class<?> type = record.getClass();
			OrmQuery query = getQuery(type);
			update(query.getDeleteCommand(), query.getDeleteCommandArguments(record));
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public void close() throws SQLException {
		if (connection != null
				&& !connection.isClosed()) {
			connection.close();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
	}

	private <T> OrmQuery getQuery(Class<T> type) throws SQLException {
		if (provider == null) {
			open();
		}
		if (!queries.containsKey(type)) {
			queries.put(type, new OrmQuery(type, provider));
		}
		return queries.get(type);
	}

	private PreparedStatement prepareStatement(String command, Object... arguments)
			throws SQLException, ReflectiveOperationException {
		if (connection == null || connection.isClosed()) {
			open();
		}
		PreparedStatement statement = connection.prepareStatement(command);
		if (arguments != null && arguments.length > 0)
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i] == null) {
					continue;
				}
				else if (Character.class.equals(arguments[i].getClass())
						|| String.class.equals(arguments[i])) {
					statement.setString(i + 1, arguments[i].toString());
				}
				else {
					for (Class<?> valueType : TYPE_WRAPPERS.keySet()) {
						Class<?> referenceType = TYPE_WRAPPERS.get(valueType);
						if (arguments[i].getClass().equals(referenceType)) {
							String key = valueType.getSimpleName();
							key = key.substring(0, 1).toUpperCase() + key.substring(1);
							Object value = referenceType
									.getMethod("parse" + key, String.class)
									.invoke(null, arguments[i].toString());
							statement.getClass()
								.getMethod("set" + key,	int.class, valueType)
								.invoke(statement, i + 1, value);
							break;
						}
					}
				}
			}
		if (debug) {
			System.out.println(statement.toString());
		}
		return statement;
	}
	
}
