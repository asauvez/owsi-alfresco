package fr.openwide.alfresco.app.web.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Alert {

	private AlertType type;
	private String message;
	private Object[] args;
	private String details;

	private Alert(AlertType type, String message, String details, Object... args) {
		this.type = type;
		this.message = message;
		this.args = args;
		this.details = details;
	}

	public static Alert newWarning(String message, String details, Object ... args) {
		return new Alert(AlertType.warning, message, details, args);
	}

	public static Alert newError(String message, String details, Object ... args) {
		return new Alert(AlertType.error, message, details, args);
	}

	public static Alert newSuccess(String message, String details, Object ... args) {
		return new Alert(AlertType.success, message, details, args);
	}

	public static Alert newInfo(String message, String details, Object ... args) {
		return new Alert(AlertType.info, message, details, args);
	}

	public AlertType getType() {
		return type;
	}
	public void setType(AlertType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object... args) {
		this.args = args;
	}

	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

}
