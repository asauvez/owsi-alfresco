package fr.openwide.alfresco.query.web.form.field;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.query.web.form.view.SelectInputFieldView;
import fr.openwide.alfresco.query.web.search.model.FormQuery;

public class ChoiceInputField extends TextInputField implements SelectInputFieldView {

	private List<ChoiceItem> items = new ArrayList<ChoiceItem>();
	private int selectTrigger = 5;

	public ChoiceInputField(FormQuery formQuery, String name) {
		super(formQuery, name);
	}

	public ChoiceInputField addChoice(ChoiceItem item) {
		items.add(item);
		return this;
	}

	public ChoiceInputField addChoice(String key, String message, Object ... messageArgs) {
		return addChoice(new ChoiceItem(key, message, messageArgs));
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

	@Override
	public List<ChoiceItem> getItems() {
		return items;
	}

	@Override
	public String getView() {
		return (getItems().size() <= selectTrigger) ? SelectInputFieldView.RADIO : SelectInputFieldView.SELECT;
	}

}
