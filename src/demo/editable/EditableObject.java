package demo.editable;

public interface EditableObject<M> {

	default EditableTransaction<M> beginTransaction() {
		return null;
	}
	
	default void commitTransaction() {
	}
	
	default void commitTransaction(EditableTransaction<M> transaction) {
	}
	
	default void rollbackTransaction() {
	}
	
	default void rollbackTransaction(EditableTransaction<M> transaction) {
	}
	
	default void releaseTransactions() {
	}

	@SuppressWarnings("unchecked")
	default M asEditable() {
		return EditableAdapter.adapt((M) this);
	}
	
}