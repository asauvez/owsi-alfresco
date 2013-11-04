package fr.openwide.alfresco.query.web.form.field;

import java.util.Map;

import fr.openwide.alfresco.query.web.form.view.InputFieldView;
import fr.openwide.alfresco.query.web.search.model.FormQuery;

public abstract class InputField<T> implements InputFieldView {

	private final FormQuery formQuery;
	private final String name;

	private String message;
	private String description;
	private boolean visible = true;
	private boolean mandatory = false;
	private int rowSpan = 4;
	private int rowOffset = 0;
	private T value;
	private T defaultValue;

	public InputField(FormQuery formQuery, String name) {
		this.formQuery = formQuery;
		this.name = name;
	}

	public InputField<T> defaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public void init(Map<String, String[]> params) {
		setValue(parse(params));
		if (getValue() == null && defaultValue != null) {
			setValue(defaultValue);
		}
	}

	protected T parse(Map<String, String[]> params) {
		String[] values = params.get(getName());
		return (values != null) 
				? (values.length == 0) ? parseNullable(null) : parseNullable(values[0])
				: parseNullable(null);
	}
	protected T parseNullable(String value) {
		return (value != null) ? parseNotNull(value) : null;
	}

	protected abstract T parseNotNull(String value);

	public InputField<T> message(String message) {
		this.message = message;
		return this;
	}
	public InputField<T> description(String description) {
		this.description = description;
		return this;
	}

	public InputField<T> visible(boolean visible) {
		this.visible = visible;
		return this;
	}
	public InputField<T> mandatory(boolean mandatory) {
		this.mandatory = mandatory;
		return this;
	}

	public InputField<T> rowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
		return this;
	}
	public InputField<T> rowOffset(int rowOffset) {
		this.rowOffset = rowOffset;
		return this;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return "inputFieldsMap[" + getName() + "].value";
	}
	public String getFullPath() {
		return formQuery.getBeanName() + "." + getPath();
	}

	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getFullName() {
		return formQuery.getClass().getName() + "." + getName();
	}

	public String getDescription() {
		return description;
	}

	public boolean isVisible() {
		return visible;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public String getRowSpan() {
		return "span" + rowSpan;
	}
	public String getRowOffset() {
		return (rowOffset != 0) ? " offset" + rowOffset : "";
	}

}
