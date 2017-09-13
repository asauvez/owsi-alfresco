package fr.openwide.alfresco.component.model.search.model.restriction;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import fr.openwide.alfresco.component.model.node.model.property.single.AbstractDatePropertyModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder.LogicalOperator;

public class BetweenRelativeTimeRestriction extends BetweenRestriction<Date> {

	private final Integer min;
	private final Integer max;
	private final TemporalUnit unit;
	
	public BetweenRelativeTimeRestriction(RestrictionBuilder parent, AbstractDatePropertyModel property, 
			Integer min, Integer max, TemporalUnit unit) {
		super(parent, property);
		this.min = min;
		this.max = max;
		this.unit = unit;
	}
	
	@Override
	protected Date getMin() {
		return (min != null) ? Date.from(Instant.now().plus(min, unit)) : null;
	}
	@Override
	protected Date getMax() {
		return (max != null) ? Date.from(Instant.now().plus(max, unit)) : null;
	}
	
	@Override
	public Restriction toTimeRelativeRestriction(Integer duration, TemporalUnit unit) {
		RestrictionBuilder or = new RestrictionBuilder(null, LogicalOperator.OR);
		or.add(getRestriction(getMin(), duration, unit));
		or.add(getRestriction(getMax(), duration, unit));
		return or;
	}
	
	private Restriction getRestriction(Date date, Integer duration, TemporalUnit unit) {
		return (date != null) 
			? new BetweenValueRestriction<Date>(of(), property, date, Date.from(date.toInstant().plus(duration, unit))) 
			: null;
	}
}
