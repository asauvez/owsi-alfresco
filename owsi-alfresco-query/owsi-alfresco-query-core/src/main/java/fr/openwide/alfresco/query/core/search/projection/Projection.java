package fr.openwide.alfresco.query.core.search.projection;

import java.io.Serializable;
import java.text.Format;
import java.util.Comparator;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import fr.openwide.alfresco.query.core.search.model.NodeResult;

public abstract class Projection<T> implements Function<NodeResult, Serializable> {

	public enum Align { LEFT, CENTER, RIGHT };

	private final ProjectionBuilder builder;
	private String message;
	private Object[] messageArgs;
	private Function<? super T, String> resultFormatter;
	private Comparator<? super T> comparator = null;
	private Align align = Align.LEFT;
	private boolean visible = true;

	public Projection(ProjectionBuilder builder) {
		this.builder = builder;
		this.resultFormatter = builder.getResultFormatter();
	}

	public ProjectionBuilder of() {
		return builder;
	}

	public Projection<T> as(String message, Object ... messageArgs) {
		this.message = message;
		this.messageArgs = messageArgs;
		return this;
	}

	public Projection<T> visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public Projection<T> format(Function<? super T, String> resultFormatter) {
		this.resultFormatter = resultFormatter;
		return this;
	}

	public Projection<T> prefix(final String prefix) {
		this.resultFormatter = Functions.compose(new Function<String, String>() {
			@Override
			public String apply(String input) {
				return prefix + input;
			}
		}, resultFormatter);
		return this;
	}

	public Projection<T> suffix(final String suffix) {
		this.resultFormatter = Functions.compose(new Function<String, String>() {
			@Override
			public String apply(String input) {
				return input + suffix;
			}
		}, resultFormatter);
		return this;
	}

	public Projection<T> format(final Format format) {
		this.resultFormatter = new Function<T, String>() {
			@Override
			public String apply(T input) {
				return format.format(input);
			}
		};
		return this;
	}

/*	public ButtonBuilder<Projection<T>, T> button(String message, Object ... messageArgs) {
		ButtonBuilder<Projection<T>, T> buttonBuilder = new ButtonBuilder<Projection<T>, T>(this, message, messageArgs);
		this.resultFormatter = buttonBuilder;
		return buttonBuilder;
	}*/

	public Projection<T> align(Align align) {
		this.align = align;
		return this;
	}

	public Projection<T> comparator(Comparator<? super T> comparator) {
		this.comparator = comparator;
		return this;
	}

	public Comparator<? super T> getComparator() {
		return comparator;
	}

	public String getMessage() {
		return (message != null) ? message : getDefaultMessage();
	}

	protected String getDefaultMessage() {
		return getClass().getSimpleName().toLowerCase() + ".label";
	}

	public Object[] getMessageArgs() {
		return messageArgs;
	}

	public boolean isVisible() {
		return visible;
	}

	public Align getAlign() {
		return align;
	}

	public abstract T getValue(NodeResult node);

	@Override
	public String apply(NodeResult node) {
		return resultFormatter.apply(getValue(node));
	}

}
