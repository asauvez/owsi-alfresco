package fr.openwide.alfresco.component.model.search.restriction;

import java.io.Serializable;

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
	protected String toQueryInternal() {
		if (min == null && max == null) {
			return "";
		}
		return property.toLucene() + ":"
			+ (minInclusive ? "[" : "{")
			+ ((min == null)
					? ((max instanceof String) ? "\\\\u0000" : "MIN")
					: toLuceneValue(property, min))
			+ " TO "
			+ ((max == null)
					? ((min instanceof String) ? "\\\\uFFFF" : "MAX")
					: toLuceneValue(property, max))
			+ (maxInclusive ? "]" : "}");
	}

}
