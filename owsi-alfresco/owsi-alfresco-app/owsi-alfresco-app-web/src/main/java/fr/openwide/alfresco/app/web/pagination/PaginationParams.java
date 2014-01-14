package fr.openwide.alfresco.app.web.pagination;

import java.io.Serializable;

public class PaginationParams implements Serializable {

	private SortParams sort = new SortParams();

	private int currentPage = 1;
	private int nbItemsPerPage = Integer.MAX_VALUE;

	public SortParams getSort() {
		return sort;
	}
	public void setSort(SortParams sort) {
		this.sort = sort;
	}

	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getNbItemsPerPage() {
		return nbItemsPerPage;
	}
	public void setNbItemsPerPage(int nbItemsPerPage) {
		this.nbItemsPerPage = nbItemsPerPage;
	}

	// Pour hibernate
	public int getFirstResult() {
		return (currentPage-1)*nbItemsPerPage;
	}
	public int getMaxResult() {
		return nbItemsPerPage;
	}

}
