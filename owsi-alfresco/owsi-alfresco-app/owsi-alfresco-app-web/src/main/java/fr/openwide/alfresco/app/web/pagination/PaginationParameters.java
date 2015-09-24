package fr.openwide.alfresco.app.web.pagination;

import java.io.Serializable;

public class PaginationParameters implements Serializable {

	private static final long serialVersionUID = 555065374685286649L;

	private SortParameters sort = new SortParameters();

	private int currentPage = 1;
	private int nbItemsPerPage = Integer.MAX_VALUE;
	private int nbPagesToDisplay = 5;

	public SortParameters getSort() {
		return sort;
	}
	public void setSort(SortParameters sort) {
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
	public int getNbPagesToDisplay() {
		return nbPagesToDisplay;
	}
	public void setNbPagesToDisplay(int nbPagesToDisplay) {
		this.nbPagesToDisplay = nbPagesToDisplay;
	}

	/** @see org.hibernate.Query#setFirstResult(int) */
	public int getFirstResult() {
		return (currentPage-1)*nbItemsPerPage;
	}
	/** @see org.hibernate.Query#setMaxResults(int) */
	public int getMaxResults() {
		return nbItemsPerPage;
	}

}
