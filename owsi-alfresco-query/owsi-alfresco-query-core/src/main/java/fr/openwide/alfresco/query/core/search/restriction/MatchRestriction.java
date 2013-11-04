package fr.openwide.alfresco.query.core.search.restriction;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;

public class MatchRestriction<C> extends Restriction {

	private final PropertyModel<C> property;
	private final C value;

	public MatchRestriction(RestrictionBuilder parent, PropertyModel<C> property, C value) {
		super(parent);
		this.property = property;
		this.value = value;
	}

	@Override
	protected String toLuceneQueryInternal() {
		return (value != null) ? property.toLucene() + ":" + toLuceneValue(value) : "";
	}

}
