package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class MatchRestriction<C extends Serializable> extends Restriction {

	private final PropertyModel<C> property;
	private boolean exact = false;
	private Double fuzzy = null;
	protected C value;

	public MatchRestriction(RestrictionBuilder parent, PropertyModel<C> property, C value) {
		super(parent);
		this.property = property;
		this.value = value;
	}
	
	@Override
	protected String toFtsQueryInternal() {
		if (value == null || (value instanceof String && ((String) value).isEmpty())) {
			return "";
		}
		String prefix = (exact && ! (value instanceof Date)) ? "=" : "@";
		String fuzzy = (this.fuzzy != null) ? "~" + this.fuzzy : "";
		return prefix + property.toLucene() + ":" + toFtsLuceneValue(property, value) + fuzzy;
	}
	
	@Override
	protected void addCmisQueryJoin(Set<ContainerModel> containersToJoin) {
		containersToJoin.add(property.getType());
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		return (value != null) ? toCmisProperty(property) + "=" + toCmisLuceneValue(property, value) : "";
	}
	
	/**
	 * Indique si l'on souhaite utiliser l'index non tokenisé (comparaison exacte).
	 * Vrai par défaut.
	 */
	public MatchRestriction<C> exact(boolean exact) {
		this.exact = exact;
		return this;
	}

	/** https://docs.alfresco.com/6.0/concepts/rm-searchsyntax-fuzzy.html */
	public MatchRestriction<C> fuzzy(Double fuzzy) {
		if (fuzzy != null && (fuzzy < 0.0 || fuzzy > 1.0)) {
			throw new IllegalStateException("" + fuzzy);
		}
		this.fuzzy = fuzzy;
		return this;
	}
}
