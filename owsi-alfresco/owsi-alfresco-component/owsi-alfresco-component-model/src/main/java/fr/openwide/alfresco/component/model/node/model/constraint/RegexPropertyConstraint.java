package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

public class RegexPropertyConstraint extends PropertyConstraint {

	private Pattern pattern;
	private boolean requiresMatch;

	public RegexPropertyConstraint(Pattern pattern, boolean requiresMatch) {
		this.pattern = pattern;
		this.requiresMatch = requiresMatch;
	}

	public RegexPropertyConstraint(String regex, boolean requiresMatch) {
		this(Pattern.compile(regex), requiresMatch);
	}
	public RegexPropertyConstraint(String regex) {
		this(Pattern.compile(regex), true);
	}

	@Override
	public boolean valid(Serializable value) {
		if (value instanceof String) {
			return acceptString((String) value);
		} else if (value instanceof List) {
			for (Object o : ((List<?>) value)) {
				if (! acceptString((String) o)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean acceptString(String value) {
		return requiresMatch ?   pattern.matcher(value).matches() 
				             : ! pattern.matcher(value).matches();
	}
	
	@Override
	public String getMessage() {
		return (requiresMatch ? "" : "Not ") + pattern.pattern();
	}
}
