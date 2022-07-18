package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;
import java.util.List;

public class NumberRangePropertyConstraint extends PropertyConstraint {

	private final Double min;
	private final Double max;
	
	public NumberRangePropertyConstraint(Double min, Double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean valid(Serializable value) {
		if (value instanceof Number) {
			return acceptDouble(((Number) value).doubleValue());
		} else if (value instanceof List) {
			for (Object o : ((List<?>) value)) {
				if (! acceptDouble(((Number) o).doubleValue())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean acceptDouble(double value) {
		return value >= min && value <= max;
	}
	
	@Override
	public String getMessage() {
		return "[" + min + " ... " + max + "]";
	}
}
