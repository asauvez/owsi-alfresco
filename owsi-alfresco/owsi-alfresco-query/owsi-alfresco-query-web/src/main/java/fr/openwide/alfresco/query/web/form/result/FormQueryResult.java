package fr.openwide.alfresco.query.web.form.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class FormQueryResult<T> {

	public final List<ColumnFormQueryResult<T>> columns = new ArrayList<ColumnFormQueryResult<T>>();
	public List<T> rows = new ArrayList<T>();

	public List<ColumnFormQueryResult<T>> getColumns() {
		return columns;
	}

	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Iterator<RowFormQueryResult<T>> getIterator() {
		return Iterators.transform(rows.iterator(), new Function<T, RowFormQueryResult<T>>() {
			@Override
			public RowFormQueryResult<T> apply(T input) {
				return new RowFormQueryResult<T>(columns, input);
			}
		});
	}

}
