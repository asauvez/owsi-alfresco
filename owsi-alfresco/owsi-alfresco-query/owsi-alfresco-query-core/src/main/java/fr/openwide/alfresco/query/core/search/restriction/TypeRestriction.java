package fr.openwide.alfresco.query.core.search.restriction;

import fr.openwide.alfresco.query.core.node.model.TypeModel;

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
	protected String toLuceneQueryInternal() {
		return ((exact) ? "EXACTTYPE:" : "TYPE:") + type.toLucene();
	}

}
