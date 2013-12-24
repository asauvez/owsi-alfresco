package fr.openwide.alfresco.app.web.pagination;

public abstract class PageableForm {

	private int pageNumber;

	private Sort sort = new Sort();

	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Sort getSort() {
		return sort;
	}
	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public static class Sort {

		public static enum Direction {
			asc,
			desc
		}

		private Direction direction = Direction.asc;

		private String property;

		public Direction getDirection() {
			return direction;
		}
		public void setDirection(Direction direction) {
			this.direction = direction;
		}

		public String getProperty() {
			return property;
		}
		public void setProperty(String property) {
			this.property = property;
		}

	}

}
