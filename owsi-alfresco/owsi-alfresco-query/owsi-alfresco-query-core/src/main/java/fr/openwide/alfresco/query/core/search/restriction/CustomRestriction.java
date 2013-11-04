package fr.openwide.alfresco.query.core.search.restriction;

public class CustomRestriction extends Restriction {

	private String customQuery;

	public CustomRestriction(RestrictionBuilder parent, String customQuery) {
		super(parent);
		this.customQuery = customQuery;
	}

	@Override
	protected String toLuceneQueryInternal() {
		return customQuery;
	}

}
