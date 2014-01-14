package fr.openwide.alfresco.component.query.form.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import fr.openwide.alfresco.app.web.pagination.Pagination;
import fr.openwide.alfresco.component.query.form.projection.ProjectionColumn;

public class FormQueryResult<T> {

	private final List<ProjectionColumn<T>> columns = new ArrayList<>();
	private List<T> rows = new ArrayList<>();
	private Pagination pagination = null;

	public List<ProjectionColumn<T>> getColumns() {
		return columns;
	}

	public List<T> getRows() {
		return rows;
	}
	public FormQueryResult<T> setRows(List<T> rows) {
		this.rows = rows;
		return this;
	}

	public Iterator<RowFormQueryResult<T>> getIterator() {
		return Iterators.transform(rows.iterator(), new Function<T, RowFormQueryResult<T>>() {
			@Override
			public RowFormQueryResult<T> apply(T input) {
				return new RowFormQueryResult<T>(columns, input);
			}
		});
	}

	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
}
