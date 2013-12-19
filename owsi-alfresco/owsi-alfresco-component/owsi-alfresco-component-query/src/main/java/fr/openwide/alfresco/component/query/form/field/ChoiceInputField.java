package fr.openwide.alfresco.component.query.form.field;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.query.form.view.input.ChoiceItem;
import fr.openwide.alfresco.component.query.form.view.input.InputFieldView;

public class ChoiceInputField extends TextInputField {

	private List<ChoiceItem> items = new ArrayList<>();
	private int selectTrigger = 5;

	public ChoiceInputField(FieldSet fieldSet, String name) {
		super(fieldSet, name);
	}

	public ChoiceInputField addChoice(ChoiceItem item) {
		items.add(item);
		return this;
	}

	public ChoiceInputField addChoice(String key, String messageCode, Object ... messageArgs) {
		return addChoice(new ChoiceItem(key, messageCode, messageArgs));
	}

	public ChoiceInputField addChoiceAllItems() {
		return addChoice(null, "choiceField.all");
	}

	public ChoiceInputField selectTrigger(int selectTrigger) {
		this.selectTrigger = selectTrigger;
		return this;
	}

	public ChoiceInputField asRadioBox() {
		return selectTrigger(Integer.MAX_VALUE);
	}

	public ChoiceInputField asComboBox() {
		return selectTrigger(0);
	}

	public List<ChoiceItem> getItems() {
		return items;
	}

	@Override
	public InputFieldView getView() {
		return (getItems().size() <= selectTrigger) ? InputFieldView.RADIO : InputFieldView.SELECT;
	}

}
