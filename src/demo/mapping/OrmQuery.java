package demo.mapping;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Vector;

public class OrmQuery {
	
	private String selectIdentityColumn;
	private String selectCommand;
	private String insertCommand;
	private String updateCommand;
	private String deleteCommand;
	private String countCommand;
	private Field[] insertCommandFields;
	private Field[] updateCommandFields;
	private Field[] deleteCommandFields;
	private OrmPrimaryKeyTypes primaryKeyType = OrmPrimaryKeyTypes.NONE;
	private static Hashtable<String, String> identityTokens = new Hashtable<String, String>();
	
	static {
		OrmQuery.identityTokens.put("mysql", "last_insert_id()");
		OrmQuery.identityTokens.put("mssql", "@@IDENTITY");
	}

	public <T> OrmQuery(Class<T> type, String provider) {
		Field[] fields = type.getDeclaredFields();
		OrmTable tableAnnotation = type.getAnnotation(OrmTable.class);
		if (tableAnnotation == null
				|| tableAnnotation.value() == null
				|| tableAnnotation.value().trim().length() == 0) {
			return;
		}
		StringBuilder selectCommandBuilder = new StringBuilder();
		StringBuilder orderClauseBuilder = new StringBuilder();
		StringBuilder countCommandBuilder = new StringBuilder();
		OrmOrderTypes orderType = OrmOrderTypes.NONE;
		selectCommandBuilder.append("select ");
		int selectCommandBuilderInitialLength = selectCommandBuilder.length();
		countCommandBuilder.append("select count(");
		StringBuffer insertCommandBuilder = new StringBuffer();
		StringBuffer valuesClauseBuilder = new StringBuffer();
		insertCommandBuilder.append("insert into " + tableAnnotation.value()
				+ " (");
		int insertCommandBuilderInitialLength = insertCommandBuilder.length();
		StringBuffer updateCommandBuilder = new StringBuffer();
		updateCommandBuilder.append("update " + tableAnnotation.value()
				+ " set ");
		int updateCommandBuilderInitialLength = updateCommandBuilder.length();
		String updateWhereClause = " where ";
		String columnName = null;
		Vector<Field> insertCommandFields = new Vector<Field>();
		Vector<Field> updateCommandFields = new Vector<Field>();
		for (int i = 0; i < fields.length; i++) {
			OrmColumn columnAnnotation = (OrmColumn) fields[i]
					.getAnnotation(OrmColumn.class);
			if (columnAnnotation != null) {
				columnName = columnAnnotation.name() != null
						&& columnAnnotation.name().trim().length() > 0 ? columnAnnotation
						.name().trim()
						: fields[i].getName();
				if (selectCommandBuilder.length() > selectCommandBuilderInitialLength) {
					selectCommandBuilder.append(", ");
				}
				selectCommandBuilder.append(columnName
						+ (!columnName.equals(fields[i].getName()) ? " as "
								+ fields[i].getName() : ""));
				if (!columnAnnotation.order().equals(OrmOrderTypes.NONE)) {
					orderType = columnAnnotation.order();
					if (orderClauseBuilder.length() > 0) {
						orderClauseBuilder.append(", ");
					}
					orderClauseBuilder.append(columnName);
				}
				if (!columnAnnotation.primaryKey().equals(
						OrmPrimaryKeyTypes.NONE)) {
					deleteCommand = MessageFormat.format("delete from {0} where {1} = ?",
							tableAnnotation.value(), columnName);
					deleteCommandFields = new Field[] { fields[i] };
					primaryKeyType = columnAnnotation.primaryKey();
					countCommandBuilder.append(columnName);
					countCommandBuilder.append(") from ");
					countCommandBuilder.append(tableAnnotation.value());
					countCommandBuilder.append("{0}");
					countCommand = countCommandBuilder.toString();
					selectIdentityColumn = MessageFormat.format("select {0} from {1} where {0} = {2}",
							columnName, tableAnnotation.value(),
							OrmQuery.identityTokens.containsKey(provider) ?
									OrmQuery.identityTokens.get(provider)
									: "max(" + columnName + ")");
					if (OrmPrimaryKeyTypes.SEQUENCE.equals(columnAnnotation.primaryKey())) {
						if (insertCommandBuilder.length() > insertCommandBuilderInitialLength) {
							insertCommandBuilder.append(", ");
							valuesClauseBuilder.append(", ");
						}
						insertCommandBuilder.append(columnName);
						valuesClauseBuilder.append("max(" + columnName + ") + 1");
					} else if (!OrmPrimaryKeyTypes.AUTOMATIC.equals(columnAnnotation.primaryKey())) {
						if (insertCommandBuilder.length() > insertCommandBuilderInitialLength) {
							insertCommandBuilder.append(", ");
							valuesClauseBuilder.append(", ");
						}
						insertCommandBuilder.append(columnName);
						valuesClauseBuilder.append("?");
					}
					updateWhereClause += columnName + " = ?";
				} else {
					if (insertCommandBuilder.length() > insertCommandBuilderInitialLength) {
						insertCommandBuilder.append(", ");
						valuesClauseBuilder.append(", ");
					}
					insertCommandBuilder.append(columnName);
					valuesClauseBuilder.append("?");
					insertCommandFields.addElement(fields[i]);
					if (updateCommandBuilder.length() > updateCommandBuilderInitialLength) {
						updateCommandBuilder.append(", ");
					}
					updateCommandBuilder.append(columnName + " = ?");
					updateCommandFields.addElement(fields[i]);
				}
			}
		}
		selectCommandBuilder.append(" from " + tableAnnotation.value().trim() + "{0}");
		if (!OrmOrderTypes.NONE.equals(orderType)
				&& orderClauseBuilder.toString().length() > 0) {
			selectCommandBuilder.append(" order by "
					+ orderClauseBuilder.toString()
					+ " "
					+ (OrmOrderTypes.ASCENDING.equals(orderType) ? "asc" : "desc"));
		}
		selectCommand = selectCommandBuilder.toString();
		insertCommandBuilder.append(") values (" + valuesClauseBuilder.toString() + ")");
		updateCommandBuilder.append(updateWhereClause);
		updateCommandFields.addElement(deleteCommandFields[0]);
		insertCommand = insertCommandBuilder.toString();
		this.insertCommandFields =
				insertCommandFields.toArray(new Field[insertCommandFields.size()]);
		updateCommand = updateCommandBuilder.toString();
		this.updateCommandFields =
				updateCommandFields.toArray(new Field[updateCommandFields.size()]);
	}

