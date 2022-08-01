package fr.openwide.alfresco.component.model.search.model.restriction;

import org.alfresco.service.cmr.repository.NodeRef;

public class ParentRestriction extends Restriction {

	private final NodeRef parentRef;
	private boolean primary = false;

	public ParentRestriction(RestrictionBuilder parent, NodeRef parentRef) {
		super(parent);
		this.parentRef = parentRef;
	}
	
	public ParentRestriction primary() {
		primary = true;
		return this;
	}

	@Override
	protected String toFtsQueryInternal() {
		return ((primary) ? "PRIMARYPARENT:" : "PARENT:") + parentRef.toString().replace(":", "\\:");
	}

}
