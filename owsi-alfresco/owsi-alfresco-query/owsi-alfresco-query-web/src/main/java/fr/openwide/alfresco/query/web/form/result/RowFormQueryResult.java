package fr.openwide.alfresco.query.web.form.result;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class RowFormQueryResult<T> {

	private final List<ColumnFormQueryResult<T>> columns;
	private final T item;

	public RowFormQueryResult(List<ColumnFormQueryResult<T>> columns, T item) {
		this.columns = columns;
		this.item = item;
	}

	public Iterator<CellFormQueryResult<T>> getIterator() {
		return Iterators.transform(columns.iterator(), new Function<ColumnFormQueryResult<T>, CellFormQueryResult<T>>() {
			@Override
			public CellFormQueryResult<T> apply(ColumnFormQueryResult<T> column) {
				return new CellFormQueryResult<T>(column, item);
			}
		});
	}

}
