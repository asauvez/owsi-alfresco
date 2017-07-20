package fr.openwide.alfresco.component.model.search.model.restriction;

public class MatchTextRestriction extends Restriction {

	private final String value;

	public MatchTextRestriction(RestrictionBuilder parent, String value) {
		super(parent);
		this.value = value;
	}

	@Override
	protected String toFtsQueryInternal() {
		return (value != null) ? "TEXT:" + toFtsLuceneValue(value) : "";
	}

}
