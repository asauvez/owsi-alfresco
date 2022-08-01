package fr.openwide.alfresco.component.model.search.model.restriction;

import org.alfresco.service.cmr.repository.NodeRef;

public class IdRestriction extends Restriction {

	private final NodeRef nodeRef;

	public IdRestriction(RestrictionBuilder parent, NodeRef nodeRef) {
		super(parent);
		this.nodeRef = nodeRef;
	}

	@Override
	protected String toFtsQueryInternal() {
		return "ID:" + nodeRef.toString().replace(":", "\\:");
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		return (nodeRef != null) ? "o.cmis:objectId =" + toCmisLuceneValue(nodeRef.toString()) : "";
	}

}
