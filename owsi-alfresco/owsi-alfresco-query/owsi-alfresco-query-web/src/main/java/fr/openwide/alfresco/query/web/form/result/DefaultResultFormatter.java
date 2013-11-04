package fr.openwide.alfresco.query.web.form.result;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

import com.google.common.base.Function;

import fr.openwide.alfresco.query.web.search.context.QueryContext;

public class DefaultResultFormatter implements Function<Object, String> {

	private static final String ITERABLE_SEPARATOR = ", ";
	private final QueryContext queryContext;
	private final DateFormat dateFormat;
	private final NumberFormat numberFormat;
	
	public DefaultResultFormatter(QueryContext queryContext) {
		this.queryContext = queryContext;
		this.dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, queryContext.getLocale());
		this.numberFormat = NumberFormat.getNumberInstance(queryContext.getLocale());
	}
	
	@Override
	public String apply(Object input) {
		if (input == null) {
			return "";
		} else if (input instanceof Date) {
			return dateFormat.format(input);
		} else if (input instanceof Number) {
			return numberFormat.format(input);
		} else if (input instanceof Boolean) {
			return queryContext.getMessage(((Boolean) input) ? "defaultFormatter.true" : "defaultFormatter.false");
		} else if (input instanceof Iterable<?>) {
			StringBuilder buf = new StringBuilder();
			for (Object item : ((Iterable<?>) input)) {
				buf.append(apply(item)).append(ITERABLE_SEPARATOR);
			}
			if (buf.length() != 0) {
				buf.setLength(buf.length() - ITERABLE_SEPARATOR.length());
			}
			return buf.toString();
		} else if (input instanceof Map.Entry) {
			return apply(((Map.Entry<?, ?>) input).getKey()) + " = " + apply(((Map.Entry<?, ?>) input).getValue());
		}
		return input.toString();
	}
	
}
