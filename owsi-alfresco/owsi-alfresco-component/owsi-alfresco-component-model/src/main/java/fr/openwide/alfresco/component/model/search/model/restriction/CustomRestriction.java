package fr.openwide.alfresco.component.model.search.model.restriction;

public class CustomRestriction extends Restriction {

	private String customQuery;

	public CustomRestriction(RestrictionBuilder parent, String customQuery) {
		super(parent);
		this.customQuery = customQuery;
	}

	@Override
	protected String toQueryInternal() {
		return customQuery;
	}

}
