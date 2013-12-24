package fr.openwide.alfresco.app.web.pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Ordering;

import fr.openwide.core.spring.util.StringUtils;

public class PageableProcessor<T> {

	private Map<String, PageablePropertyDefinition<T, ?>> propertyDefinitions = new HashMap<String, PageablePropertyDefinition<T, ?>>();

	private int pageSize;

	public PageableResult<T> paginate(Collection<T> items, PageableForm pageable) {
		PageableResult<T> result = new PageableResult<T>();
		List<T> allItems = new ArrayList<T>(items);
		
		// Tri
		if (pageable.getSort() != null && StringUtils.hasLength(pageable.getSort().getProperty())) {
			final PageablePropertyDefinition<T, ?> columnDefinition = propertyDefinitions.get(pageable.getSort().getProperty());
			Comparator<T> columnComparator = new Comparator<T>() {
				@Override
				public int compare(T o1, T o2) {
					return Ordering.natural().nullsFirst().compare(columnDefinition.getPropertyValue(o1), columnDefinition.getPropertyValue(o2));
				}
			};
			if (PageableForm.Sort.Direction.desc.equals(pageable.getSort().getDirection())) {
				Collections.sort(allItems, Collections.reverseOrder(columnComparator));
			} else {
				Collections.sort(allItems, columnComparator);
			}
			result.setSort(pageable.getSort());
		}
		
		// Pagination
		int total = allItems.size();
		result.setTotal(total);
		int page = pageable.getPageNumber();
		if (page < 1) {
			page = 1;
		}
		int lastPage = ((total-1) / pageSize) + 1;
		result.setLastPageNumber(lastPage);
		if (page > lastPage) {
			page = lastPage;
		}
		result.setPageNumber(page);
		int fromIndex = pageSize * (page-1);
		int toIndex = pageSize * page;
		if(toIndex > total) {
			toIndex = total;
		}
		result.setItems(allItems.subList(fromIndex, toIndex));
		return result;
	}

	public void addPropertyDefinition(String property, PageablePropertyDefinition<T, ?> definition) {
		propertyDefinitions.put(property, definition);
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}