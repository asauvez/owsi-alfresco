package fr.openwide.alfresco.query.web.search.context;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class QueryContext {

	private Locale locale;
	private MessageSource messageSource;

	public QueryContext(Locale locale, MessageSource messageSource) {
		this.locale = locale;
		this.messageSource = messageSource;
	}

	public String getMessage(String i18nKey, Object ... args) {
		return messageSource.getMessage(i18nKey, args, locale);
	}

	public Locale getLocale() {
		return locale;
	}

}
