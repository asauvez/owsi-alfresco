package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class NameReferenceFormatter implements Formatter<NameReference> {

	@Override
	public NameReference parse(String text, Locale locale) throws ParseException {
		return NameReference.create(text);
	}

	@Override
	public String print(NameReference object, Locale locale) {
		return object.getFullName();
	}

}
