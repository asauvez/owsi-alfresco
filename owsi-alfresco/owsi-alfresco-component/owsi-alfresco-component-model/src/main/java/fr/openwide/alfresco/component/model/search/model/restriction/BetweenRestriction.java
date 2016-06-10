package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.util.Set;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class BetweenRestriction<C extends Serializable> extends Restriction {

	private final PropertyModel<C> property;
	private final C min;
	private final C max;

	private boolean minInclusive = true;
	private boolean maxInclusive = true;

	public BetweenRestriction(RestrictionBuilder parent, PropertyModel<C> property, C min, C max) {
		super(parent);
		this.property = property;
		this.min = min;
		this.max = max;
	}

	public BetweenRestriction<C> minInclusive(boolean minInclusive) {
		this.minInclusive = minInclusive;
		return this;
	}

	public BetweenRestriction<C> maxInclusive(boolean maxInclusive) {
		this.maxInclusive = maxInclusive;
		return this;
	}

	@Override
	protected String toFtsQueryInternal() {
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
