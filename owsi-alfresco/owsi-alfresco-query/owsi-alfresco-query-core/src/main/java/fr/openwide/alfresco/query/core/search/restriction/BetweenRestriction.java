package fr.openwide.alfresco.query.core.search.restriction;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;

public class BetweenRestriction<C> extends Restriction {

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
	protected String toLuceneQueryInternal() {
		if (min == null && max == null) {
			return "";
		}
		return property.toLucene() + ":"
			+ (minInclusive ? "[" : "{")
			+ ((min == null) 
					? ((max instanceof String) ? "\\\\u0000" : "MIN") 
					: toLuceneValue(min))
			+ " TO "
			+ ((max == null) 
					? ((min instanceof String) ? "\\\\uFFFF" : "MAX") 
					: toLuceneValue(max))
			+ (maxInclusive ? "]" : "}");
	}

}
