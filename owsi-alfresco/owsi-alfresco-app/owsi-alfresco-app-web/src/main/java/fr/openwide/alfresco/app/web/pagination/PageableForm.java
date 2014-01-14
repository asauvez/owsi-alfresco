package fr.openwide.alfresco.app.web.pagination;

public abstract class PageableForm {

	private PaginationParams pagination = new PaginationParams();

	public PaginationParams getPagination() {
		return pagination;
	}
	public void setPagination(PaginationParams pagination) {
		this.pagination = pagination;
	}
}
