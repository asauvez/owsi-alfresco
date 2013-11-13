package fr.openwide.alfresco.query.web.search.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaginationParams implements Serializable {
	
	public enum SortDirection { NONE, ASC, DESC }
	
	private Integer sortColumn = null;
	private SortDirection sortDirection = SortDirection.NONE;
	
	private int currentPage = 1;
	private int nbItemsPerPage = Integer.MAX_VALUE;
	private Integer nbItemsTotal = null;
	
	public Integer getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(Integer sortColumn) {
		this.sortColumn = sortColumn;
	}
	public SortDirection getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
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
	
	public Integer getNbItemsTotal() {
		return nbItemsTotal;
	}
	public boolean isInitialized() {
		return (nbItemsTotal != null);
	}
	
	public boolean isHasPagination() {
		return nbItemsTotal > nbItemsPerPage;
	}
	public boolean isHasPreviousPage() {
		return currentPage > 1;
	}
	public int getPreviousPage() {
		return currentPage - 1;
	}
	public int getFirstPage() {
		return 1;
	}
	public int getLastPage() {
		return nbItemsTotal / nbItemsPerPage + ((nbItemsTotal % nbItemsPerPage != 0) ? 1 : 0);
	}
	public List<Integer> getPages() {
		List<Integer> pages = new ArrayList<>();
		for (int page=getFirstPage(); page<=getLastPage(); page++) {
			pages.add(page);
		}
		return pages;
	}
	public boolean isHasNextPage() {
		return currentPage < getLastPage(); 
	}
	public int getNextPage() {
		return currentPage + 1;
	}
	
	public <T> List<T> filterList(List<T> list) {
		this.nbItemsTotal = list.size();
		this.currentPage = Math.min(currentPage, getLastPage());
		return isHasPagination() ? list.subList(getFirstResult(), Math.min(list.size(), currentPage*nbItemsPerPage)) 
				                 : list;
	}
	
	// Pour Hibernate
	public int getFirstResult() {
		return (currentPage-1)*nbItemsPerPage; 
	}
	public int getMaxResult() {
		return nbItemsPerPage;
	}
}
