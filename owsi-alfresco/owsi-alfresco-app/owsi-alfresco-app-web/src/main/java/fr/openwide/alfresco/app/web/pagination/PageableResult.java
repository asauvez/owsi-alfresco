package fr.openwide.alfresco.app.web.pagination;

import java.util.List;

public class PageableResult<T> extends PageableForm {

	private List<T> items;

	private int total;

	private int lastPageNumber;

	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

	public int getLastPageNumber() {
		return lastPageNumber;
	}
	public void setLastPageNumber(int lastPageNumber) {
		this.lastPageNumber = lastPageNumber;
	}

}