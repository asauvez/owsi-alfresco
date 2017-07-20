package fr.openwide.alfresco.component.model.search.model.restriction;

public class MatchAllRestriction extends Restriction {

	private final String value;

	public MatchAllRestriction(RestrictionBuilder parent, String value) {
		super(parent);
		this.value = value;
	}

	@Override
	protected String toFtsQueryInternal() {
		return (value != null) ? "ALL:" + toFtsLuceneValue(value) : "";
	}

}
