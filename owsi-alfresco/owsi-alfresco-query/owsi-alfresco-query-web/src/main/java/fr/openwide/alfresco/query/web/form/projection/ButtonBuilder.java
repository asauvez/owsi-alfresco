package fr.openwide.alfresco.query.web.form.projection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import fr.openwide.alfresco.query.web.search.context.QueryContext;
import fr.openwide.alfresco.query.web.search.context.QueryContextHolder;

public class ButtonBuilder<P extends QueryContextHolder, T> implements Function<T, String>, QueryContextHolder {

	private final P parent;

	private String message;
	private Object[] messageArgs;

	private MessageFormat hrefPattern;

	private String cssClass;
	private String iconClass;

	private Predicate<? super T> visible = Predicates.alwaysTrue();
	private Predicate<? super T> enabled = Predicates.alwaysTrue();

	private List<ButtonBuilder<ButtonBuilder<P, T>, T>> dropDown = new ArrayList<ButtonBuilder<ButtonBuilder<P, T>, T>>();

	public ButtonBuilder(P parent, String message, Object [] messageArgs) {
		this.parent = parent;
		this.message = message;
		this.messageArgs = messageArgs;
	}

	public P of() {
		return parent;
	}

	@Override
	public QueryContext getQueryContext() {
		return parent.getQueryContext();
	}

	public ButtonBuilder<P, T> visible(Predicate<? super T> visible) {
		this.visible = visible;
		return this;
	}	
	public ButtonBuilder<P, T> visible(boolean visible) {
		return visible(visible ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
	}

	public ButtonBuilder<P, T> enabled(Predicate<? super T> enabled) {
		this.enabled = enabled;
		return this;
	}
	public ButtonBuilder<P, T> enabled(boolean enabled) {
		return enabled(enabled ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
	}

	public ButtonBuilder<P, T> primary() {
		return cssClass("btn-primary");
	}

	public ButtonBuilder<P, T> icon(String iconClass) {
		this.iconClass = Joiner.on(" ").join(this.iconClass, iconClass);
		return this;
	}
	public ButtonBuilder<P, T> iconWhite(String iconClass) {
		this.iconClass = Joiner.on(" ").join(this.iconClass, "icon-white", iconClass);
		return this;
	}
	public ButtonBuilder<P, T> cssClass(String cssClass) {
		this.cssClass = Joiner.on(" ").join(this.cssClass, cssClass);
		return this;
	}

	public ButtonBuilder<P, T> url(String urlPattern) {
		hrefPattern = new MessageFormat(urlPattern);
		return this;
	}

	public ButtonBuilder<P, T> dropDownSeparator() {
		dropDown.add(null);
		return this;
	}

	public ButtonBuilder<ButtonBuilder<P, T>, T> dropDownButton(String message, Object ... messageArgs) {
		ButtonBuilder<ButtonBuilder<P, T>, T> subButton = new ButtonBuilder<ButtonBuilder<P, T>, T>(this, message, messageArgs);
		dropDown.add(subButton);
		return subButton;
	}

	@Override
	public String apply(T input) {
		if (input == null || ! visible.apply(input)) {
			return "";
		}
		
		List<ButtonBuilder<ButtonBuilder<P, T>, T>> dropDownFiltered = new ArrayList<ButtonBuilder<ButtonBuilder<P,T>,T>>();
		for (ButtonBuilder<ButtonBuilder<P, T>, T> sub : dropDown) {
			if (sub == null || sub.visible.apply(input)) {
				dropDownFiltered.add(sub);
			}
		}
		
		if (dropDownFiltered.isEmpty() && ! dropDown.isEmpty()) {
			return "";
		}
		
		boolean enabled = this.enabled.apply(input);
		
		StringBuilder buf = new StringBuilder();
		if (! dropDownFiltered.isEmpty()) {
			buf.append("<div class=\"btn-group\">");
		}
		buf.append("<a href=\"")
			.append((enabled && hrefPattern != null) ? hrefPattern.format(new Object[] { input }) : "#")
			.append("\" data-ref=\"").append(input).append("\"");
		if (! dropDownFiltered.isEmpty()) {
			buf.append(" data-toggle=\"dropdown\"");
		}
		buf.append(" class=\"")
			.append((parent instanceof ButtonBuilder) ? "" : "btn ")
			.append(enabled ? "" : "disabled ")
			.append(dropDownFiltered.isEmpty() ? "" : "dropdown-toggle ")
			.append(cssClass.trim())
			.append("\">");
		if (iconClass != null) {
			buf.append("<i class=\"icon-user " + iconClass + "" + "\"></i>&#160;");
		}
		buf.append(parent.getQueryContext().getMessage(message, messageArgs));
		if (! dropDownFiltered.isEmpty()) {
			buf.append("&#160;<span class=\"caret\"></span>");
		}
		buf.append("</a>");
		
		if (! dropDownFiltered.isEmpty()) {
			buf.append("<ul class=\"dropdown-menu\">");
			for (ButtonBuilder<ButtonBuilder<P, T>, T> sub : dropDownFiltered) {
				if (sub != null) {
					buf.append("<li>");
					buf.append(sub.apply(input));
					buf.append("</li>");
				} else {
					buf.append("<li class=\"divider\"></li>");
				}
			}
			buf.append("</ul></div>");
		}
		return buf.toString();
	}

}
