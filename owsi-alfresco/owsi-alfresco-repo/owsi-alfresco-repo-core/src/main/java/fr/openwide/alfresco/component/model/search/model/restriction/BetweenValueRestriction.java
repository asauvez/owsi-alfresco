package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class BetweenValueRestriction<C extends Serializable> extends BetweenRestriction<C> {

	private final C min;
	private final C max;

	public BetweenValueRestriction(RestrictionBuilder parent, PropertyModel<C> property, C min, C max) {
		super(parent, property);
		this.min = min;
		this.max = max;
	}
	
	@Override
	protected C getMin() {
		return min;
	}
	@Override
	protected C getMax() {
		return max;
	}
	
}
