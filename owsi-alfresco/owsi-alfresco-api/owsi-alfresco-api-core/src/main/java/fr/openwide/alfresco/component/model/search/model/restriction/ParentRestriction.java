package fr.openwide.alfresco.component.model.search.model.restriction;

import com.google.common.base.Objects;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class ParentRestriction extends Restriction {

	private final NodeReference parentRef;
	private boolean primary = false;

	public ParentRestriction(RestrictionBuilder parent, NodeReference parentRef) {
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
	
	public ParentRestriction primary() {
		primary = true;
		return this;
	}

	@Override
	protected String toFtsQueryInternal() {
		return ((primary) ? "PRIMARYPARENT:" : "PARENT:") + parentRef.getReference().replace(":", "\\:");
	}

}
