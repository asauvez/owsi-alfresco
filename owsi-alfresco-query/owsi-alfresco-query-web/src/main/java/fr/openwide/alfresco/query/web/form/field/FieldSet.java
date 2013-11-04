package fr.openwide.alfresco.query.web.form.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldSet {

	private String message;
	private Object[] messageArgs;
	private List<InputField<?>> inputFields;
	private boolean visible = true;
	private boolean inRow = false;

	public FieldSet(InputField<?> ... inputFields) {
		this.inputFields = new ArrayList<InputField<?>>(Arrays.asList(inputFields));
	}
	
	public FieldSet message(String message, String ... messageArgs) {
		this.message = message;
		this.messageArgs = messageArgs;
		return this;
	}

	public FieldSet visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public FieldSet inRow(boolean inRow) {
		this.inRow = inRow;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Object[] getMessageArgs() {
		return messageArgs;
	}

	public boolean isVisible() {
		if (visible) {
			for (InputField<?> inputField : inputFields) {
				if (inputField.isVisible()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInRow() {
		return inRow;
	}

	public List<InputField<?>> getInputFields() {
		return inputFields;
	}

}
