package fr.openwide.alfresco.component.query.search.model;

import org.springframework.validation.Errors;

import fr.openwide.alfresco.component.query.form.field.FieldSet;
import fr.openwide.alfresco.component.query.form.field.InputField;
import fr.openwide.alfresco.component.query.form.field.InputFieldBuilder;
import fr.openwide.alfresco.component.query.form.result.FormQueryResult;
import fr.openwide.alfresco.component.query.form.util.MessageUtils;

public abstract class AbstractFormQuery<I> {

	private PaginationParams pagination = new PaginationParams();
	private InputFieldBuilder inputFieldBuilder = new InputFieldBuilder();

	public AbstractFormQuery() {
		initFields(inputFieldBuilder);

		for (FieldSet fieldSet : inputFieldBuilder.getFieldSets()) {
			for (InputField<?> inputField : fieldSet.getInputFields()) {
				if (inputField.getLabel() == null) {
					inputField.setLabel(MessageUtils.codes(
						this.getClass().getName() + "." + inputField.getName(),
						this.getClass().getSimpleName() + "." + inputField.getName(),
						inputField.getName()));
				}
			}
		}
	}

	public void initFields(InputFieldBuilder builder) {
		// to override
	}

	public InputFieldBuilder getInputFieldBuilder() {
		return inputFieldBuilder;
	}

	public PaginationParams getPagination() {
		return pagination;
	}

	public boolean filterResult(I item) {
		// to override
		return true;
	}

	public void initResult(FormQueryResult<I> result) {
		// to override
	}

	public void validate(Errors errors) {
		// to override
	}
}
