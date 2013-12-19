package fr.openwide.alfresco.component.model.search.restriction;

import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class ParentRestriction extends Restriction {

	private final NodeReference parentRef;
	private boolean primary = false;

	public ParentRestriction(RestrictionBuilder parent, NodeReference parentRef) {
		super(parent);
		this.parentRef = parentRef;
	}

	public ParentRestriction primary() {
		primary = true;
		return this;
	}

	@Override
	protected String toLuceneQueryInternal() {
		return ((primary) ? "PRIMARYPARENT:" : "PARENT:") + parentRef.getReference();
	}

}
