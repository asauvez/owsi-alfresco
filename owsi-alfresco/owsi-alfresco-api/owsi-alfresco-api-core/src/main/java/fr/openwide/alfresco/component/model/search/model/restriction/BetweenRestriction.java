package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public abstract class BetweenRestriction<C extends Serializable> extends Restriction {

	protected final PropertyModel<C> property;

	private boolean minInclusive = true;
	private boolean maxInclusive = true;

	public BetweenRestriction(RestrictionBuilder parent, PropertyModel<C> property) {
		super(parent);
		this.property = property;
	}

	public BetweenRestriction<C> minInclusive(boolean minInclusive) {
		this.minInclusive = minInclusive;
		return this;
	}

	public BetweenRestriction<C> maxInclusive(boolean maxInclusive) {
		this.maxInclusive = maxInclusive;
		return this;
	}

	protected abstract C getMin();
	protected abstract C getMax();
	
	@Override
	public void testInit(NodeScopeBuilder nodeScopeBuilder) {
		nodeScopeBuilder.properties().set(property);
	}
	@Override
	public boolean test(BusinessNode node) {
		if (property instanceof SinglePropertyModel) {
			return filter(node.properties().get((SinglePropertyModel<C>) property));
		} else if (property instanceof MultiPropertyModel) {
			List<C> list = node.properties().get((MultiPropertyModel<C>) property);
			if (list != null) {
				for (C currentVal : list) {
					if (filter(currentVal)) {
						return true;
					}
				}
			}
			return false;
		}
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unchecked")
	private boolean filter(C value) {
		C min = getMin();
		C max = getMax();
		if (value == null) return false;
		if (min != null) {
			int d = ((Comparable<C>) min).compareTo(value);
			if ((minInclusive) ? d >= 0 : d > 0) {
				return false;
			}
		}
		if (max != null) {
			int d = ((Comparable<C>) max).compareTo(value);
			if ((maxInclusive) ? d <= 0 : d < 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected String toFtsQueryInternal() {
		C min = getMin();
		C max = getMax();
		if (min == null && max == null) {
			return "";
		}
		return property.toLucene() + ":"
			+ (minInclusive ? "[" : "<")
			+ ((min == null)
					? ((max instanceof String) ? "\\\\u0000" : "MIN")
					: toFtsLuceneValue(property, min))
			+ " TO "
			+ ((max == null)
					? ((min instanceof String) ? "\\\\uFFFF" : "MAX")
					: toFtsLuceneValue(property, max))
			+ (maxInclusive ? "]" : ">");
	}
	
	@Override
	protected void addCmisQueryJoin(Set<ContainerModel> containersToJoin) {
		containersToJoin.add(property.getType());
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		C min = getMin();
		C max = getMax();
		if (min == null && max == null) {
			return "";
		}
		
		StringBuilder result = new StringBuilder(toCmisProperty(property));
		
		if (min != null){
			result .append(" >")
				.append((minInclusive) ? "= " : " ")
				.append(toCmisLuceneValue(property, min));
			if (max !=null){
				result.append("\nAND ")
					.append(toCmisProperty(property));
			}
		}
		if (max != null){
			result .append(" <")
				.append((maxInclusive) ? "= " : " ")
				.append(toCmisLuceneValue(property, max));
		}
		
		return result.toString();
	}

}
