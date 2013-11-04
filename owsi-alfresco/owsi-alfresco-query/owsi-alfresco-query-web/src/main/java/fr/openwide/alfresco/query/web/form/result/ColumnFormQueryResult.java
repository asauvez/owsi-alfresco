package fr.openwide.alfresco.query.web.form.result;

import java.util.Comparator;

import com.google.common.base.Function;

public class ColumnFormQueryResult<T> {

	private final String message;
	private final Object[] messageArgs;
	private String align = "left";

	private Function<T, String> resultFormatter;
	private Comparator<T> comparator = null;

	public ColumnFormQueryResult(String message, Object ... messageArgs) {
		this.message = message;
		this.messageArgs = messageArgs;
	}

	public String getMessage() {
		return message;
	}
	public Object[] getMessageArgs() {
		return messageArgs;
	}

	public Function<T, String> getResultFormatter() {
		return resultFormatter;
	}
	public ColumnFormQueryResult<T> resultFormatter(Function<T, String> resultFormatter) {
		this.resultFormatter = resultFormatter;
		return this;
	}

	public Comparator<T> getComparator() {
		return comparator;
	}
	public ColumnFormQueryResult<T> comparator(Comparator<T> comparator) {
		this.comparator = comparator;
		return this;
	}

	public String getAlign() {
		return align;
	}
	public ColumnFormQueryResult<T> align(String align) {
		this.align = align;
		return this;
	}

}
