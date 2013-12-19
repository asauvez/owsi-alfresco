package fr.openwide.alfresco.component.query.form.field;

import java.util.ArrayList;
import java.util.List;

public final class InputFieldBuilder {

	private final List<FieldSet> fieldSets = new ArrayList<>();

	public FieldSet newFieldSet() {
		FieldSet fieldSet = new FieldSet(this);
		fieldSets.add(fieldSet);
		return fieldSet;
	}

	public List<FieldSet> getFieldSets() {
		return fieldSets;
	}
}