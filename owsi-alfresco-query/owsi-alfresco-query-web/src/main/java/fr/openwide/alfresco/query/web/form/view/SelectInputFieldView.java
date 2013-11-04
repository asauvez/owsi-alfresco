package fr.openwide.alfresco.query.web.form.view;

import java.util.List;

public interface SelectInputFieldView extends InputFieldView {

	String SELECT = "select";
	String RADIO = "radio";

	List<ChoiceItem> getItems();

	public static class ChoiceItem {
		
		private final String key;
		private final String message;
		private final Object[] messageArgs;
		
		public ChoiceItem(String key, String message, Object ... messageArgs) {
			this.key = key;
			this.message = message;
			this.messageArgs = messageArgs;
		}
		
		public String getKey() {
			return key;
		}
		public String getMessage() {
			return message;
		}
		public Object[] getMessageArgs() {
			return messageArgs;
		}
	}

}
