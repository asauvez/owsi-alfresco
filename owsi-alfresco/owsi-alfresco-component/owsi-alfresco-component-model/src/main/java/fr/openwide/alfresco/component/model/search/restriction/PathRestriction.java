package fr.openwide.alfresco.component.model.search.restriction;


public class PathRestriction extends Restriction {

	private final String path;

	public PathRestriction(RestrictionBuilder parent, String path) {
		super(parent);
		this.path = path;
	}

	@Override
	protected String toQueryInternal() {
		return "PATH:" + toLuceneValue(null, path);
	}

}
