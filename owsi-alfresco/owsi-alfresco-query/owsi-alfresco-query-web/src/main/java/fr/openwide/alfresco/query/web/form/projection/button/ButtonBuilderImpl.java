package fr.openwide.alfresco.query.web.form.projection.button;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.util.MessageUtils;

public class ButtonBuilderImpl<P, T> implements TopButtonBuilder<P, T>, Function<NodeResult, Object> {

	private final Projection<T> projection;
	private final P parent;

	private MessageSourceResolvable label;

	private MessageFormat hrefPattern;

	private boolean primary = false;
	private String cssClass = "";
	private String iconClass = "";

	private Predicate<? super NodeResult> visible = Predicates.alwaysTrue();
	private Predicate<? super NodeResult> enabled = Predicates.alwaysTrue();

	private List<ButtonBuilderImpl<TopButtonBuilder<P, T>, T>> dropDown = new ArrayList<>();

	private NodeResult currentNode;

	public ButtonBuilderImpl(P parent, Projection<T> projection, String labelCode, Object [] labelArgs) {
		this.projection = projection;
		this.parent = parent;
		this.label = MessageUtils.code(labelCode, labelArgs);
	}

	@Override
	public P of() {
		return parent;
	}

	@Override
	public ButtonBuilderImpl<P, T> visible(Predicate<? super NodeResult> visible) {
		this.visible = visible;
		return this;
	}	
	@Override
	public ButtonBuilderImpl<P, T> visible(boolean visible) {
		return visible(visible ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
	}

	@Override
	public ButtonBuilderImpl<P, T> enabled(Predicate<? super NodeResult> enabled) {
		this.enabled = enabled;
		return this;
	}
	@Override
	public ButtonBuilderImpl<P, T> enabled(boolean enabled) {
		return enabled(enabled ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
	}

	@Override
	public ButtonBuilderImpl<P, T> primary() {
		return primary(true);
	}
	@Override
	public ButtonBuilderImpl<P, T> primary(boolean primary) {
		this.primary = primary;
		return this;
	}

	@Override
	public ButtonBuilderImpl<P, T> icon(String iconClass) {
		this.iconClass = Joiner.on(" ").join(this.iconClass, iconClass);
		return this;
	}
	@Override
	public ButtonBuilderImpl<P, T> cssClass(String cssClass) {
		this.cssClass = Joiner.on(" ").join(this.cssClass, cssClass);
		return this;
	}

	@Override
	public ButtonBuilderImpl<P, T> url(String urlPattern) {
		hrefPattern = new MessageFormat(urlPattern);
		return this;
	}

	@Override
	public ButtonBuilderImpl<P, T> dropDownSeparator() {
		dropDown.add(null);
		return this;
	}

	@Override
	public ButtonBuilder<TopButtonBuilder<P, T>, T> dropDownButton(String message, Object ... messageArgs) {
		ButtonBuilderImpl<TopButtonBuilder<P, T>, T> subButton = new ButtonBuilderImpl<TopButtonBuilder<P, T>, T>(this, projection, message, messageArgs);
		dropDown.add(subButton);
		return subButton;
	}

	@Override
	public Object apply(NodeResult currentValue) {
		this.currentNode = currentValue;
		for (ButtonBuilderImpl<TopButtonBuilder<P, T>, T> sub : dropDown) {
			if (sub != null) {
				sub.apply(currentValue);
			}
		}
		return this;
	}
	public Object getCurrentValue() {
		return projection.getResultTransformer().apply(projection.apply(currentNode));
	}
	
	public MessageSourceResolvable getLabel() {
		return label;
	}
	public List<ButtonBuilderImpl<TopButtonBuilder<P, T>, T>> getDropDown() {
		return dropDown;
	}
	public String getHref() {
		return (isEnabled() && hrefPattern != null) ? hrefPattern.format(new Object[] { 
				getCurrentValue() 
			}) : "#";
	}
	public boolean isVisible() {
		return visible.apply(currentNode);
	}
	public boolean isEnabled() {
		return enabled.apply(currentNode);
	}
	public boolean isPrimary() {
		return primary;
	}
	public String getCssClass() {
		return cssClass;
	}
	public String getIconClass() {
		return iconClass;
	}
	public boolean isSubButton() {
		return (parent instanceof ButtonBuilderImpl);
	}
}
