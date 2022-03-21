package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;

public class StoreReferenceFormatter implements Formatter<StoreReference> {

	@Override
	public StoreReference parse(String text, Locale locale) throws ParseException {
		return StoreReference.create(text);
	}

	@Override
	public String print(StoreReference object, Locale locale) {
		return object.getReference();
	}

}
