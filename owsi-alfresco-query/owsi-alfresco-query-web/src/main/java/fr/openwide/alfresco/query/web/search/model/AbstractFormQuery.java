package fr.openwide.alfresco.query.web.search.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import fr.openwide.alfresco.query.web.form.field.FieldSet;
import fr.openwide.alfresco.query.web.form.field.InputField;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;

public abstract class AbstractFormQuery<N> implements FormQuery {

	private String beanName = "formQuery";

	private final Map<String, InputField<?>> inputFields = new LinkedHashMap<String, InputField<?>>();

	private final List<FieldSet> fieldSets = new ArrayList<FieldSet>();

	private Function<Object, String> defaultResultFormatter = Functions.toStringFunction();

	private FormQueryResult<N> result;

	@Override
	public void initFields() {
		// to override
	}

	@Override
	public void doQuery() {
		result = computeResults();
	}

	protected abstract FormQueryResult<N> computeResults();

	public Collection<InputField<?>> getInputFields() {
		return Collections.unmodifiableCollection(inputFields.values());
	}
	public Map<String, InputField<?>> getInputFieldsMap() {
		return Collections.unmodifiableMap(inputFields);
	}

	protected <F extends InputField<?>> F add(F inputField) {
		inputFields.put(inputField.getName(), inputField);
		return inputField;
	}

	public FieldSet add(FieldSet fieldSet) {
		fieldSets.add(fieldSet);
		return fieldSet;
	}

	public List<FieldSet> getFieldSets() {
		return fieldSets;
	}

	public void initValues(Map<String, String[]> params) {
		initFields();
		FieldSet unaffectedFieldSet = new FieldSet(); 
		unaffectedFieldSet.getInputFields().addAll(getInputFields());
		for (FieldSet fieldSet : fieldSets) {
			unaffectedFieldSet.getInputFields().removeAll(fieldSet.getInputFields());
		}
		fieldSets.add(0, unaffectedFieldSet);
		
		for (InputField<?> inputField : getInputFields()) {
			inputField.init(params);
		}
	}

	public Function<Object, String> getDefaultResultFormatter() {
		return defaultResultFormatter;
	}
	public void setDefaultResultFormatter(Function<Object, String> defaultResultFormatter) {
		this.defaultResultFormatter = defaultResultFormatter;
	}

	public String getMessageKeyPrefix() {
		return getClass().getName();
	}

	@Override
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public FormQueryResult<N> getResult() {
		return result;
	}
}
