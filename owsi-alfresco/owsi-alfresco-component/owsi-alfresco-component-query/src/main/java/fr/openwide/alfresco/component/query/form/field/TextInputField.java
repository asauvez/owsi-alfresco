package fr.openwide.alfresco.component.query.form.field;

import org.springframework.context.MessageSourceResolvable;

import fr.openwide.alfresco.component.query.form.util.MessageUtils;
import fr.openwide.alfresco.component.query.form.view.input.InputFieldView;

public class TextInputField extends InputField<String> {

	private MessageSourceResolvable placeholder;

	public TextInputField(FieldSet fieldSet, String name) {
		super(fieldSet, name);
	}

	public TextInputField placeholder(String placeholderCode, Object ... placeholderArgs) {
		this.placeholder = MessageUtils.code(placeholderCode, placeholderArgs);
		return this;
	}

	@Override
	public InputFieldView getView() {
		return InputFieldView.TEXT;
	}

	public MessageSourceResolvable getPlaceholder() {
		return placeholder;
	}

}
