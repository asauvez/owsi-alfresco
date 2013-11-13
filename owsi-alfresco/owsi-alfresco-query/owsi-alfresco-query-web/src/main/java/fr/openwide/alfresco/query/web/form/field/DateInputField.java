package fr.openwide.alfresco.query.web.form.field;

import java.util.Date;

import fr.openwide.alfresco.query.web.form.view.input.InputFieldView;

public class DateInputField extends InputField<Date> {

	public DateInputField(FieldSet fieldSet, String name) {
		super(fieldSet, name);
	}

	@Override
	public InputFieldView getView() {
		return InputFieldView.DATE;
	}
	
}
