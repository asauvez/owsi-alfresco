package fr.openwide.alfresco.component.query.form.projection.button;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import fr.openwide.alfresco.component.query.form.projection.ProjectionVisitor;
import fr.openwide.alfresco.component.query.form.projection.ProjectionVisitorAcceptor;
import fr.openwide.alfresco.component.query.form.util.MessageUtils;

public class ButtonBuilderImpl<PARENT, I>
	implements 
		TopButtonBuilder<PARENT, I>,
		Function<I, Object>,
		ProjectionVisitorAcceptor {

	private final PARENT parent;
	private final Function<I, Object> transformer;

	private MessageSourceResolvable label;

	private MessageFormat hrefPattern;

	private boolean primary = false;
	private String cssClass = "";
	private String iconClass = "";

	private Predicate<? super I> visible = Predicates.alwaysTrue();
	private Predicate<? super I> enabled = Predicates.alwaysTrue();

	private List<ButtonBuilderImpl<TopButtonBuilder<PARENT, I>, I>> dropDown = new ArrayList<>();

	private I currentItem;

	public ButtonBuilderImpl(PARENT parent, Function<I, Object> transformer, String labelCode, Object [] labelArgs) {
		this.parent = parent;
		this.transformer = transformer;
		this.label = MessageUtils.code(labelCode, labelArgs);
	}

	@Override
	public PARENT of() {
		return parent;
	}

	@Override
	public ButtonBuilderImpl<PARENT, I> visible(Predicate<? super I> visible) {
		this.visible = visible;
		return this;
	}
	@Override
	public ButtonBuilderImpl<PARENT, I> visible(boolean visible) {
		return visible(visible ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
	}

	@Override
	public ButtonBuilderImpl<PARENT, I> enabled(Predicate<? super I> enabled) {
		this.enabled = enabled;
		return this;
	}
	@Override
	public ButtonBuilderImpl<PARENT, I> enabled(boolean enabled) {
		return enabled(enabled ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
	}

	@Override
	public ButtonBuilderImpl<PARENT, I> primary() {
		return primary(true);
	}
	@Override
	public ButtonBuilderImpl<PARENT, I> primary(boolean primary) {
		this.primary = primary;
		return this;
	}

	@Override
	public ButtonBuilderImpl<PARENT, I> icon(String iconClass) {
		this.iconClass = Joiner.on(" ").join(this.iconClass, iconClass);
		return this;
	}
	@Override
	public ButtonBuilderImpl<PARENT, I> cssClass(String cssClass) {
		this.cssClass = Joiner.on(" ").join(this.cssClass, cssClass);
		return this;
	}

	@Override
	public ButtonBuilderImpl<PARENT, I> url(String urlPattern) {
		hrefPattern = new MessageFormat(urlPattern);
		return this;
	}

	@Override
	public ButtonBuilderImpl<PARENT, I> dropDownSeparator() {
		dropDown.add(null);
		return this;
	}

	@Override
	public ButtonBuilder<TopButtonBuilder<PARENT, I>, I> dropDownButton(String message, Object ... messageArgs) {
		ButtonBuilderImpl<TopButtonBuilder<PARENT, I>, I> subButton = new ButtonBuilderImpl<TopButtonBuilder<PARENT, I>, I>(this, transformer, message, messageArgs);
		dropDown.add(subButton);
		return subButton;
	}

	@Override
	public Object apply(I currentItem) {
		this.currentItem = currentItem;
		for (ButtonBuilderImpl<TopButtonBuilder<PARENT, I>, I> sub : dropDown) {
			if (sub != null) {
				sub.apply(currentItem);
			}
		}
		return this;
	}
	public Object getCurrentValue() {
		return transformer.apply(currentItem);
	}

	public MessageSourceResolvable getLabel() {
		return label;
	}
	public List<ButtonBuilderImpl<TopButtonBuilder<PARENT, I>, I>> getDropDown() {
		return dropDown;
	}
	public String getHref() {
		return (isEnabled() && hrefPattern != null) ? hrefPattern.format(new Object[] {
				getCurrentValue()
			}) : "#";
	}
	public boolean isVisible() {
		return visible.apply(currentItem);
	}
	public boolean isEnabled() {
		return enabled.apply(currentItem);
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
	
	@Override
	public void accept(ProjectionVisitor visitor) {
		visitor.visit(transformer);
		visitor.visit(visible);
		visitor.visit(enabled);
		for (ButtonBuilderImpl<?, I> subButton : getDropDown()) {
			visitor.visit(subButton);
		}
	}
}
