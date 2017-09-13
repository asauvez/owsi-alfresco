package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.TypeModel;

public class TypeRestriction extends Restriction {

	private final TypeModel type;
	private boolean exact = false;

	public TypeRestriction(RestrictionBuilder parent, TypeModel type) {
		super(parent);
		this.type = type;
	}

	public TypeRestriction exact() {
		exact = true;
		return this;
	}

	@Override
	public void testInit(NodeScopeBuilder nodeScopeBuilder) {
		nodeScopeBuilder.type();
	}
	@Override
	public boolean test(BusinessNode node) {
		return node.isType(type);
	}
	
	@Override
	protected String toFtsQueryInternal() {
		return ((exact) ? "EXACTTYPE:" : "TYPE:") + type.toLucene();
	}

}
