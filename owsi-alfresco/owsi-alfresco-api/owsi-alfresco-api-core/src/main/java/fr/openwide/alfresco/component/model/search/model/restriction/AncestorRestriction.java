package fr.openwide.alfresco.component.model.search.model.restriction;

import com.google.common.base.Objects;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class AncestorRestriction extends Restriction {

	private final NodeReference parentRef;

	public AncestorRestriction(RestrictionBuilder parent, NodeReference parentRef) {
		super(parent);
		this.parentRef = parentRef;
	}

	@Override
	public void testInit(NodeScopeBuilder nodeScopeBuilder) {
		nodeScopeBuilder.assocs().primaryParent().nodeReference();
	}
	@Override
	public boolean test(BusinessNode node) {
		return Objects.equal(parentRef, node.assocs().primaryParent().getNodeReference());
	}
	
	@Override
	protected String toFtsQueryInternal() {
		return "ANCESTOR:" + parentRef.getReference().replace(":", "\\:");
	}

}
