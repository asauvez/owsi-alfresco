package fr.openwide.alfresco.query.web.form.field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.openwide.alfresco.query.web.form.view.TextInputFieldView;
import fr.openwide.alfresco.query.web.search.model.FormQuery;

public class DateInputField extends InputField<Date> implements TextInputFieldView {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public DateInputField(FormQuery formQuery, String name) {
		super(formQuery, name);
	}

	@Override
	public String getView() {
		return TextInputFieldView.TEXT;
	}

	@Override
	public String getPlaceholder() {
		return DATE_FORMAT.format(new Date());
	}

	@Override
	protected Date parseNotNull(String value) {
		try {
			return (value.length() > 0) ? DATE_FORMAT.parse(value) : null;
		} catch (ParseException e) {
			//throw new ValidationException("error.date.format", DATE_FORMAT.format(new Date()));
			return new Date(); // TODO
		}
	}

}
