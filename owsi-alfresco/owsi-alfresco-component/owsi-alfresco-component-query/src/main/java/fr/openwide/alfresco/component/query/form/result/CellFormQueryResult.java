package fr.openwide.alfresco.component.query.form.result;

import fr.openwide.alfresco.component.query.form.projection.ProjectionColumn;

public class CellFormQueryResult<I> {

	private final ProjectionColumn<I> column;
	private final I item;

	public CellFormQueryResult(ProjectionColumn<I> column, I item) {
		this.column = column;
		this.item = item;
	}

	public Object getValue() {
		return column.getItemTransformer().apply(item);
	}

	public ProjectionColumn<I> getColumn() {
		return column;
	}

	public I getRow() {
		return item;
	}

}
