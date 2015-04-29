package fr.openwide.alfresco.app.web.validation.model;

import java.util.ArrayList;
import java.util.List;

public class AlertContainer {

	public static final String ALERTS_FIELD_NAME = "alerts";

	private List<Alert> items = new ArrayList<>();

	public List<Alert> getItems() {
		return items;
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public AlertContainer addWarning(String message, Object ... args) {
		items.add(Alert.newWarning(message, null, args));
		return this;
	}

	public AlertContainer addError(String message, Object ... args) {
		items.add(Alert.newError(message, null, args));
		return this;
	}

	public AlertContainer addSuccess(String message, Object ... args) {
		items.add(Alert.newSuccess(message, null, args));
		return this;
	}

	public AlertContainer addInfo(String message, Object ... args) {
		items.add(Alert.newInfo(message, null, args));
		return this;
	}

}