	public String getCountCommand() {
		return getCountCommand("");
	}

	public String getCountCommand(String condition) {
		return MessageFormat.format(countCommand,
				condition != null && condition.trim().length() > 0 ?
						" where " + condition : "");
	}

	public String getSelectCommand() {
		return getSelectCommand("");
	}

	public String getSelectCommand(String condition) {
		return MessageFormat.format(selectCommand,
				condition != null && condition.trim().length() > 0 ?
						" where " + condition : "");
	}

	public <T> Object[] getInsertCommandArguments(T item)
			throws ReflectiveOperationException {
		Object[] insertCommandArguments = new Object[insertCommandFields.length];
		if (item != null) {
			for (int i = 0; i < insertCommandArguments.length; i++) {
				if (insertCommandFields[i]
						.equals(deleteCommandFields[0])
						&& primaryKeyType.equals(OrmPrimaryKeyTypes.GUID)) {
					insertCommandArguments[i] = "new GUID";
				}
				else {
					insertCommandArguments[i] = OrmFieldInvoker.getValue(insertCommandFields[i], item);
				}
			}
		}
		return insertCommandArguments;
	}

	public String getInsertCommand() {
		return insertCommand;
	}

	public String getUpdateCommand() {
		return updateCommand;
	}

	public <T> Object[] getUpdateCommandArguments(T item)
			throws ReflectiveOperationException {
		Object[] updateCommandArguments = new Object[updateCommandFields.length];
		if (item != null) {
			for (int i = 0; i < updateCommandArguments.length - 1; i++) {
				updateCommandArguments[i] = OrmFieldInvoker.getValue(updateCommandFields[i], item);
			}
			updateCommandArguments[updateCommandArguments.length - 1] =
					OrmFieldInvoker.getValue(deleteCommandFields[0], item);
		}
		return updateCommandArguments;
	}

	public String getDeleteCommand() {
		return deleteCommand;
	}

	public <T> Object[] getDeleteCommandArguments(T item)
			throws ReflectiveOperationException {
		return new Object[] { OrmFieldInvoker.getValue(deleteCommandFields[0], item) };
	}

	public String getSelectIdentityCommand() {
		return selectIdentityColumn;
	}

	public <T> Object getSelectIdentityCommandArgument(T item)
			throws ReflectiveOperationException {
		return OrmFieldInvoker.getValue(deleteCommandFields[0], item);
	}

	public OrmPrimaryKeyTypes getPrimaryKeyType() {
		return primaryKeyType;
	}

	public Field getPrimaryKeyField() {
		return deleteCommandFields[0];
	}
	
}
