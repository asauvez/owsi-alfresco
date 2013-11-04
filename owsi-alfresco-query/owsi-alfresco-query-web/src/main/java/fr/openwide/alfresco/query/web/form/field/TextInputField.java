package fr.openwide.alfresco.query.web.form.field;

import fr.openwide.alfresco.query.web.form.view.TextInputFieldView;
import fr.openwide.alfresco.query.web.search.model.FormQuery;

public class TextInputField extends InputField<String> implements TextInputFieldView {

	private String placeholder;

	public TextInputField(FormQuery formQuery, String name) {
		super(formQuery, name);
	}

	public TextInputField placeholder(String placeholder) {
		this.placeholder = placeholder;
		return this;
	}

	@Override
	protected String parseNotNull(String value) {
		return (value.length() > 0) ? value : null;
	}

	@Override
	public String getView() {
		return TextInputFieldView.TEXT;
	}

	@Override
	public String getPlaceholder() {
		return placeholder;
	}

}
