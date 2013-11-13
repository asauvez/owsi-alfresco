package fr.openwide.alfresco.query.web.form.result;

public class CellFormQueryResult<T> {

	private final ColumnFormQueryResult<T> column;
	private final T item;

	public CellFormQueryResult(ColumnFormQueryResult<T> column, T item) {
		this.column = column;
		this.item = item;
	}

	public Object getValue() {
		return column.getTransformer().apply(item);
	}

	public ColumnFormQueryResult<T> getColumn() {
		return column;
	}
	
	public T getRow() {
		return item;
	}

}
