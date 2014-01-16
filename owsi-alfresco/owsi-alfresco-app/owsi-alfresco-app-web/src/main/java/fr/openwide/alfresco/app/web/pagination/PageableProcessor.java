package fr.openwide.alfresco.app.web.pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.collect.Ordering;

import fr.openwide.alfresco.app.web.pagination.SortParams.SortDirection;
import fr.openwide.core.spring.util.StringUtils;

public class PageableProcessor<T> {

	private Map<String, PageablePropertyDefinition<T, ?>> propertyDefinitions = new HashMap<String, PageablePropertyDefinition<T, ?>>();

	private int pageSize;

	public PageableResult<T> paginate(Collection<T> items, PageableForm pageable, HttpServletRequest request) {
		PaginationParams paginationParams = pageable.getPagination();
		paginationParams.setNbItemsPerPage(pageSize);
		List<T> allItems = new ArrayList<T>(items);
				
		// Tri
		if (paginationParams.getSort() != null && StringUtils.hasLength(paginationParams.getSort().getColumn())) {
			final PageablePropertyDefinition<T, ?> columnDefinition = propertyDefinitions.get(paginationParams.getSort().getColumn());
			Comparator<T> columnComparator = new Comparator<T>() {
				@Override
				public int compare(T o1, T o2) {
					return Ordering.natural().nullsFirst().compare(columnDefinition.getPropertyValue(o1), columnDefinition.getPropertyValue(o2));
				}
			};
			if (SortDirection.DESC.equals(paginationParams.getSort().getDirection())) {
				Collections.sort(allItems, Collections.reverseOrder(columnComparator));
			} else {
				Collections.sort(allItems, columnComparator);
			}
		}
		
		// Pagination
		Pagination pagination = new Pagination(paginationParams, ServletUriComponentsBuilder.fromRequest(request));
		return new PageableResult<T>(
				pagination, 
				pagination.filterList(allItems));
	}

	public void addPropertyDefinition(String property, PageablePropertyDefinition<T, ?> definition) {
		propertyDefinitions.put(property, definition);
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}