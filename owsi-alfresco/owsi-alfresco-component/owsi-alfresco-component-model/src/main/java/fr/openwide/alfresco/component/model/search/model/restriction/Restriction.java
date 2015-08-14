package fr.openwide.alfresco.component.model.search.model.restriction;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NameReferencePropertyModel;

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
	
	@Override
	public String toString() {
		return toQuery();
	}

	public final String toQuery() {
		String query = toQueryInternal();
		if (query.isEmpty()) return query;
		return (not) 
				? "NOT " + (isNeedingParenthesis() ? "(" + query + ")" : query)  
				: query;
	}

	protected boolean isNeedingParenthesis() {
		return false;
	}
	
	protected abstract String toQueryInternal();

	protected static String toLuceneValue(PropertyModel<?> propertyModel, Object value) {
		if (propertyModel instanceof DateTimePropertyModel) {
			return ISO8601Utils.format((Date) value, false, TimeZone.getDefault()).replace(":", "\\:");
		} else if (propertyModel instanceof DatePropertyModel) {
			return dateFormat.format((Date) value);
		} else if (value instanceof Number) {
			return value.toString();
		} else if (propertyModel instanceof NameReferencePropertyModel && value instanceof NameReference) {
			return "\"" + ((NameReference) value).getFullyQualified().replace("\"", "\\\"") + "\"";
		} else {
			return "\"" + value.toString().replace("\"", "\\\"") + "\"";
		}
	}

}
