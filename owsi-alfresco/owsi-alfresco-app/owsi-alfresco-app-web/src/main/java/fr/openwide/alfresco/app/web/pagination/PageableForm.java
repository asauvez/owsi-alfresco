package fr.openwide.alfresco.app.web.pagination;

public abstract class PageableForm {

	private PaginationParameters pagination = new PaginationParameters();

	public PaginationParameters getPagination() {
		return pagination;
	}
	public void setPagination(PaginationParameters pagination) {
		this.pagination = pagination;
	}
}
