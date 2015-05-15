package fr.openwide.alfresco.app.web.pagination;

import java.io.Serializable;

public class SortParameters implements Serializable {

	private static final long serialVersionUID = -8019001561774052701L;

	public enum SortDirection { NONE, ASC, DESC }

	private String column = null;
	private SortDirection direction = SortDirection.NONE;

	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public SortDirection getDirection() {
		return direction;
	}
	public void setDirection(SortDirection direction) {
		this.direction = direction;
	}

}
