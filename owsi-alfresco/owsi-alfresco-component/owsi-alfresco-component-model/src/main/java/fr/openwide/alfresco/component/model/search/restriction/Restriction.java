package fr.openwide.alfresco.component.model.search.restriction;

import java.util.Date;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

public abstract class Restriction {

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

	public final String toLuceneQuery() {
		return (not) ? "NOT (" + toLuceneQueryInternal() + ")" : toLuceneQueryInternal();
	}

	protected abstract String toLuceneQueryInternal();

	protected static String toLuceneValue(Object value) {
		if (value instanceof Date) {
			return ISO8601Utils.format((Date) value);
		} else if (value instanceof Number) {
			return value.toString();
		} else {
			return "\"" + value.toString().replace("\"", "\\\"") + "\"";
		}
	}

}
