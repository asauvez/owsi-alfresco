package fr.openwide.alfresco.component.model.search.model.restriction;

import org.alfresco.service.cmr.repository.NodeRef;

public class AncestorRestriction extends Restriction {

	private final NodeRef parentRef;

	public AncestorRestriction(RestrictionBuilder parent, NodeRef parentRef) {
		super(parent);
		this.parentRef = parentRef;
	}

	@Override
	protected String toFtsQueryInternal() {
		return "ANCESTOR:" + parentRef.toString().replace(":", "\\:");
	}

}
