package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class MatchRestriction<C extends Serializable> extends Restriction {

	private final PropertyModel<C> property;
	private boolean exact = true;
	protected C value;

	public MatchRestriction(RestrictionBuilder parent, PropertyModel<C> property, C value) {
		super(parent);
		this.property = property;
		this.value = value;
	}

	@Override
	protected String toQueryInternal() {
		if (value instanceof String && StringUtils.isEmpty((String) value)) {
			value = null;
		}
		String prefix = exact ? "=" : "@";
		return (value != null) ? prefix + property.toLucene() + ":" + toLuceneValue(property, value) : "";
	}
	
	/**
	 * Indique si l'on souhaite utiliser l'index non tokenisé (comparaison exacte).
	 * Vrai par défaut.
	 */
	public MatchRestriction<C> exact(boolean exact) {
		this.exact = exact;
		return this;
	}

}
