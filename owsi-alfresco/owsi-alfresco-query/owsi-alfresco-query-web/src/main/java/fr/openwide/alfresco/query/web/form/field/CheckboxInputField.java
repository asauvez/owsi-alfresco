package fr.openwide.alfresco.query.web.form.field;

import fr.openwide.alfresco.query.web.search.model.FormQuery;

public class CheckboxInputField extends InputField<Boolean> {

	public CheckboxInputField(FormQuery formQuery, String name) {
		super(formQuery, name);
	}

	@Override
	public Boolean parseNotNull(String value) {
		return Boolean.valueOf(value);
	}

	@Override
	public String getView() {
		return "checkbox";
	}
}
