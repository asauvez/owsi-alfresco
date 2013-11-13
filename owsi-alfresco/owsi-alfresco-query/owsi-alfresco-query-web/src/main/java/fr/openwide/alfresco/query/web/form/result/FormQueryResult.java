package fr.openwide.alfresco.query.web.form.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import fr.openwide.alfresco.query.web.search.model.PaginationParams;

public class FormQueryResult<T> {

	private final List<ColumnFormQueryResult<T>> columns = new ArrayList<>();
	private List<T> rows = new ArrayList<>();
	private PaginationParams pagination = null;

	public List<ColumnFormQueryResult<T>> getColumns() {
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

	public PaginationParams getPagination() {
		return pagination;
	}
	public void setPagination(PaginationParams pagination) {
		this.pagination = pagination;
	}
}
