package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class IdRestriction extends Restriction {

	private final NodeReference nodeReference;

	public IdRestriction(RestrictionBuilder parent, NodeReference nodeRef) {
		super(parent);
		this.nodeReference = nodeRef;
	}

	@Override
	protected String toFtsQueryInternal() {
		return "ID:" + nodeReference.getReference().replace(":", "\\:");
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		return (nodeReference.getReference() != null) ? "o.cmis:objectId =" + toCmisLuceneValue(nodeReference.getReference()) : "";
	}

}
