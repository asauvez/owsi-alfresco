package fr.openwide.alfresco.query.web.form.field;

import org.springframework.context.MessageSourceResolvable;

import fr.openwide.alfresco.query.web.form.util.MessageUtils;
import fr.openwide.alfresco.query.web.form.view.input.InputFieldView;

public abstract class InputField<T> {

	private FieldSet fieldSet;
	private final String name;

	private MessageSourceResolvable label;
	private MessageSourceResolvable description;
	private boolean visible = true;
	private int rowSpan = 4;
	private int rowOffset = 0;

	public InputField(FieldSet fieldSet, String name) {
		this.fieldSet = fieldSet;
		this.name = name;
	}

	public FieldSet of() {
		return fieldSet;
	}

	public InputField<T> label(String labelCode, Object ... labelArgs) {
		this.label = MessageUtils.code(labelCode, labelArgs);
		return this;
	}
	public InputField<T> description(String descriptionCode, Object ... descriptionArgs) {
		this.description = MessageUtils.code(descriptionCode, descriptionArgs);
		return this;
	}

	public InputField<T> visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public InputField<T> rowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
		return this;
	}
	public InputField<T> rowOffset(int rowOffset) {
		this.rowOffset = rowOffset;
		return this;
	}

	public String getName() {
		return name;
	}

	public abstract InputFieldView getView();

	public MessageSourceResolvable getLabel() {
		return label;
	}
	public void setLabel(MessageSourceResolvable label) {
		this.label = label;
	}
	public MessageSourceResolvable getDescription() {
		return description;
	}
	public void setDescription(MessageSourceResolvable description) {
		this.description = description;
	}

	public boolean isVisible() {
		return visible;
	}
	public String getRowSpan() {
		return "col-md-" + rowSpan;
	}
	public String getRowOffset() {
		return (rowOffset != 0) ? " col-md-offset-" + rowOffset : "";
	}

}
