package fr.openwide.alfresco.component.model.search.model.restriction;


public class PathRestriction extends Restriction {

	private final String path;
	private String suffix = "";

	public PathRestriction(RestrictionBuilder parent, String path) {
		super(parent);
		this.path = path;
	}

	public PathRestriction below() {
		suffix = "//*";
		return this;
	}
	public PathRestriction orBelow() {
		suffix = "//.";
		return this;
	}
	
	@Override
	protected String toQueryInternal() {
		return "PATH:" + toLuceneValue(null, path + suffix);
	}

}
