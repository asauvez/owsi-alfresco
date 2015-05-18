package fr.openwide.alfresco.component.model.search.restriction;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;

public abstract class Restriction {

	private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");

	private final RestrictionBuilder parent;
	private boolean not = false;

	public Restriction(RestrictionBuilder parent) {
		this.parent = parent;
	}

	public Restriction not() {
		not = true;
		return this;
	}

	public RestrictionBuilder of() {
		return parent;
	}

	public final String toQuery() {
		String query = toQueryInternal();
		return (query.length() > 0) ? ((not) ? "NOT (" + query + ")" : query) : "";
	}

	protected abstract String toQueryInternal();

	protected static String toLuceneValue(PropertyModel<?> propertyModel, Object value) {
		if (value instanceof Date) {
			if (propertyModel instanceof DateTimePropertyModel) {
				return dateFormat.format((Date) value);
			}
			return ISO8601Utils.format((Date) value).replace(":", "\\:");
		} else if (value instanceof Number) {
			return value.toString();
		} else {
			return "\"" + value.toString().replace("\"", "\\\"") + "\"";
		}
	}

}
