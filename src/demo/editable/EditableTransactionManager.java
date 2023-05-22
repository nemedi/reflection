package demo.editable;

import java.util.Stack;

public class EditableTransactionManager<M> {

	private M model;
	private Stack<EditableTransaction<M>> transactions;

	public EditableTransactionManager(M model) {
		this.model = model;
		this.transactions = new Stack<EditableTransaction<M>>();
	}

	public M getModel() {
		return model;
	}
	
	public EditableTransaction<M> beginTransaction() throws EditableException {
		EditableTransaction<M> transaction = new EditableTransaction<M>(this.model);
		transactions.push(transaction);
		return transaction;
	}
	
	public void commitTransaction() throws EditableException {
		if (transactions.size() > 0
				&& transactions.peek() != null) {
			transactions.pop().commit();
		}
	}
	
	public void commitTransaction(EditableTransaction<M> transaction) throws EditableException {
		if (transaction != null
				&& transactions.contains(transaction)) {
			int index = transactions.indexOf(transaction);
			while (transactions.size() >= index) {
				transactions.pop();
			}
			transaction.commit();
		}
	}
	
	public void rollbackTransaction() throws EditableException {
		if (transactions.size() > 0
				&& transactions.peek() != null) {
			transactions.pop().rollback();
		}
	}
	
	public void rollbackTransaction(EditableTransaction<M> transaction) throws EditableException {
		if (transaction != null
				&& transactions.contains(transaction)) {
			int index = transactions.indexOf(transaction);
			while (transactions.size() >= index) {
				transactions.pop();
			}
			transaction.rollback();
		}		
	}
	
	public void releaseTransactions() {
		transactions.clear();
	}

}
