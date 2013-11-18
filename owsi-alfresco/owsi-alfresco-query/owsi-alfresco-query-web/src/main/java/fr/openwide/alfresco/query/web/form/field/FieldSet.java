package fr.openwide.alfresco.query.web.form.field;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.property.BooleanPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.query.web.form.util.MessageUtils;

public class FieldSet {

	private InputFieldBuilder builder;
	private List<InputField<?>> inputFields = new ArrayList<>();

	private MessageSourceResolvable label;
	private boolean visible = true;
	private boolean inRow = false;

	public FieldSet(InputFieldBuilder builder) {
		this.builder = builder;
	}

	public InputFieldBuilder of() {
		return builder;
	}

	public FieldSet label(String labelCode, Object ... labelArgs) {
		this.label = MessageUtils.code(labelCode, labelArgs);
		return this;
	}

	public FieldSet visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public FieldSet inRow(boolean inRow) {
		this.inRow = inRow;
		return this;
	}


	public TextInputField newText(String name) {
		return add(new TextInputField(this, name));
	}

	public DateInputField newDate(String name) {
		return add(new DateInputField(this, name));
	}

	public ChoiceInputField newChoice(String name) {
		return add(new ChoiceInputField(this, name));
	}

	private String toName(PropertyModel<?> property) {
		NameReference nameReference = property.getNameReference();
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}

	public TextInputField newText(TextPropertyModel property) {
		return newText(toName(property));
	}

	public ChoiceInputField newChoice(BooleanPropertyModel property) {
		ChoiceInputField choice = newChoice(toName(property));
		choice.addChoiceAllItems();
		choice.addChoice("true", "booleanField.true");
		choice.addChoice("false", "booleanField.false");
		return choice;
	}


	public MessageSourceResolvable getLabel() {
		return label;
	}

	public boolean isVisible() {
		if (visible) {
			for (InputField<?> inputField : inputFields) {
				if (inputField.isVisible()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isInRow() {
		return inRow;
	}

	public List<InputField<?>> getInputFields() {
		return inputFields;
	}

	private <T extends InputField<?>> T add(T inputField) {
		inputFields.add(inputField);
		return inputField;
	}
}
