package fr.openwide.alfresco.app.web.pagination;

import java.util.List;

public class PageableResult<T> {

	private Pagination pagination;
	private List<T> items;

	public PageableResult(Pagination pagination, List<T> items) {
		this.pagination = pagination;
		this.items = items;
	}
	
	public Pagination getPagination() {
		return pagination;
	}
	public List<T> getItems() {
		return items;
	}

}