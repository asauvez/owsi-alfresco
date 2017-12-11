package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public class MatchRestriction<C extends Serializable> extends Restriction {

	private final PropertyModel<C> property;
	private boolean exact = false;
	protected C value;

	public MatchRestriction(RestrictionBuilder parent, PropertyModel<C> property, C value) {
		super(parent);
		this.property = property;
		this.value = value;
	}
	
	@Override
	public void testInit(NodeScopeBuilder nodeScopeBuilder) {
		nodeScopeBuilder.properties().set(property);
	}
	@Override
	public boolean test(BusinessNode node) {
		if (exact) {
			if (property instanceof SinglePropertyModel) {
				return Objects.equals(node.properties().get((SinglePropertyModel<C>) property), value);
			} else if (property instanceof MultiPropertyModel) {
				List<C> list = node.properties().get((MultiPropertyModel<C>) property);
				if (list != null) {
					for (C currentVal : list) {
						if (Objects.equals(currentVal, value)) {
							return true;
						}
					}
				}
				return false;
			}
		}
		throw new UnsupportedOperationException();
	}

	@Override
	protected String toFtsQueryInternal() {
		if (value instanceof String && ((String) value).isEmpty()) {
			value = null;
		}
		String prefix = exact ? "=" : "@";
		return (value != null) ? prefix + property.toLucene() + ":" + toFtsLuceneValue(property, value) : "";
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

}
