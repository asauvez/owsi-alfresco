package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class AncestorRestriction extends Restriction {

	private final NodeReference parentRef;

	public AncestorRestriction(RestrictionBuilder parent, NodeReference parentRef) {
		super(parent);
		this.parentRef = parentRef;
	}

	@Override
	protected String toFtsQueryInternal() {
		return "ANCESTOR:" + parentRef.getReference().replace(":", "\\:");
	}

}
