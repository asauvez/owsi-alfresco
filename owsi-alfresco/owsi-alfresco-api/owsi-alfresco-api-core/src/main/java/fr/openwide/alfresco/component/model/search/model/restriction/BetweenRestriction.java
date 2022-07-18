package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.util.Set;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

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
