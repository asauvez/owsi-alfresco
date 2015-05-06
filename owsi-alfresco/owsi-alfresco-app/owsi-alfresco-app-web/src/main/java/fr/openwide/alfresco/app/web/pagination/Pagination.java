package fr.openwide.alfresco.app.web.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import fr.openwide.alfresco.app.web.pagination.SortParameters.SortDirection;

public class Pagination implements Serializable {

	private final PaginationParameters params;
	private int nbItemsTotal;
	private final UriComponents uriComponentsPage;
	private final UriComponents uriComponentsSort;

	public Pagination(PaginationParameters params, UriComponentsBuilder uriComponentsBuilder) {
		this.params = params;
		this.uriComponentsPage = uriComponentsBuilder
				.replaceQueryParam("pagination.currentPage", "{page}")
				.build();
		this.uriComponentsSort = uriComponentsBuilder
				.replaceQueryParam("pagination.currentPage")
				.replaceQueryParam("pagination.sort.column", "{column}")
				.replaceQueryParam("pagination.sort.direction", "{direction}")
				.build();
	}
	
	public void setNbItemsTotal(int nbItemsTotal) {
		this.nbItemsTotal = nbItemsTotal;
	}
	
	public <T> List<T> filterList(List<T> list) {
		setNbItemsTotal(list.size());
		this.params.setCurrentPage(Math.max(Math.min(getCurrentPage(), getLastPage()), getFirstPage()));
		return isHasPagination() ? list.subList(params.getFirstResult(), Math.min(list.size(), getCurrentPage()*getNbItemsPerPage()))
				                 : list;
	}

	public String getPageUri(int page) {
		return "#!" + StringUtils.substringAfter(uriComponentsPage.expand(page).toUriString(), "?");
	}
	
	public int getCurrentPage() {
		return params.getCurrentPage();
	}
	public String getCurrentPageUrl() {
		return getPageUri(params.getCurrentPage());
	}
	public int getNbItemsPerPage() {
		return params.getNbItemsPerPage();
	}
	public Integer getNbItemsTotal() {
		return nbItemsTotal;
	}
	public boolean isHasPagination() {
		return nbItemsTotal > getNbItemsPerPage();
	}
	public boolean isHasPreviousPage() {
		return getCurrentPage() > 1;
	}
	public int getPreviousPage() {
		return Math.max(getCurrentPage() - 1, getFirstPage());
	}
	public String getPreviousPageUri() {
		return getPageUri(getPreviousPage());
	}
	public int getFirstPage() {
		return 1;
	}
	public String getFirstPageUri() {
		return getPageUri(getFirstPage());
	}
	public int getLastPage() {
		return nbItemsTotal / getNbItemsPerPage() + ((nbItemsTotal % getNbItemsPerPage() != 0) ? 1 : 0);
	}
	public String getLastPageUri() {
		return getPageUri(getLastPage());
	}
	
	public static class PageLink {
		private Integer number;
		private String label;
		private String link;
		public PageLink(Integer number, String label, String link) {
			this.number = number;
			this.label = label;
			this.link = link;
		}
		public String getLabel() {
			return label;
		}
		public String getLink() {
			return link;
		}
		public Integer getNumber() {
			return number;
		}
	}
	public List<PageLink> getPageLinks() {
		int currentPage = getCurrentPage();
		int lastPage = getLastPage();
		int nbPagesToDisplay = params.getNbPagesToDisplay();
		
		List<PageLink> pages = new ArrayList<PageLink>();
		for (int page=getFirstPage(); page<=lastPage; page++) {
			if (page == 1 || page == lastPage || Math.abs(page - currentPage) < nbPagesToDisplay) {
				pages.add(new PageLink(page, Integer.toString(page), 
						(page != currentPage) ? getPageUri(page) : null));
			} else if (pages.get(pages.size()-1).getNumber() != null) {
				pages.add(new PageLink(null, "&#133;", null));
			}
		}
		return pages;
	}
	public boolean isHasNextPage() {
		return getCurrentPage() < getLastPage();
	}
	public int getNextPage() {
		return Math.min(getCurrentPage() + 1, getLastPage());
	}
	public String getNextPageUri() {
		return getPageUri(getNextPage());
	}

	public SortParameters getSort() {
		return params.getSort();
	}
	public Map<String, String> getSortUri() {
		return new HashMap<String, String>() {
			@Override
			public String get(Object column) {
				SortDirection direction = 
						(   column.equals(params.getSort().getColumn()) 
						 && params.getSort().getDirection() == SortDirection.ASC) 
						? SortDirection.DESC : SortDirection.ASC;
				return "#!" + StringUtils.substringAfter(uriComponentsSort.expand(column, direction).toUriString(), "?");
			}
		};
	}
}
