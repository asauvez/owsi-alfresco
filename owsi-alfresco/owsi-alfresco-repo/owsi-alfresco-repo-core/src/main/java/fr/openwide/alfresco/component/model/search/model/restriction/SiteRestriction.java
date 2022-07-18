package fr.openwide.alfresco.component.model.search.model.restriction;

public class SiteRestriction extends Restriction {

	private final String siteShortName;

	public SiteRestriction(RestrictionBuilder parent, String siteShortName) {
		super(parent);
		this.siteShortName = siteShortName;
	}

	@Override
	protected String toFtsQueryInternal() {
		return (siteShortName != null) ? "SITE:" + toFtsLuceneValue(siteShortName) : "";
	}

}
