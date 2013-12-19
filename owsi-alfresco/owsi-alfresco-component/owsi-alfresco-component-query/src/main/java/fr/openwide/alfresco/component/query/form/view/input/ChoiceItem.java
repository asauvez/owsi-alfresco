package fr.openwide.alfresco.component.query.form.view.input;

import org.springframework.context.MessageSourceResolvable;

import fr.openwide.alfresco.component.query.form.util.MessageUtils;

public class ChoiceItem {

	private final String key;
	private final MessageSourceResolvable label;

	public ChoiceItem(String key, String labelCode, Object ... labelArgs) {
		this.key = key;
		this.label = MessageUtils.code(labelCode, labelArgs);
	}

	public String getKey() {
		return key;
	}
	public MessageSourceResolvable getLabel() {
		return label;
	}
}
