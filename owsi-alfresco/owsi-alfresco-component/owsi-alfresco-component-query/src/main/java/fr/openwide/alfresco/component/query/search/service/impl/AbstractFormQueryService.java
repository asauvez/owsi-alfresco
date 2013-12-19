package fr.openwide.alfresco.component.query.search.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Ordering;

import fr.openwide.alfresco.component.query.form.projection.ProjectionBuilder;
import fr.openwide.alfresco.component.query.form.projection.ProjectionColumn;
import fr.openwide.alfresco.component.query.form.projection.ProjectionImpl;
import fr.openwide.alfresco.component.query.form.result.FormQueryResult;
import fr.openwide.alfresco.component.query.form.util.MessageUtils;
import fr.openwide.alfresco.component.query.search.model.AbstractFormQuery;
import fr.openwide.alfresco.component.query.search.model.PaginationParams;
import fr.openwide.alfresco.component.query.search.model.PaginationParams.SortDirection;

public class AbstractFormQueryService {

	protected <I> FormQueryResult<I> createQueryResult(AbstractFormQuery<I> formQuery, ProjectionBuilder<I, ?> projectionBuilder) {
		FormQueryResult<I> result = new FormQueryResult<I>();
		Map<String, Integer> alreadyUseIds = new HashMap<>();
		for (ProjectionImpl<I, ?, ?> projection : projectionBuilder.getProjections()) {
			String id = projection.getId();
			if (id == null) {
				id = projection.getDefaultLabelCode();
			}
			Integer n = alreadyUseIds.get(id);
			alreadyUseIds.put(id, (n != null) ? n + 1 : 1);
			projection.id((n != null) ? id + "_" + n: id);
			
			if (projection.getLabel() == null) {
				projection.setLabel(MessageUtils.codes(
					formQuery.getClass().getName() + "." + projection.getDefaultLabelCode(),
					formQuery.getClass().getSimpleName() + "." + projection.getDefaultLabelCode(),
					projection.getDefaultLabelCode()));
			}
			result.getColumns().add(projection);
		}
		return result;
	}

	protected <I> FormQueryResult<I> initResult(AbstractFormQuery<I> formQuery, final FormQueryResult<I> result, List<I> list) {
		List<I> rows = new ArrayList<>();
		formQuery.initResult(result);

		// Filtre
		for (I item : list) {
			if (formQuery.filterResult(item)) {
				rows.add(item);
			}
		}

		PaginationParams pagination = formQuery.getPagination();
		result.setPagination(pagination);

		// Tri
		if (pagination.getSortColumn() != null) {
			for (ProjectionColumn<I> column : result.getColumns()) {
				if (pagination.getSortColumn().equals(column.getId())) {
					column.sort(pagination.getSortDirection(), Integer.MAX_VALUE);
				}
			}
		}

		Set<ProjectionColumn<I>> sortColumns = new TreeSet<ProjectionColumn<I>>(new Comparator<ProjectionColumn<I>>() {
			@Override
			public int compare(ProjectionColumn<I> p1, ProjectionColumn<I> p2) {
				int d = p2.getSortPriority() - p1.getSortPriority();
				if (d != 0) {
					return d;
				}
				return result.getColumns().indexOf(p1) - result.getColumns().indexOf(p2);
			}
		});
		for (ProjectionColumn<I> projection : result.getColumns()) {
			if (projection.getItemComparator() != null && projection.getSortDirection() != SortDirection.NONE) {
				sortColumns.add(projection);
			}
		}

		if (! sortColumns.isEmpty()) {
			List<Comparator<I>> comparators = new ArrayList<>();
			for (ProjectionColumn<I> projection : sortColumns) {
				switch (projection.getSortDirection()) {
				case NONE: break;
				case ASC:  comparators.add(projection.getItemComparator()); break;
				case DESC: comparators.add(Ordering.from(projection.getItemComparator()).reverse()); break;
				}
			}
			Collections.sort(rows, Ordering.compound(comparators));

			ProjectionColumn<I> firstSortColumn = sortColumns.iterator().next();
			for (ProjectionColumn<I> column : result.getColumns()) {
				if (column != firstSortColumn) {
					column.sort(SortDirection.NONE, 0);
				}
			}
		}

		// Pagination
		rows = pagination.filterList(rows);
		result.setRows(rows);

		return result;
	}

}
