package fr.openwide.alfresco.component.model.search.restriction;

public class MatchAllRestriction extends Restriction {

	private final String value;

	public MatchAllRestriction(RestrictionBuilder parent, String value) {
		super(parent);
		this.value = value;
	}

	@Override
	protected String toLuceneQueryInternal() {
		return (value != null) ? "ALL:" + toLuceneValue(null, value) : "";
	}

}
