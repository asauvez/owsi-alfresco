package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class IdRestriction extends Restriction {

	private final NodeReference nodeRef;

	public IdRestriction(RestrictionBuilder parent, NodeReference nodeRef) {
		super(parent);
		this.nodeRef = nodeRef;
	}

	@Override
	protected String toQueryInternal() {
		return "ID" + nodeRef.getReference().replace(":", "\\:");
	}

}
