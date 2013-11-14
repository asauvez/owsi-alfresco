package fr.openwide.alfresco.query.web.form.result;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import fr.openwide.alfresco.query.web.form.projection.ProjectionColumn;

public class RowFormQueryResult<I> {

	private final List<ProjectionColumn<I>> columns;
	private final I item;

	public RowFormQueryResult(List<ProjectionColumn<I>> columns, I item) {
		this.columns = columns;
		this.item = item;
	}

	public Iterator<CellFormQueryResult<I>> getIterator() {
		return Iterators.transform(columns.iterator(), new Function<ProjectionColumn<I>, CellFormQueryResult<I>>() {
			@Override
			public CellFormQueryResult<I> apply(ProjectionColumn<I> projection) {
				return new CellFormQueryResult<I>(projection, item);
			}
		});
	}

}
