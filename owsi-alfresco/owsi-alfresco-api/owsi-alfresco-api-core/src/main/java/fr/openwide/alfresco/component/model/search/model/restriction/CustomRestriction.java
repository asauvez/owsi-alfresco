package fr.openwide.alfresco.component.model.search.model.restriction;

public class CustomRestriction extends Restriction {

	private String customFtsQuery;

	public CustomRestriction(RestrictionBuilder parent, String customFtsQuery) {
		super(parent);
		this.customFtsQuery = customFtsQuery;
	}

	@Override
	protected String toFtsQueryInternal() {
		return customFtsQuery;
	}
	@Override
	protected String toCmisQueryWhereInternal() {
		return customFtsQuery;
	}

}
