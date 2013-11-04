package fr.openwide.alfresco.query.web.form.field;

import fr.openwide.alfresco.query.core.node.model.property.BooleanPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.web.search.model.FormQuery;

public final class Fields {

	public static TextInputField newText(FormQuery formQuery, String name) {
		return new TextInputField(formQuery, name);
	}

	public static DateInputField newDate(FormQuery formQuery, String name) {
		return new DateInputField(formQuery, name);
	}

	public static CheckboxInputField newCheckBox(FormQuery formQuery, String name) {
		return new CheckboxInputField(formQuery, name);
	}

	public static ChoiceInputField newChoice(FormQuery formQuery, String name) {
		return new ChoiceInputField(formQuery, name);
	}

	private static String toName(NameReference nameReference) {
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}

	public static TextInputField newText(FormQuery formQuery, TextPropertyModel property) {
		return newText(formQuery, toName(property.getNameReference()));
	}

	public static ChoiceInputField newChoice(FormQuery formQuery, BooleanPropertyModel property) {
		ChoiceInputField choice = newChoice(formQuery, toName(property.getNameReference()));
		choice.addChoiceAllItems();
		choice.addChoice("true", "booleanField.true");
		choice.addChoice("false", "booleanField.false");
		return choice;
	}

	public static FieldSet newFieldSet(InputField<?> ... inputFields) {
		return new FieldSet(inputFields);
	}

	private Fields() {}
}
