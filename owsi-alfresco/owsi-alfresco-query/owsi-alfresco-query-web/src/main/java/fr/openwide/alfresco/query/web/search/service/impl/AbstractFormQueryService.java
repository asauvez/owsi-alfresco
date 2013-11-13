package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Ordering;

import fr.openwide.alfresco.query.web.form.result.ColumnFormQueryResult;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.AbstractFormQuery;
import fr.openwide.alfresco.query.web.search.model.PaginationParams;
import fr.openwide.alfresco.query.web.search.model.PaginationParams.SortDirection;

public class AbstractFormQueryService {
	
	protected <T> FormQueryResult<T> initResult(AbstractFormQuery<T> formQuery, final FormQueryResult<T> result, List<T> list) {
		List<T> rows = new ArrayList<>();
		formQuery.initResult(result);

		// Filtre
		for (T item : list) {
			if (formQuery.filterResult(item)) {
				rows.add(item);
			}
		}
		
		PaginationParams pagination = formQuery.getPagination();
		result.setPagination(pagination);
		
		// Tri
		if (pagination.getSortColumn() != null) { 
			ColumnFormQueryResult<T> sortColumn = result.getColumns().get(Math.min(pagination.getSortColumn(), result.getColumns().size()-1));
			sortColumn.sort(pagination.getSortDirection(), Integer.MAX_VALUE);
		}
		
		Set<ColumnFormQueryResult<T>> sortColumns = new TreeSet<ColumnFormQueryResult<T>>(new Comparator<ColumnFormQueryResult<T>>() {
			@Override
			public int compare(ColumnFormQueryResult<T> o1, ColumnFormQueryResult<T> o2) {
				int d = o2.getSortPriority() - o1.getSortPriority();
				if (d != 0) {
					return d;
				}
				return result.getColumns().indexOf(o1) - result.getColumns().indexOf(o2);
			}
		});
		for (ColumnFormQueryResult<T> column : result.getColumns()) {
			if (column.getComparator() != null && column.getSortDirection() != SortDirection.NONE) {
				sortColumns.add(column);
			}
		}
		
		List<Comparator<T>> comparators = new ArrayList<>();
		for (ColumnFormQueryResult<T> column : sortColumns) {
			switch (column.getSortDirection()) {
			case NONE: break;
			case ASC:  comparators.add(column.getComparator()); break;
			case DESC: comparators.add(Ordering.from(column.getComparator()).reverse()); break;
			}
		}
		Collections.sort(rows, Ordering.compound(comparators));

		ColumnFormQueryResult<T> firstSortColumn = sortColumns.iterator().next();
		for (ColumnFormQueryResult<T> column : result.getColumns()) {
			if (column != firstSortColumn) {
				column.sort(SortDirection.NONE, 0);
			}
		}

		// Pagination
		rows = pagination.filterList(rows);
		result.setRows(rows);
		
		return result;
	}
	
}
