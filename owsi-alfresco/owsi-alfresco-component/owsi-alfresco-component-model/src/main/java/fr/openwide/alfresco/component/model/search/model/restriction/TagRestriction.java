package fr.openwide.alfresco.component.model.search.model.restriction;

public class TagRestriction extends Restriction {

	private final String value;

	public TagRestriction(RestrictionBuilder parent, String value) {
		super(parent);
		this.value = value;
	}

	@Override
	protected String toFtsQueryInternal() {
		return (value != null) ? "TAG:" + toFtsLuceneValue(value) : "";
	}

}
