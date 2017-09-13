package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class IdRestriction extends Restriction {

	private final NodeReference nodeReference;

	public IdRestriction(RestrictionBuilder parent, NodeReference nodeRef) {
		super(parent);
		this.nodeReference = nodeRef;
	}

	@Override
	public void testInit(NodeScopeBuilder nodeScopeBuilder) {
		nodeScopeBuilder.nodeReference();
	}
	@Override
	public boolean test(BusinessNode node) {
		return nodeReference.equals(node.getNodeReference());
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
