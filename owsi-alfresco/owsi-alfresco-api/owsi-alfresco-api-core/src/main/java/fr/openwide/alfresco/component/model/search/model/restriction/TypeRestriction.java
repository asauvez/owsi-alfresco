package fr.openwide.alfresco.component.model.search.model.restriction;

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
	protected String toFtsQueryInternal() {
		return ((exact) ? "EXACTTYPE:" : "TYPE:") + type.toLucene();
	}

}
