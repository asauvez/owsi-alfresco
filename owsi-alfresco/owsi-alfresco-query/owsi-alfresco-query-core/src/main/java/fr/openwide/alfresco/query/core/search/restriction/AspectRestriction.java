package fr.openwide.alfresco.query.core.search.restriction;

import fr.openwide.alfresco.query.core.node.model.AspectModel;

public class AspectRestriction extends Restriction {

	private final AspectModel aspect;
	private boolean exact = false;

	public AspectRestriction(RestrictionBuilder parent, AspectModel aspect) {
		super(parent);
		this.aspect = aspect;
	}

	public AspectRestriction exact() {
		exact = true;
		return this;
	}

	@Override
	protected String toLuceneQueryInternal() {
		return ((exact) ? "EXACTASPECT:" : "ASPECT:") + aspect.toLucene();
	}

}
