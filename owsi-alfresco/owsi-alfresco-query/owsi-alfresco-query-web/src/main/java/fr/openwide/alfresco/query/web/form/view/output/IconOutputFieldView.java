package fr.openwide.alfresco.query.web.form.view.output;

import org.springframework.context.MessageSourceResolvable;

import fr.openwide.alfresco.query.web.form.util.MessageUtils;

public class IconOutputFieldView {
	
	private String cssClass;
	private MessageSourceResolvable label;

	public IconOutputFieldView(String cssClass, String labelCode, Object ... labelArgs) {
		this.cssClass = cssClass;
		this.label = MessageUtils.code(labelCode, labelArgs);
	}
	
	public String getCssClass() {
		return cssClass;
	}
	public MessageSourceResolvable getLabel() {
		return label;
	}
}
