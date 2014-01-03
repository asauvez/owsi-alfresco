package fr.openwide.alfresco.component.model.search.restriction;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class MatchRestriction<C extends Serializable> extends Restriction {

	private final PropertyModel<C> property;
	protected C value;

	public MatchRestriction(RestrictionBuilder parent, PropertyModel<C> property, C value) {
		super(parent);
		this.property = property;
		this.value = value;
	}

	@Override
	protected String toLuceneQueryInternal() {
		if (value instanceof String && StringUtils.isEmpty((String) value)) {
			value = null;
		}
		return (value != null) ? property.toLucene() + ":" + toLuceneValue(value) : "";
	}

}
